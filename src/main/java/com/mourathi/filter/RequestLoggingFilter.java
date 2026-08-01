package com.mourathi.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Extract the original IP address (handles Docker/Proxy routing)
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        } else {
            // If multiple proxies exist, the first IP is the true client
            clientIp = clientIp.split(",")[0].trim();
        }

        // 2. Extract request details
        String method = request.getMethod();
        String url = request.getRequestURI();
        String queryString = request.getQueryString();

        String fullUrl = (queryString != null) ? url + "?" + queryString : url;

        // 3. Log the incoming request
        log.info("Incoming Request | IP: {} | {} {}", clientIp, method, fullUrl);

        // 4. Continue the filter chain so the request reaches your controllers
        filterChain.doFilter(request, response);

    }
}

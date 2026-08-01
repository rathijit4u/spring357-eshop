package com.mourathi.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.warn(
                "Unauthorized request: method={}, uri={}, ip={}, error={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr(),
                authException.getMessage()
        );

        response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized"
        );
    }
}

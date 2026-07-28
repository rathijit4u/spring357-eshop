package com.mourathi.aop;

import com.mourathi.config.security.CustomUserDetails;
import com.mourathi.exception.DuplicateResourceException;
import com.mourathi.exception.ResourceNotFoundException;
import net.changeshield.aop.TestCoverageAspect;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Aspect
@Component
public class TestAspect extends TestCoverageAspect {

    @Pointcut("within(com.mourathi..*) && !within(com.mourathi.filter..*) && !within(com.mourathi.config..*)")
    public void applicationPackagePointcut() {}

    @Override
    public boolean isUnexpectedException(Throwable ex) {
        List<Class<?>> expectedExceptions = Arrays.asList(DuplicateResourceException.class,
                ResourceNotFoundException.class, AccessDeniedException.class);

        return !expectedExceptions.contains(ex.getClass());
    }

    @Override
    public Object httpServletRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        return attrs.getRequest();
    }

    @Override
    public Set<String> getUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Set.of();
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return Set.of(customUserDetails.getUsername());
//        return authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toSet());
    }
}

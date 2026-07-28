package com.mourathi.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

//@Aspect
//@Component
public class PreAuthorizeLoggingAspect {

    @After("@annotation(org.springframework.security.access.prepost.PreAuthorize)")
    public void logAuthorityCheck(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
        if (preAuthorize != null) {
            String expression = preAuthorize.value(); // e.g. "hasAuthority('CART_CREATE')"
            System.out.printf("Method %s.%s requires: %s%n",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    method.getName(),
                    expression);
        }
    }
}

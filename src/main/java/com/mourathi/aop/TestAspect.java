package com.mourathi.aop;

import com.mourathi.exception.DuplicateResourceException;
import net.changeshield.aop.TestCoverageAspect;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Set;

@Aspect
@Component
public class TestAspect extends TestCoverageAspect {

    @Pointcut("within(com.example.crud..*)")
    public void applicationPackagePointcut() {}

    @Override
    public boolean isUnexpectedException(Throwable ex) {
        return ex.getClass() != DuplicateResourceException.class;
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
        return Set.of();
    }
}

# Spring Boot CRUD Application - eShop

## Prerequisites
### Add following Environment variables
```
APP_SERVER_PORT=<port_number>

REDIS_HOST=<server_address>
REDIS_PORT=<port_number>
REDIS_FOLDER=<high_level_group>

POSTGRES_SERVER_URL=jdbc:postgresql://<server_address>:<port_number>/<db_name>
POSTGRES_USER_NAME=<>
POSTGRES_PASSWORD=<>
```

### Swagger UI Endpoint
[Swagger UI](http://<server_url>:<server_port>/swagger-ui/index.html)

### Build a fat jar for docker
```shell
mvn clean package -DskipTests
```

### Docker
```azure
docker compose up -d --build
```

## Setup AOP 
Add a class extending net.changeshield.aop.TestCoverageAspect and implement/override following methods
Only track business logic in AOP. Custom spring filters & security configs might interfere with AOP.
If you face any error them exclude them.

- public void applicationPackagePointcut(){}
- public boolean isUnexpectedException(Throwable ex){}
- public Object httpServletRequest(){}
- public Set<String> getUserRoles(){}

#### For example, 'TestAspect' class has been created for this purpose. 

```java
@Aspect
@Component
public class TestAspect extends TestCoverageAspect {

    @Pointcut("within(com.mourathi..*) && !within(com.mourathi.filter..*) && !within(com.mourathi.config..*)")
    public void applicationPackagePointcut() {
    }

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
    }
}
```
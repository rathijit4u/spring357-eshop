package com.mourathi.controller;


import com.mourathi.dto.ApiResponse;
import com.mourathi.dto.LoginRequest;
import com.mourathi.dto.RegisterRequest;
import com.mourathi.entity.Role;
import com.mourathi.entity.RoleEntity;
import com.mourathi.entity.User;
import com.mourathi.repository.RoleRepository;
import com.mourathi.repository.UserRepository;
import com.mourathi.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(RoleRepository roleRepository, UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Username already taken"));
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email already in use"));
        }

        RoleEntity customerRole = roleRepository.findByName(Role.ROLE_CUSTOMER)
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setName(Role.ROLE_CUSTOMER);
                    return roleRepository.save(role);
                });

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRoles(Set.of(customerRole));
        userService.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            // Store authentication in a new session — equivalent to what form login does internally
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful"
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {


        new SecurityContextLogoutHandler()
                .logout(request, response, authentication);

        return ResponseEntity.ok(
                ApiResponse.success("Logged out successfully"));
    }

}

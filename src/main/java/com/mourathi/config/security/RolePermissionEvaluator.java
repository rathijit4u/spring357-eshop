package com.mourathi.config.security;

import com.mourathi.entity.User;
import com.mourathi.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class RolePermissionEvaluator {
    private final UserRepository userRepository;

    public RolePermissionEvaluator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                                ||
                                authority.getAuthority().equals(role)
                );
    }

    public boolean isAdminOrSameUser(Authentication authentication, Long userId) {
        if (this.hasRole(authentication, "ROLE_ADMIN")) {
            return true;
        }

        User user = userRepository.findById(userId)
                .orElseGet(() -> null);
        if (user == null) {
            return false;
        }
        return user.getUsername().equals(authentication.getName());
    }
}

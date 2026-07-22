package com.mourathi.dto;

import com.mourathi.entity.RoleEntity;
import com.mourathi.entity.User;
import com.mourathi.entity.UserStatus;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class AdminUserResponse extends UserResponse {

    private final UserStatus status;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    private Set<String> roles;

    public AdminUserResponse(User user) {
        super(user);
        this.status = user.getStatus();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.roles = user.getRoles().stream().map(
                roleEntity -> roleEntity.getName().name()
                    ).collect(Collectors.toSet());
    }

    public static AdminUserResponse from(User user){
        return new AdminUserResponse(user);
    }

    public UserStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Set<String> getRoles() {
        return roles;
    }
}

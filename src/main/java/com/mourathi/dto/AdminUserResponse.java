package com.mourathi.dto;

import com.mourathi.entity.User;
import com.mourathi.entity.UserStatus;
import java.time.LocalDateTime;

public class AdminUserResponse extends UserResponse {

    private final UserStatus status;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    public AdminUserResponse(User user) {
        super(user);
        this.status = user.getStatus();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

    public static AdminUserResponse from(User user){
        return new AdminUserResponse(user);
    }
}

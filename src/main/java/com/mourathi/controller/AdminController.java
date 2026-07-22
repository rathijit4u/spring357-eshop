package com.mourathi.controller;

import com.mourathi.dto.AdminUserResponse;
import com.mourathi.dto.ApiResponse;
import com.mourathi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getUsers() {
        List<AdminUserResponse> responses = userService.getAdminUsers();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{id}")
    public AdminUserResponse  getUser(@PathVariable Long id) {
        return userService.getAdminUser(id);
    }

    @PostMapping("/{id}/disable")
    public void disableUser(@PathVariable Long id) {
        userService.disable(id);
    }

    @PostMapping("/{id}/enable")
    public void enableUser(@PathVariable Long id) {
        userService.enable(id);
    }

}

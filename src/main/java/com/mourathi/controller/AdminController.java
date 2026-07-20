package com.mourathi.controller;

import com.mourathi.dto.AdminUserResponse;
import com.mourathi.service.UserService;
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
    public List<AdminUserResponse>  getUsers() {
        return userService.getAdminUsers();
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

//    @PostMapping("/{id}/role/{role}")
//    public void assignRoleToUser(@PathVariable Long id, String role) {
//        userService.addRole(id, role);
//    }
}

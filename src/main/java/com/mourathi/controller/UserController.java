package com.mourathi.controller;

import com.mourathi.dto.*;
import com.mourathi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserByEmail(email)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("User updated successfully", userService.updateUser(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    @PostMapping("/{id}/roles")
    public void assignRoleToUser(@PathVariable Long id, @RequestBody UserRoleRequest request) {
        if(request.operationType() == OperationType.ADD) {
            userService.addRoleToUser(id, new HashSet<>(request.roles()));
        } else if(request.operationType() == OperationType.REMOVE) {
            userService.removeRoleFromUser(id, new HashSet<>(request.roles()));
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

    }

}

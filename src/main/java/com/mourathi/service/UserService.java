package com.mourathi.service;

import com.mourathi.dto.AdminUserResponse;
import com.mourathi.dto.UserRequest;
import com.mourathi.dto.UserResponse;
import com.mourathi.entity.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserById(Long id);
    UserResponse getUserByUserName(String userName);
    UserResponse getUserByEmail(String email);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long id, UserRequest request);
    void save(User user);
    void deleteUser(Long id);
    void enable(Long id);
    void disable(Long id);
    List<AdminUserResponse> getAdminUsers();
    AdminUserResponse getAdminUser(Long id);
    void addRoleToUser(Long id, Set<String> roles);
    void removeRoleFromUser(Long id, Set<String> roles);
}

package com.mourathi.service;

import com.mourathi.dto.AdminUserResponse;
import com.mourathi.dto.UserDto;
import com.mourathi.dto.UserResponse;
import com.mourathi.entity.User;

import java.util.List;

public interface UserService {
    UserDto.Response createUser(UserDto.Request request);
    UserDto.Response getUserById(Long id);
    UserDto.Response getUserByUserName(String userName);
    UserDto.Response getUserByEmail(String email);
    List<UserDto.Response> getAllUsers();
    UserDto.Response updateUser(Long id, UserDto.Request request);
    void save(User user);
    void deleteUser(Long id);
    void enable(Long id);
    void disable(Long id);
    List<AdminUserResponse> getAdminUsers();
    AdminUserResponse getAdminUser(Long id);
}

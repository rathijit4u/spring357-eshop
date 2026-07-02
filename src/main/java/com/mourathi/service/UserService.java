package com.mourathi.service;

import com.mourathi.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto.Response createUser(UserDto.Request request);
    UserDto.Response getUserById(Long id);
    UserDto.Response getUserByEmail(String email);
    List<UserDto.Response> getAllUsers();
    UserDto.Response updateUser(Long id, UserDto.Request request);
    void deleteUser(Long id);
}

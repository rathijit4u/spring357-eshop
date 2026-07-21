package com.mourathi.service.impl;

import com.mourathi.dto.AdminUserResponse;
import com.mourathi.dto.UserDto;
import com.mourathi.dto.UserResponse;
import com.mourathi.entity.User;
import com.mourathi.entity.UserStatus;
import com.mourathi.exception.DuplicateResourceException;
import com.mourathi.exception.ResourceNotFoundException;
import com.mourathi.repository.UserRepository;
import com.mourathi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto.Response createUser(UserDto.Request request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User with email '" + request.getEmail() + "' already exists");
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();
        return mapToResponse(userRepository.save(user));
    }

    @Override
    public void save(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("User with email '" + user.getEmail() + "' already exists");
        }
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto.Response getUserById(Long id) {
        return mapToResponse(findUserOrThrow(id));
    }

    @Override
    public UserDto.Response getUserByUserName(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new ResourceNotFoundException("No user with username '%s' found".formatted(userName)));
        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto.Response getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto.Response> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto.Response updateUser(Long id, UserDto.Request request) {
        User user = findUserOrThrow(id);
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already in use");
        }
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        return mapToResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void disable(Long id) {
        User existing = userRepository.getReferenceById(id);
        if (existing.getStatus() == UserStatus.ACTIVE) {
            existing.setStatus(UserStatus.SUSPENDED);
            userRepository.save(existing);
        }
    }

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void enable(Long id) {
        User existing = userRepository.getReferenceById(id);
        if (existing.getStatus() == UserStatus.SUSPENDED) {
            existing.setStatus(UserStatus.ACTIVE);
            userRepository.save(existing);
        }
    }

    public List<AdminUserResponse> getAdminUsers() {
        return userRepository.findAll()
                .stream()
                .map(AdminUserResponse::from)
                .collect(Collectors.toList());
    }

    public AdminUserResponse getAdminUser(Long id) {
        return AdminUserResponse.from(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserDto.Response mapToResponse(User user) {
        return UserDto.Response.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

//    private UserResponse mapToUserResponse(User user) {
//        return UserResponse.builder()
//                .id(user.getId())
//                .name(user.getFirstName() + " " + user.getLastName())
//                .email(user.getEmail())
//                .username(user.getUsername())
//                .build();
//    }

}

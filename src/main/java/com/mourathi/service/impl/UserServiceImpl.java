package com.mourathi.service.impl;

import com.mourathi.dto.AdminUserResponse;
import com.mourathi.dto.UserRequest;
import com.mourathi.dto.UserResponse;
import com.mourathi.entity.Role;
import com.mourathi.entity.RoleEntity;
import com.mourathi.entity.User;
import com.mourathi.entity.UserStatus;
import com.mourathi.exception.DuplicateResourceException;
import com.mourathi.exception.ResourceNotFoundException;
import com.mourathi.repository.RoleRepository;
import com.mourathi.repository.UserRepository;
import com.mourathi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User with email '" + request.getEmail() + "' already exists");
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .status(UserStatus.ACTIVE)
                .password(passwordEncoder.encode(request.getPassword()))
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
    public UserResponse getUserById(Long id) {
        return mapToResponse(findUserOrThrow(id));
    }

    @Override
    public UserResponse getUserByUserName(String userName) {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new ResourceNotFoundException("No user with username '%s' found".formatted(userName)));
        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
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

    public void disable(Long id) {
        User existing = userRepository.getReferenceById(id);
        if (existing.getStatus() == UserStatus.ACTIVE) {
            existing.setStatus(UserStatus.SUSPENDED);
            userRepository.save(existing);
        }
    }

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

    @Override
    public void addRoleToUser(Long id, Set<String> roles) {
        try{

            Set<RoleEntity> roleEntities = roles.stream().distinct()
                    .map(role -> roleRepository.findByName(Role.valueOf(role))
                            .orElseThrow(() -> new ResourceNotFoundException("Role " + role + " not found"))
                    ).collect(Collectors.toSet());
            User user = findUserOrThrow(id);
            user.getRoles().addAll(roleEntities);
            userRepository.save(user);
        } catch (IllegalArgumentException ex){
            throw new ResourceNotFoundException("Role not found with names " + roles);
        }
    }

    @Override
    public void removeRoleFromUser(Long id, Set<String> roles) {
        try{
            Set<RoleEntity> roleEntities = roles.stream().distinct()
                    .map(role -> roleRepository.findByName(Role.valueOf(role))
                            .orElseThrow(() -> new ResourceNotFoundException("Role " + role + " not found"))
                    ).collect(Collectors.toSet());            User user = findUserOrThrow(id);
            user.getRoles().removeAll(roleEntities);
            userRepository.save(user);
        } catch (IllegalArgumentException ex){
            throw new ResourceNotFoundException("Role not found with names " + roles);
        }
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.from(user);
    }

}

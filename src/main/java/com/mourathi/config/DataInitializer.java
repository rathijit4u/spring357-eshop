package com.mourathi.config;

import com.mourathi.entity.Role;
import com.mourathi.entity.RoleEntity;
import com.mourathi.entity.User;
import com.mourathi.repository.RoleRepository;
import com.mourathi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component
public class DataInitializer implements ApplicationRunner {
    private final static Logger log = LoggerFactory
            .getLogger(DataInitializer.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.firstname}")
    private String adminFirstName;

    @Value("${app.admin.lastname}")
    private String adminLastName;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.email}")
    private String adminEmail;

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByUsername(adminUsername)) {
            log.info("Admin user '{}' already exists, skipping creation", adminUsername);
            return;
        }

        RoleEntity adminRole = roleRepository.findByName(Role.ROLE_ADMIN)
                        .orElseGet(() -> {
                            RoleEntity role = new RoleEntity();
                            role.setName(Role.ROLE_ADMIN);
                            return roleRepository.save(role);
                        });

        RoleEntity customerRole = roleRepository.findByName(Role.ROLE_CUSTOMER)
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setName(Role.ROLE_CUSTOMER);
                    return roleRepository.save(role);
                });

        RoleEntity productManagerRole = roleRepository.findByName(Role.ROLE_PRODUCT_MANAGER)
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setName(Role.ROLE_PRODUCT_MANAGER);
                    return roleRepository.save(role);
                });

        RoleEntity financeManagerRole = roleRepository.findByName(Role.ROLE_FINANCE_MANAGER)
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setName(Role.ROLE_FINANCE_MANAGER);
                    return roleRepository.save(role);
                });

        RoleEntity orderManagerRole = roleRepository.findByName(Role.ROLE_ORDER_MANAGER)
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setName(Role.ROLE_ORDER_MANAGER);
                    return roleRepository.save(role);
                });

        RoleEntity inventoryManagerRole = roleRepository.findByName(Role.ROLE_INVENTORY_MANAGER)
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setName(Role.ROLE_INVENTORY_MANAGER);
                    return roleRepository.save(role);
                });

        RoleEntity supportAgentRole = roleRepository.findByName(Role.ROLE_SUPPORT_AGENT)
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setName(Role.ROLE_SUPPORT_AGENT);
                    return roleRepository.save(role);
                });

        User admin = new User();
        admin.setFirstName(adminFirstName);
        admin.setLastName(adminLastName);
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setEmail(adminEmail);

        admin.setRoles(Set.of(adminRole));

        userRepository.save(admin);
        log.info("Admin user '{}' created", adminUsername);
    }
}

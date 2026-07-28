package com.mourathi.config.security;

import com.mourathi.entity.Role;
import com.mourathi.entity.RoleEntity;
import com.mourathi.entity.User;
import com.mourathi.entity.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<GrantedAuthority> authorities = new HashSet<>();

        Set<RoleEntity> roles = user.getRoles();

        for(RoleEntity role :roles){
            authorities.add(
                    new SimpleGrantedAuthority(role.getName().name())
            );

            role.getName().getPermissions()
                    .forEach(permission ->
                            authorities.add(
                                    new SimpleGrantedAuthority(
                                            permission.name()
                                    )
                            )
                    );
        }

        return authorities;
    }


    @Override
    public String getPassword() {
        return user.getPassword();
    }


    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == UserStatus.ACTIVE;
    }


    public Long getUserId() {
        return user.getId();
    }


    public User getUser() {
        return user;
    }


    public boolean hasRole(Role role) {
        return user.getRoles().contains(new RoleEntity(role));
    }
}

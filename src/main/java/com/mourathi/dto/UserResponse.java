package com.mourathi.dto;

import com.mourathi.entity.User;


public class UserResponse {

    private final Long id;
    private final String name;
    private final String username;
    private final String email;

    protected UserResponse(User user) {
        this.id       = user.getId();
        this.name     = user.getFirstName() + " " + user.getLastName();
        this.username = user.getUsername();
        this.email    = user.getEmail();
    }

    public static UserResponse from(User user) {
        return new UserResponse(user);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}

package com.mourathi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 250)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 250)
    private String lastName;

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s]).{8,64}$",
            message = "Password must be 8-64 characters with uppercase, lowercase, a digit and a special character"
    )
    private String password;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone must be a valid 10-digit number")
    private String phone;

    public RegisterRequest() {
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}

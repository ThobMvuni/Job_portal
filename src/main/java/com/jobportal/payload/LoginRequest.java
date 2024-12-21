package com.jobportal.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;

public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    @Email
    private String email;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

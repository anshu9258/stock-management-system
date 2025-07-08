package com.niet.stockmanagement.model;

public class User {

    private String username;
    private String password;
    private String role;

    public User() {
        // Default constructor for Spring form binding
    }

    public User(String username, String password, String role) {
        this.username = username != null ? username.trim() : null;
        this.password = password != null ? password.trim() : null;
        this.role = role != null ? role.trim().toUpperCase() : "USER";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username != null ? username.trim() : null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password != null ? password.trim() : null;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role != null ? role.trim().toUpperCase() : "USER";
    }
}

package com.example.memorizor.Model;

public class User {
    private String email;
    private String userId;
    private String name;
    private String username;
    private String profileImageUrl;

    public User() {
    }

    public User(String email, String userId, String name, String username, String profileImageUrl) {
        this.email = email;
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return userId;
    }

    public void setId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}

package com.example.memorizor.Model;

public class User {
    private String email;
    private String userId;
    private String name;
    private String permissions;
    private String profileImageUrl;

    public User() {
    }

    public User(String email, String userId, String name, String permissions, String profileImageUrl) {
        this.email = email;
        this.userId = userId;
        this.name = name;
        this.permissions = permissions;
        this.profileImageUrl = profileImageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}

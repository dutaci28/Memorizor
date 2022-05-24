package com.example.memorizor.Model;

public class User {
    private String email;
    private String id;
    private String name;
    private String username;
    private String profileImageUrl;

    public User() {
    }

    public User(String email, String id, String name, String username, String profileImageUrl) {
        this.email = email;
        this.id = id;
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
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

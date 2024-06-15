package com.example.gocart.Model;

public class User {
    private String email;
    private String name;
    private String role;
    private String uid;
    private String unicId;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String name, String role, String uid, String unicId) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.uid = uid;
        this.unicId = unicId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getUid() {
        return uid;
    }

    public String getUnicId() {
        return unicId;
    }
}

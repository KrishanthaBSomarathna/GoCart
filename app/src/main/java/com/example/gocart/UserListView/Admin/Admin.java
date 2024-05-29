package com.example.gocart.UserListView.Admin;

public class Admin {
    private String name;
    private String id; // Assuming there's an ID for each admin

    // No-argument constructor
    public Admin() {
    }

    // Constructor
    public Admin(String name, String id) {
        this.name = name;
        this.id = id;
    }

    // Getter and Setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

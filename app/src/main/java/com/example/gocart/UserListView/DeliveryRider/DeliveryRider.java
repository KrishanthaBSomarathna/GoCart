package com.example.gocart.Model;

public class DeliveryRider {
    private String uid;
    private String name;
    private String email;
    private String role; // Ensure role is "Delivery Rider"
    private String vehicle_type; // Optional if you need to display vehicle type

    // Constructor, getters, and setters
    public DeliveryRider() {
        // Default constructor required for Firebase
    }

    public DeliveryRider(String uid, String name, String email, String role, String vehicle_type) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.role = role;
        this.vehicle_type = vehicle_type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }
}

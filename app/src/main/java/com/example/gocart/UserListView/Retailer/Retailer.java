package com.example.gocart.UserListView.Retailer;

public class Retailer {
    private String district;
    private String division;
    private String email;
    private double latitude;
    private double longitude;
    private String name;
    private String phone;
    private String role;
    private String unicId;

    // Empty constructor needed for Firebase
    public Retailer() {}

    public Retailer(String district, String division, String email, double latitude, double longitude, String name, String phone, String role, String unicId) {
        this.district = district;
        this.division = division;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.unicId = unicId;
    }

    // Getters and setters
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getUnicId() { return unicId; }
    public void setUnicId(String unicId) { this.unicId = unicId; }
}


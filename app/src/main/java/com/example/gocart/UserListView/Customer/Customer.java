package com.example.gocart.UserListView.Customer;

public class Customer {

    private String unicId;
    private String name;

    public Customer() {
        // Default constructor required for Firebase
    }

    public Customer(String unicId, String name) {
        this.unicId = unicId;
        this.name = name;
    }

    public String getUnicId() {
        return unicId;
    }

    public void setUnicId(String unicId) {
        this.unicId = unicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}



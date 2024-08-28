package com.example.gocart.Model;

public class OrderItem {
    private String customerId;

    public OrderItem(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }
}

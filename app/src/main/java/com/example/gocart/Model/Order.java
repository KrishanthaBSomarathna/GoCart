package com.example.gocart.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order {
    public String status;
    public Map<String, Item> items;
    private String orderId;
    private String date;
    private String address;
    private String division;
    private double totalPayment;
    private List<OrderItem> orderItems;  // Represents the list of ordered items (itemId, quantity)

    public Order() {
        // Default constructor required for Firebase
    }

    public Order(String orderId, String date, String address, String division, double totalPayment, List<OrderItem> orderItems) {
        this.orderId = orderId;
        this.date = date;
        this.address = address;
        this.division = division;
        this.totalPayment = totalPayment;
        this.orderItems = orderItems;
    }

    public Order(String orderId, String date, List<OrderItem> orderItems) {
        this.orderId = orderId;
        this.date = date;
        this.orderItems = orderItems;
    }

    // Getters and Setters for each field
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(double totalPayment) {
        this.totalPayment = totalPayment;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}

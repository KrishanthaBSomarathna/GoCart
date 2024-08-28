package com.example.gocart.Model;

public class OrderItem {
    private String itemName;
    private String division;
    private int quantity;
    private double total;
    private double unitPrice;
    private String imageUrl; // Add this if you have images associated with the items

    // Constructor, Getters, and Setters
    public OrderItem(String itemName, String division, int quantity, double total, double unitPrice, String imageUrl) {
        this.itemName = itemName;
        this.division = division;
        this.quantity = quantity;
        this.total = total;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
// Getters and Setters
}
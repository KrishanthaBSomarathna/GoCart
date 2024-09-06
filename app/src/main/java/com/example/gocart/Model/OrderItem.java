package com.example.gocart.Model;

public class OrderItem {
    private String itemName;
    private String division;
    private int quantity;
    private double total;
    private double unitPrice;
    private String imageUrl;  // URL to the image
    private String address;   // Address for the order
    private double totalPayment;
    private  String value;// Total payment for the order
    private String status;

    // Default constructor required for Firebase deserialization
    public OrderItem() {
    }

    // Constructor with all fields
    public OrderItem(String itemName, String division, int quantity, double total, double unitPrice, String imageUrl, String address, double totalPayment,String value, String status) {
        this.itemName = itemName;
        this.division = division;
        this.quantity = quantity;
        this.total = total;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.address = address;
        this.totalPayment = totalPayment;
        this.value = value;

    }

    public OrderItem(String itemName, String division, int quantity, double total, double unitPrice, String imageUrl, String value, String status) {
        this.itemName = itemName;
        this.division = division;
        this.quantity = quantity;
        this.total = total;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.value = value;
        this.status = status;
    }

    // Getters and Setters
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(double totalPayment) {
        this.totalPayment = totalPayment;
    }

    // Additional helper methods (optional)
    public double calculateTotal() {
        return this.quantity * this.unitPrice;
    }

    public String getValue() {
        return value;
    }

    public String getStatus() {
        return status;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

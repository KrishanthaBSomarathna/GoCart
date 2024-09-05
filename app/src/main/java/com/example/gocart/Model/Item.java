package com.example.gocart.Model;

public class Item {
    private String itemId;
    private String imageUrl;
    private String itemName;
    private String price;
    private String userId;
    private String value;
    private String category;
    private boolean bestdeal;
    private String division;
    private String status;  // Added status field
    private int cartQty;
    private int total;
    private int unitPrice;

    // Default constructor required for calls to DataSnapshot.getValue(Item.class)
    public Item() {
    }

    // Constructor for initializing item with essential fields
    public Item(String itemId, String imageUrl, String itemName, String price, String userId, String value, String category, boolean bestdeal, String division, String status, int cartQty, int unitPrice, int total) {
        this.itemId = itemId;
        this.imageUrl = imageUrl;
        this.itemName = itemName;
        this.price = price;
        this.userId = userId;
        this.value = value;
        this.category = category;
        this.bestdeal = bestdeal;
        this.division = division;
        this.status = status;  // Initialize status
        this.cartQty = cartQty;
        this.unitPrice = unitPrice;
        this.total = total;
    }

    // Getters and Setters
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isBestdeal() {
        return bestdeal;
    }

    public void setBestdeal(boolean bestdeal) {
        this.bestdeal = bestdeal;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCartQty() {
        return cartQty;
    }

    public void setCartQty(int cartQty) {
        this.cartQty = cartQty;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }
}

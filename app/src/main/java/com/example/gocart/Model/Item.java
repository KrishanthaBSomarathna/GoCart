package com.example.gocart.Model;

public class Item {
    private String itemId;
    private String imageUrl;
    private String itemName;
    private String price;
    private String quantity;
    private String userId;
    private String value;
    private String category;
    private boolean bestdeal;
    private String division;

    public Item() {}

    public Item(String itemId, String imageUrl, String itemName, String price, String quantity, String userId, String value, String category, boolean bestdeal, String division) {
        this.itemId = itemId;
        this.imageUrl = imageUrl;
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.userId = userId;
        this.value = value;
        this.category = category;
        this.bestdeal = bestdeal;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getItemName() {
        return itemName;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getUserId() {
        return userId;
    }

    public String getValue() {
        return value;
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

}

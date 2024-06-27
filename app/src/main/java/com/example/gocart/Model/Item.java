package com.example.gocart.Model;

public class Item {
    private String imageUrl;
    private String itemName;
    private String price;
    private String quantity;
    private String userId;
    private String value;

    public Item() {}

    public Item(String imageUrl, String itemName, String price, String quantity, String userId, String value) {
        this.imageUrl = imageUrl;
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.userId = userId;
        this.value = value;
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
}

package com.example.gocart.Model;

public class CartItem {
    private String itemId;
    private String quantity;

    public CartItem() {}

    public CartItem(String itemId, String quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}

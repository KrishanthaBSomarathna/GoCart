package com.example.gocart.Model;
import java.util.Map;

import java.util.List;

public class OrderDetail {
    private String orderId;
    private String address;
    private List<String> itemIds;

    // Default constructor required for calls to DataSnapshot.getValue(OrderDetail.class)
    public OrderDetail() {}

    // Parameterized constructor
    public OrderDetail(String orderId, String address, List<String> itemIds) {
        this.orderId = orderId;
        this.address = address;
        this.itemIds = itemIds;
    }

    // Getters and setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<String> itemIds) {
        this.itemIds = itemIds;
    }
}

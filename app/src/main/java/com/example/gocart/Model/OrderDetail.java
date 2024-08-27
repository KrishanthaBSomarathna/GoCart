package com.example.gocart.Model;
public class OrderDetail {
    private String orderId;
    private String address;
    private int totalPayment;

    // Default constructor required for calls to DataSnapshot.getValue(OrderDetail.class)
    public OrderDetail() {}

    // Parameterized constructor
    public OrderDetail(String orderId, String address, int totalPayment) {
        this.orderId = orderId;
        this.address = address;
        this.totalPayment = totalPayment;
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

    public int getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(int totalPayment) {
        this.totalPayment = totalPayment;
    }
}

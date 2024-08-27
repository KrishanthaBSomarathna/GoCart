package com.example.gocart.Model;

public class OrderData {
    private String orderId;
    private String address;
    private String totalPayment;

    public OrderData(String orderId, String address, String totalPayment) {
        this.orderId = orderId;
        this.address = address;
        this.totalPayment = totalPayment;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getAddress() {
        return address;
    }

    public String getTotalPayment() {
        return totalPayment;
    }
}

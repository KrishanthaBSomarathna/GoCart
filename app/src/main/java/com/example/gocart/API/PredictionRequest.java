package com.example.gocart.API;

public class PredictionRequest {
    private String customer_id;

    public PredictionRequest(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }
}

package com.example.gocart.Model;

public class Shop {
    private String shopId;
    private String name;
    private String phone;
    private String division;

    public Shop(String shopId, String name, String phone, String division) {
        this.shopId = shopId;
        this.name = name;
        this.phone = phone;
        this.division = division;
    }

    public String getShopId() {
        return shopId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getDivision() {
        return division;
    }
}

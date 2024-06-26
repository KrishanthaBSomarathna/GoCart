package com.example.gocart.Model;

public class Item {
    private String id;
    private String name;
    private String size;
    private double price;
    private int imageResId;

    public Item(String id, String name, String size, double price, int imageResId) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.price = price;
        this.imageResId = imageResId;
    }

    // Getters and Setters


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}


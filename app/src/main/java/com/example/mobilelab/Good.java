package com.example.mobilelab;

import com.google.gson.annotations.SerializedName;

public class Good {
    @SerializedName("title")
    private String title;
    @SerializedName("place")
    private String place;
    @SerializedName("date")
    private Long date;
    @SerializedName("price")
    private double price;
    @SerializedName("img")
    private String img;

    String getTitle() {
        return title;
    }

    String getPlace() {
        return place;
    }

    double getPrice() {
        return price;
    }

    String getImg() {
        return img;
    }

    Long getDate() {
        return date;
    }

    public Good(String title, String place, double price, String img, Long date) {
        this.title = title;
        this.place = place;
        this.price = price;
        this.img = img;
        this.date = date;
    }
}

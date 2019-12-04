package com.example.mobilelab;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Good {

    @SerializedName("title")
    private String title;
    @SerializedName("place")
    private String place;
    @SerializedName("date")
    private long date;
    @SerializedName("price")
    private float price;
    @SerializedName("img")
    private String img;

    public Good(String title, String place, float price, String img, long date) {
        this.title = title;
        this.place = place;
        this.price = price;
        this.img = img;
        this.date = date;
    }

    public Good(String title, String place, String date, String price, String img) {
        this.title = title;
        this.place = place;
        this.price = Float.parseFloat(price);
        this.img = img;
        final String pattern = "dd/MM/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
        try {
            Date parsedDate = format.parse(date);
            this.date = Objects.requireNonNull(parsedDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    public String getPlace() {
        return place;
    }

    public String getPrice() {
        return String.format("%s$", price);
    }

    public String getImg() {
        return img;
    }

    public String getDate() {
        int MILLIS = 1000;
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date(this.date * MILLIS);
        return formatter.format(date);
    }
}

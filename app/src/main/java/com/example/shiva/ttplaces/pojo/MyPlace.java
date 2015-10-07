package com.example.shiva.ttplaces.pojo;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by jevon on 29-Sep-15.
 */
public class MyPlace {
    private LatLng position;
    private String name = "";
    private String type = "";
    private String area = "";
    private double rating = 0;

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public MyPlace(String name, String type, String area, LatLng position) {
        this.name = name;
        this.type = type;
        this.position = position;
        this.area = area;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setLatLng(LatLng position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}


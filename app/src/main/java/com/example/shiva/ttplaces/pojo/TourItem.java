package com.example.shiva.ttplaces.pojo;

public class TourItem {
    String name;
    int type;
    public TourItem(String n, int t) {
        name = n;
        type = t;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

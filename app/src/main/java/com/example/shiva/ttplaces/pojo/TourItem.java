package com.example.shiva.ttplaces.pojo;

public class TourItem {
    String name;
    int type;
    String url;
    public TourItem(String n, int t,String u) {
        name = n;
        type = t;
        url=u;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

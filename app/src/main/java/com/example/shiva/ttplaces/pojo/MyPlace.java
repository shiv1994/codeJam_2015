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
    private int recreationVal = 0;
    private int educationalVal = 0;
    private int religiousVal = 0;
    private int remoteVal = 0;
    //private double rating = 0;

    public MyPlace(String name, String type, String area, LatLng position, int recreationVal, int educationalVal, int religiousVal, int remoteVal) {
        this.name = name;
        this.type = type;
        this.area = area;
        this.position = position;
        this.recreationVal = recreationVal;
        this.educationalVal = educationalVal;
        this.religiousVal = religiousVal;
        this.remoteVal = remoteVal;
    }

    public MyPlace(String name, String type, String area, LatLng position){
        this.name=name;
        this.type=type;
        this.area=area;
        this.position=position;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getRecreationVal() {
        return recreationVal;
    }

    public void setRecreationVal(int recreationVal) {
        this.recreationVal = recreationVal;
    }

    public int getEducationalVal() {
        return educationalVal;
    }

    public void setEducationalVal(int educationalVal) {
        this.educationalVal = educationalVal;
    }

    public int getReligiousVal() {
        return religiousVal;
    }

    public void setReligiousVal(int religiousVal) {
        this.religiousVal = religiousVal;
    }

    public int getRemoteVal() {
        return remoteVal;
    }

    public void setRemoteVal(int remoteVal) {
        this.remoteVal = remoteVal;
    }
}

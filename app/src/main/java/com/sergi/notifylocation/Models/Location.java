package com.sergi.notifylocation.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergi on 11/04/2017.
 */

public class Location {

    private double longitude;
    private double latitude;
    private String name;
    private String address;
    private String phone;
    private List<String> comments;
    private ArrayList<String> images;

    public Location() {
    }

    public Location(double longitude, double latitude, String name, String address, String phone, List<String> comments, ArrayList<String> images) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.comments = comments;
        this.images = images;
    }

    public double getLongitude() {
        return longitude;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

package com.sergi.notifylocation.Models;

import java.util.List;

/**
 * Created by Sergi on 28/03/2017.
 */

public class User {
    private String username;
    private String email;
    private int radius;
    private List<String> placesOfInterest;
    private List<String> friends;

    public User() {

    }

    public User(String username, String email, int radius, List<String> placesOfInterest, List<String> friends) {
        this.username = username;
        this.email = email;
        this.radius = radius;
        this.placesOfInterest = placesOfInterest;
        this.friends = friends;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public List<String> getPlacesOfInterest() {
        return placesOfInterest;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public void setPlacesOfInterest(List<String> placesOfInterest) {
        this.placesOfInterest = placesOfInterest;
    }
}

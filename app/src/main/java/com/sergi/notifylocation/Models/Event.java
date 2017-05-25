package com.sergi.notifylocation.Models;

import android.graphics.Bitmap;

/**
 * Created by victorserrate on 16/3/17.
 */

public class Event {

    private Bitmap image;
    private String description;
    private String time;

    public Event(Bitmap image, String place, String description) {
        this.image = image;
        this.description = place;
        this.time = description;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }
}

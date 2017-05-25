package com.sergi.notifylocation.Models;

/**
 * Created by sergi on 20/05/2017.
 */

public class HistoryEntry {

    private String place;
    private String time;

    public HistoryEntry () {
    }

    public HistoryEntry (String place, String time) {
        this.place = place;
        this.time = time;
    }

    public String getPlace() {
        return place;
    }


    public String getTime() {
        return time;
    }
}

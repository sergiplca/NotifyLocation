package com.sergi.notifylocation.Models;

/**
 * Created by victorserrate on 16/3/17.
 */

public class PrincipalComment {

    private String comment;
    private String place;

    public PrincipalComment () {

    }

    public PrincipalComment(String comment, String place) {
        this.comment = comment;
        this.place = place;
    }

    public String getComment() {
        return comment;
    }

    public String getPlace() {
        return place;
    }
}

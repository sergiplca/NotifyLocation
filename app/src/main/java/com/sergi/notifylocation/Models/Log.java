package com.sergi.notifylocation.Models;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Log {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("usuari")
    @Expose
    private String usuari;

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("userTime")
    @Expose
    private String userTime;

    /**
     * No args constructor for use in serialization
     *
     */
    public Log() {
    }

    /**
     *
     * @param id
     * @param location
     * @param usuari
     * @param userTime
     */
    public Log(String id, String usuari, String location, String userTime) {
        super();
        this.id = id;
        this.usuari = usuari;
        this.location = location;
        this.userTime = userTime;
    }

    public String getId() { return id; }

    public void setId() { this.id = id; }

    public String getUsuari() {
        return usuari;
    }

    public void setUsuari(String usuari) {
        this.usuari = usuari;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUserTime() { return userTime; }

    public void setUserTime() { this.userTime = userTime; }

    @Override
    public String toString() {
        return "Log{" +
                "usuari='" + usuari + '\'' +
                ", location='" + location + '\'' +
                ", userTime='" + userTime + '\'' +
                '}';
    }
}
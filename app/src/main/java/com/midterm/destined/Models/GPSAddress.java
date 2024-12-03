package com.midterm.destined.Models;

import java.io.Serializable;

public class GPSAddress implements Serializable {

    private double latitude;
    private double longitude;

    public GPSAddress(){

    }

    public GPSAddress(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
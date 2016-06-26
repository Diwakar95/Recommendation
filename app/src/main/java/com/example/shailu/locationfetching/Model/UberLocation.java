package com.example.shailu.locationfetching.Model;

/**
 * Created by shailu on 5/5/16.
 */
public class UberLocation {
    public float latitude;
    public float longitude;

    public UberLocation(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public UberLocation(double latitude, double longitude) {
        this.latitude = (float) latitude;
        this.longitude = (float) longitude;
    }

    public UberLocation(String latitude, String longitude) {
        this.latitude = Float.parseFloat(latitude);
        this.longitude = Float.parseFloat(longitude);
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}

package com.example.shailu.locationfetching.Adapter;

import android.location.Location;

/**
 * Created by shailu on 5/5/16.
 */
public class UserLocation {
    private static Location location;

    public static Location getLocation() {
        return location;
    }

    public static void setLocation(Location location) {
        UserLocation.location = location;
    }

}

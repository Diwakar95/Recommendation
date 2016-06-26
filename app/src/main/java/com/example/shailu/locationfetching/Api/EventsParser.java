package com.example.shailu.locationfetching.Api;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.example.shailu.locationfetching.Adapter.UserLocation;
import com.example.shailu.locationfetching.Model.Events;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shailu on 5/5/16.
 */
public class EventsParser {

    public static Events parseNormalEventById(JSONObject event, Context context) {

        Events entity = new Events();
        try {
            entity.id = event.getInt("id");
            entity.location_lati = event.getString("location_lati");
            entity.location_long = event.getString("location_long");
            entity.cost =""+event.getInt("cost");
            entity.date = event.getString("date");
            entity.description = event.getString("description");
            entity.event_image = event.optJSONObject("event_image").optString("url");
            entity.nameEvent = event.getString("name");
            try {
                entity.distance = "" + (int) getDistance(UserLocation.getLocation().getLatitude(), UserLocation.getLocation().getLongitude(), Double.valueOf(entity.location_lati), Double.valueOf(entity.location_long), context);
            }catch (Exception e){
                e.printStackTrace();
            }            entity.address = event.getString("name");
            entity.time =event.getString("time");
            entity.venue=event.getString("venue");
            try {
                entity.category = event.optString("category");
                entity.subcategories = event.optString("subcategories");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return entity;
    }

    public static ArrayList<Events> parseGetEventDetail(JSONObject jsonEvents, Context context) {
        ArrayList<Events> mEvents = new ArrayList<Events>();
        try {
                JSONArray events = jsonEvents.getJSONArray("events");
                for (int i = 0; i < events.length(); i++) {
                    try {
                        JSONObject event = events.getJSONObject(i);
                        Events entity = new Events();
                        entity.id = event.getInt("id");
                        entity.location_lati = event.getString("location_lati");
                        entity.location_long = event.getString("location_long");
                        entity.cost =""+event.getInt("cost");
                        entity.date = event.getString("date");
                        entity.description = event.getString("description");
                        try {
                            entity.distance = "" + (int) getDistance(UserLocation.getLocation().getLatitude(), UserLocation.getLocation().getLongitude(), Double.valueOf(entity.location_lati), Double.valueOf(entity.location_long), context);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        entity.event_image = event.optJSONObject("event_image").optString("url");
                        entity.nameEvent = event.getString("name");
                        entity.address = event.getString("name");
                        entity.time =event.getString("time");
                        entity.venue=event.getString("venue");
                        try {
                            entity.category = event.optString("category");
                            entity.subcategories = event.optString("subcategories");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                            mEvents.add(entity);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return mEvents;
    }

    public static double getDistance(double startLati, double startLongi, double goalLati, double goalLongi, Context mContext) {
        Location locationA = new Location("Start");
        locationA.setLatitude(startLati);
        locationA.setLongitude(startLongi);
        Location locationB = new Location("End");
        locationB.setLatitude(goalLati);
        locationB.setLongitude(goalLongi);
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null)
            return (locationA.distanceTo(locationB) / 1000); //in km
        else
            return (0);

    }

}

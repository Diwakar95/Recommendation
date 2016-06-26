package com.example.shailu.locationfetching.Api;

/**
 * Created by shailu on 5/5/16.
 */

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.shailu.locationfetching.Activity.EventDetailsActivity;
import com.example.shailu.locationfetching.Model.Events;
import com.example.shailu.locationfetching.Network.NetworkCallbackEvents;
import com.example.shailu.locationfetching.Network.NetworkManager;
import com.example.shailu.locationfetching.Network.NetworkResponseListener;
import com.example.shailu.locationfetching.Network.URLConstants;

import org.json.JSONObject;

import java.util.ArrayList;





/**
 * Created by girishk on 26/04/15.
 */
public final class EventsAPI {

    public static void getAllEvents(final Context mContext, String date, final NetworkCallbackEvents callback) {
        String requestURL = URLConstants.BASE_URL + URLConstants.EVENTS + URLConstants.RESOURCE_FORMAT + "?date=" + date;

        Log.d("API", ":: request url :: " + requestURL);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(requestURL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                ArrayList<Events> res = EventsParser.parseGetEventDetail(response, mContext);
                callback.onSuccess(res);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.getMessage());
                Toast.makeText(mContext, "Unable to reach our servers. Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
        NetworkManager.getInstance(mContext).getRequestQueue().add(jsonArrayRequest);
    }


    public static void getEventsDetailById(final Context mContext, final int id, final NetworkResponseListener callback) {
//        String requestURL = URLConstants.BASE_URL + URLConstants.EVENTS + "/" + "all" + URLConstants.RESOURCE_FORMAT + "?date=" + id;
        //http://quiet-peak-1766.herokuapp.com/v1/events/all.json?date=2015-07-11

        String requestURL = URLConstants.BASE_URL + URLConstants.EVENTS + "/" + id + URLConstants.RESOURCE_FORMAT;


        Log.d("API", ":: request url :: " + requestURL);

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(requestURL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("API", ":: request url :: " + response);
                Events event = new Events();
                event = EventsParser.parseNormalEventById(response, mContext);
                callback.onSuccess(event);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                EventDetailsActivity.scroll_ll.setVisibility(View.GONE);
                EventDetailsActivity.appBarLayout.setVisibility(View.GONE);
                EventDetailsActivity.no_internet_event.setVisibility(View.VISIBLE);
                EventDetailsActivity.retryEventDetail.setVisibility(View.VISIBLE);
                EventDetailsActivity.no_internet_img.setVisibility(View.VISIBLE);
                EventDetailsActivity.backButtonEvent.setVisibility(View.VISIBLE);
                EventDetailsActivity.eventDetailsLoader.setVisibility(View.GONE);

                Toast.makeText(mContext, "Unable to reach our servers. Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
        NetworkManager.getInstance(mContext).getRequestQueue().add(jsonArrayRequest);
    }
}


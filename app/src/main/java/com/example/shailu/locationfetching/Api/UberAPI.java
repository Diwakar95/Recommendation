package com.example.shailu.locationfetching.Api;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.shailu.locationfetching.Activity.EventDetailsActivity;
import com.example.shailu.locationfetching.Model.UberData;
import com.example.shailu.locationfetching.Model.UberDetailData;
import com.example.shailu.locationfetching.Model.UberLocation;
import com.example.shailu.locationfetching.Network.NetworkManager;
import com.example.shailu.locationfetching.Network.NetworkResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by shailu on 5/5/16.
 */
public class UberAPI {
    public static final String UBER_SERVER_TOKEN = "k1pM9hNE797ATUsx-OhaxA-bDQerEDq0Z90sdrW-";
    public static final String CLIENT_ID = "BfiUBEy6pD6PWj6hYHziVS2lErzvQNU0";
    private static final String TAG = "UberAPI";
    //    public static ArrayList<UberData> uberData = new ArrayList<>();
    public static HashMap<String, Double> perKmCharges = new HashMap<>();
    public static HashMap<String, Integer> pickupTimeById = new HashMap<>();

//    public static ArrayList<UberDetailData> uberDetailData = new ArrayList<>();

    public static void getUberRideTime(final Context context, float latitude, float longitude, final NetworkResponseListener callback) {

        pickupTimeById.clear();
        String requestURL = "https://api.uber.com/v1/estimates/time?";

        //Log.e(TAG, "requestUrl " + requestURL);

        final ArrayList<UberData> uberData = new ArrayList<>();
        requestURL += "start_latitude=" + latitude;
        requestURL += "&start_longitude=" + longitude;
        requestURL += "&server_token=" + UBER_SERVER_TOKEN;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
//                ////Log.d("UberAPI", "" + jsonObject);

                JSONArray jsonArray = null;
                try {
                    jsonArray = jsonObject.getJSONArray("times");
                } catch (JSONException e) {
//                    e.printStackTrace();
                }
                if (jsonArray != null)
                    for (int i = 0; i < jsonArray.length(); i++) {
                        UberData u = new UberData();
                        JSONObject jsonObject1 = null;
                        try {
                            jsonObject1 = jsonArray.getJSONObject(i);
                        } catch (JSONException e) {
//                            e.printStackTrace();
                        }
                        try {
                            u.productId = jsonObject1.getString("product_id");
                            u.carType = jsonObject1.getString("display_name");
                            u.estimatedTime = jsonObject1.getInt("estimate") / 60;
                            pickupTimeById.put(u.productId, new Integer(u.estimatedTime));
                        } catch (JSONException e) {
//                            e.printStackTrace();
                            //Log.e("UberAPI", "error in pickuptime");
                        }
                        uberData.add(u);

                    }
                callback.onSuccess(uberData);

                Set set = pickupTimeById.entrySet();
                // Get an iterator
                Iterator i = set.iterator();
                // Display elements
                while (i.hasNext()) {
                    Map.Entry me = (Map.Entry) i.next();

                    //Log.e("UberAPI", "key-> " + me.getKey());
                    //Log.e("UberAPI", "value-> " + me.getValue());

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //Log.e("uberridetime", "error");
//                if (InfoFragment.uber_progressbar != null && InfoFragment.uber_pickuptime != null) {
//                    InfoFragment.uber_progressbar.setVisibility(View.GONE);
//                    InfoFragment.uber_pickuptime.setVisibility(View.GONE);
//                }
                if (EventDetailsActivity.uber_progressbar != null && EventDetailsActivity.uber_pickuptime != null) {
                    EventDetailsActivity.uber_pickuptime.setVisibility(View.GONE);
                    EventDetailsActivity.uber_progressbar.setVisibility(View.GONE);
                }

            }
        });

        NetworkManager.getInstance(context).getRequestQueue().add(jsonObjectRequest);
    }

    public static UberData getMinimumPickupRide(ArrayList<UberData> uberData) {
        int min = Integer.MAX_VALUE;
        int min_pos = -1;
        for (int i = 0; i < uberData.size(); i++) {
            if (uberData.get(i).getEstimatedTime() < min) {
                min = uberData.get(i).getEstimatedTime();
                min_pos = i;
            }
        }
        if (min_pos == -1)
            return null;
        return uberData.get(min_pos);
    }

    public static void getProductDetails(final Context context, String my_latitude, String my_longitude, final NetworkResponseListener callback) {
        String requestURL = "https://api.uber.com/v1/products?";


        requestURL += "latitude=" + my_latitude;
        requestURL += "&longitude=" + my_longitude;
        requestURL += "&server_token=" + UBER_SERVER_TOKEN;


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                ////Log.d("UberAPI", "success in getbyproductid " + response);
                try {
                    JSONArray products = response.getJSONArray("products");
                    for (int i = 0; i < products.length(); i++) {

                        perKmCharges.put(products.getJSONObject(i).getString("product_id"),
                                (Double) products.getJSONObject(i).getJSONObject("price_details").get("cost_per_distance"));
//                        ////Log.d("UberAPI distance unit", products.getJSONObject(i).getJSONObject("price_details").getString("distance_unit"));
                    }
                } catch (JSONException e) {
//                    e.printStackTrace();
                }

                callback.onSuccess(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("UberAPI", "error in getbyproductid");
            }
        });
        NetworkManager.getInstance(context).getRequestQueue().add(jsonObjectRequest);
    }

    public static void getUberDetailData(final Context context, final UberLocation start, final UberLocation end, final NetworkResponseListener callback) {

        String requestURL = "https://api.uber.com/v1/estimates/price?";
        final JsonObjectRequest[] jsonObjectRequest = new JsonObjectRequest[1];
        requestURL += "start_latitude=" + start.getLatitude();
        requestURL += "&start_longitude=" + start.getLongitude();
        requestURL += "&end_latitude=" + end.getLatitude();
        requestURL += "&end_longitude=" + end.getLongitude();

        requestURL += "&server_token=" + UBER_SERVER_TOKEN;

        final ArrayList<UberDetailData> uberDetailData = new ArrayList<>();


        if (pickupTimeById.isEmpty()) {
            final String finalRequestURL = requestURL;
            getUberRideTime(context, start.getLatitude(), start.getLongitude(), new NetworkResponseListener() {
                @Override
                public void onSuccess(Object response) {
//                    ////Log.d("UberApi", "pickupridetime from uberdetails");


                    jsonObjectRequest[0] = new JsonObjectRequest(finalRequestURL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //Log.e("json price estimate", "Success");
                            JSONArray jsonArray = null;
                            try {
                                jsonArray = response.getJSONArray("prices");
                                for (int i = 0; i < jsonArray.length(); i++) {


//                                    ////Log.d("json price estimate", "name-> " + jsonArray.getJSONObject(i).get("display_name"));
//                                    ////Log.d("json price estimate", "estimate-> " + jsonArray.getJSONObject(i).get("estimate"));
//                                    ////Log.d("json price estimate", "prod_id-> " + jsonArray.getJSONObject(i).get("product_id"));
//                                    ////Log.d("json price estimate", "distance-> " + jsonArray.getJSONObject(i).get("distance"));
//                                    ////Log.d("json price estimate", "duration-> " + jsonArray.getJSONObject(i).get("duration"));
//                                    ////Log.d("json price estimate", "surge-> " + jsonArray.getJSONObject(i).get("surge_multiplier"));


                                    if (pickupTimeById.containsKey(jsonArray.getJSONObject(i).getString("product_id"))) {
                                        UberDetailData u = new UberDetailData();

//                                        //Log.e("UberAPI", "check....");
                                        u.product_id = jsonArray.getJSONObject(i).getString("product_id");
                                        u.display_name = (String) jsonArray.getJSONObject(i).get("display_name");
//                                        u.perKm = perKmCharges.get(u.product_id);
                                        u.perKm = 0;
                                        u.pickupTime = pickupTimeById.get(u.product_id);
                                        u.priceEstimate = (String) jsonArray.getJSONObject(i).get("estimate");
                                        u.surge_multiplier = Float.parseFloat(jsonArray.getJSONObject(i).get("surge_multiplier").toString());

                                        uberDetailData.add(u);
                                    }


                                }
                            } catch (JSONException e) {
//                                e.printStackTrace();
                            }

                            callback.onSuccess(uberDetailData);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Log.e("UberAPI", "json error from new uber price estimates");


                            //Log.e("price estimate", "network failure");
//                            if (InfoFragment.uber_request_loader != null)
//                                InfoFragment.uber_request_loader.setVisibility(View.GONE);
                            if (EventDetailsActivity.uber_request_loader != null) {
                                EventDetailsActivity.uber_request_loader.setVisibility(View.GONE);
                                EventDetailsActivity.uber_ll.setVisibility(View.VISIBLE);
                            }
                            Toast.makeText(context, "No Internet Connection. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    NetworkManager.getInstance(context).getRequestQueue().add(jsonObjectRequest[0]);

                }

                @Override
                public void onError(String response) {

                }
            });
        } else {
            jsonObjectRequest[0] = new JsonObjectRequest(requestURL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //Log.e("json price estimate", "Success");
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = response.getJSONArray("prices");
                        for (int i = 0; i < jsonArray.length(); i++) {


//                            ////Log.d("json price estimate", "name-> " + jsonArray.getJSONObject(i).get("display_name"));
//                            ////Log.d("json price estimate", "estimate-> " + jsonArray.getJSONObject(i).get("estimate"));
//                            ////Log.d("json price estimate", "prod_id-> " + jsonArray.getJSONObject(i).get("product_id"));
//                            ////Log.d("json price estimate", "distance-> " + jsonArray.getJSONObject(i).get("distance"));
//                            ////Log.d("json price estimate", "duration-> " + jsonArray.getJSONObject(i).get("duration"));
//                            ////Log.d("json price estimate", "surge-> " + jsonArray.getJSONObject(i).get("surge_multiplier"));


                            if (pickupTimeById.containsKey(jsonArray.getJSONObject(i).getString("product_id"))) {
                                UberDetailData u = new UberDetailData();

                                //Log.e("UberAPI", "check....");
                                u.product_id = jsonArray.getJSONObject(i).getString("product_id");
                                u.display_name = (String) jsonArray.getJSONObject(i).get("display_name");
//                                u.perKm = perKmCharges.get(u.product_id);
                                u.perKm = 0;
                                u.pickupTime = pickupTimeById.get(u.product_id);
                                u.priceEstimate = (String) jsonArray.getJSONObject(i).get("estimate");
                                u.surge_multiplier = Float.parseFloat(jsonArray.getJSONObject(i).get("surge_multiplier").toString());

                                uberDetailData.add(u);
                            }


                        }
                    } catch (JSONException e) {
//                        e.printStackTrace();
                    }

                    callback.onSuccess(uberDetailData);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Log.e("UberAPI", "json error from new uber price estimates");


                    //Log.e("price estimate", "network failure");
//                    if (InfoFragment.uber_request_loader != null)
//                        InfoFragment.uber_request_loader.setVisibility(View.GONE);
                    if (EventDetailsActivity.uber_request_loader != null) {
                        EventDetailsActivity.uber_request_loader.setVisibility(View.GONE);
                        EventDetailsActivity.uber_ll.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(context, "No Internet Connection. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            });
            NetworkManager.getInstance(context).getRequestQueue().add(jsonObjectRequest[0]);

        }

    }
}

package com.example.shailu.locationfetching.Network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.shailu.locationfetching.Model.BaseResponse;
import com.google.gson.Gson;

import org.apache.http.HttpStatus;

/**
 * Created by shailu on 5/5/16.
 */
public class NetworkManager {
    private static NetworkManager instance;
    private RequestQueue requestQueue;
    private String TAG = NetworkManager.class.getSimpleName();
    // These tags will be used to cancel the requests
    private String tag_json_obj = "jobj_req";
    private static Context ctx;


    public NetworkManager() {
    }

    private NetworkManager(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public static NetworkManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkManager(context);
        }
        return instance;
    }

    private Object onErResponse(VolleyError error, String from) {

	/*
        This method checks Requested url & accordingly binds the error status & error message to that particular response class.
	*/
        Gson gson = new Gson();
        Object object = new Object();

        return object;
    }

    private BaseResponse onError(VolleyError error) {
        BaseResponse baseResponse = new BaseResponse();
        if (error instanceof NoConnectionError) {
            baseResponse.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            baseResponse.setMessage("No Internet Connection !");
        } else if (error instanceof ServerError) {
            baseResponse.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            baseResponse.setMessage("Server Error !");
        } else if (error instanceof AuthFailureError) {
            baseResponse.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            baseResponse.setMessage("Authentication Failure !");
        } else if (error instanceof ParseError) {
            baseResponse.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            baseResponse.setMessage("Opps ! Something went Wrong !");
        } else if (error instanceof NetworkError) {
            baseResponse.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            baseResponse.setMessage("Network Error !");
        } else if (error instanceof TimeoutError) {
            baseResponse.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            baseResponse.setMessage("Connection Timeout !");
        }

        return baseResponse;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}

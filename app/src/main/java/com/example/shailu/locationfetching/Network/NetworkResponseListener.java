package com.example.shailu.locationfetching.Network;

/**
 * Created by shailu on 5/5/16.
 */
public interface NetworkResponseListener {
    void onSuccess(Object response);

    void onError(String response);
}

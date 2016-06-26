package com.example.shailu.locationfetching.Network;

import com.example.shailu.locationfetching.Model.Events;

import java.util.ArrayList;

/**
 * Created by shailu on 5/5/16.
 */
public interface NetworkCallbackEvents {
    void onSuccess(ArrayList<Events> result);

    void onError(String error);
}

package com.example.shailu.locationfetching.Model;

import java.util.ArrayList;

/**
 * Created by shailu on 5/5/16.
 */
public class EventsUpdateEvent {
    public final ArrayList<Events> mEvents;

    public EventsUpdateEvent(ArrayList<Events> events) {
        mEvents = events;
    }
}

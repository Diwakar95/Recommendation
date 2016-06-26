package com.example.shailu.locationfetching.Adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shailu.locationfetching.Activity.EventDetailsActivity;
import com.example.shailu.locationfetching.Activity.MainActivity;
import com.example.shailu.locationfetching.Model.Events;
import com.example.shailu.locationfetching.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by shailu on 5/5/16.
 */
public class EventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static ArrayList<Events> mEvents;
    private static MainActivity mobject;


    public EventsAdapter(ArrayList<Events> events, MainActivity object) {
        mEvents = events;
        mobject = object;
    }

    public void updateList(ArrayList<Events> data) {
        mEvents = data;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        RecyclerView.ViewHolder pvh = null;
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_events, parent, false);
                pvh = new EventsViewHolder(v);
                return pvh;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
            final Events event = mEvents.get(position );
            final EventsViewHolder holder = (EventsViewHolder) viewHolder;
            holder.event_name.setText(event.nameEvent);
            if (!event.cost.equalsIgnoreCase("0"))
                holder.event_cost.setText("\u20B9" + event.cost);
            else
                holder.event_cost.setText("Entry Free");

            if(!event.subcategories.trim().equals("null"))
            {
                holder.subcategory.setVisibility(View.VISIBLE);
                holder.subcategory.setText(event.subcategories);
            }
            else
            {
                holder.subcategory.setVisibility(View.GONE);
            }



            SimpleDateFormat sdf1;
            SimpleDateFormat sdf2;
            SimpleDateFormat sdf3;
            SimpleDateFormat sdf4;
            String date = "";
            String datetime = "";
            try {
                sdf3 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                sdf1 = new SimpleDateFormat("dd MMM");
                sdf2 = new SimpleDateFormat("hh:mm a");
                sdf4 = new SimpleDateFormat("yyyy-MM-dd");
                date = sdf1.format(sdf4.parse(event.date));
                datetime = sdf2.format(sdf3.parse(event.time));
            } catch (Exception e) {

                e.printStackTrace();

            }

            Location location = new Location("");
            location.setLatitude(Double.valueOf(event.location_lati));
            location.setLongitude(Double.valueOf(event.location_long));

            setDistance(holder, location);
            holder.timeTextView.setText(date + ", " + datetime);


            if (event.cost_note != null && !event.cost_note.trim().equals("")) {
                holder.cost_note.setText("  " + event.cost_note + "  ");
                holder.cost_note.setVisibility(View.VISIBLE);
            } else
                holder.cost_note.setVisibility(View.GONE);


            holder.venue.setText(event.venue.toUpperCase());
            if (!event.cost.equalsIgnoreCase("0")) {
                holder.startsAtTextView.setText("Starts at");
                holder.event_cost.setText("\u20B9" + event.cost);
            } else {
                holder.startsAtTextView.setText("Entry");
                holder.event_cost.setText("FREE");
            }
            if (event.event_image != null)
                Glide.with(mobject).load(event.event_image).asBitmap().centerCrop().placeholder(R.drawable.placeholderxx4).into(holder.eventBackground);
//            //Log.e("bari ddd", "" + event.bar_id);
//        }   else if (position == VIEW_TONIGHT) {
//            TonightsEventsTextHolder holder = (TonightsEventsTextHolder) viewHolder;
//            holder.tv.setVisibility(View.VISIBLE);
//        }

    }

    private void  setDistance(EventsViewHolder holder, Location placeLocation) {
        if (holder.distance != null && holder.barDiatanceImageView != null)
            if (UserLocation.getLocation() != null && placeLocation != null) {
                holder.barDiatanceImageView.setVisibility(View.VISIBLE);
                double distance = Math.round(placeLocation.distanceTo(UserLocation.getLocation()) / 1000 * 10.0) / 10.0;
                holder.distance.setText(distance + " " + ((distance < 1.0) ? " km" : " kms"));
            } else {
                holder.barDiatanceImageView.setVisibility(View.INVISIBLE);
                holder.distance.setText("");
            }
    }


    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public static class TonightsEventsTextHolder extends RecyclerView.ViewHolder {
        private final TextView tv;

        TonightsEventsTextHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tonights_events_tv);
        }
    }

    public class EventsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView distance;
        private final TextView startsAtTextView;
        private final ImageView barDiatanceImageView;
        private CardView cv;
        private TextView event_name;
        private TextView subcategory;
        private TextView timeTextView;
        private TextView venue;
        private TextView event_cost, cost_note;
        private ImageView eventBackground;
        private View v;

        EventsViewHolder(View itemView) {
            super(itemView);
            v = itemView;
            itemView.setOnClickListener(this);
            cv = (CardView) itemView.findViewById(R.id.event_card);
            event_name = (TextView) itemView.findViewById(R.id.event_name);
            event_cost = (TextView) itemView.findViewById(R.id.place_card_cost);
            timeTextView = (TextView) itemView.findViewById(R.id.event_time);
            venue = (TextView) itemView.findViewById(R.id.venue);
            distance = (TextView) itemView.findViewById(R.id.bar_distance);
            barDiatanceImageView = (ImageView) itemView.findViewById(R.id.bar_distance_iv);
            eventBackground = (ImageView) itemView.findViewById(R.id.event_card_image);
            cost_note = (TextView) itemView.findViewById(R.id.cost_note);
            startsAtTextView = (TextView) itemView.findViewById(R.id.starts_at_tv);
            subcategory = (TextView) itemView.findViewById(R.id.subcategory);
        }

        @Override
        public void onClick(View v) {
            Context context = itemView.getContext();
            Events event = mEvents.get(getPosition());

            Intent intent = new Intent(context, EventDetailsActivity.class);
            intent.putExtra(EventDetailsActivity.EVENT_ID, event.id);
            intent.putExtra("event_name", event.nameEvent);
            intent.putExtra("venue", event.venue);
            intent.putExtra("address", event.address);
            intent.putExtra("image", event.event_image);
            intent.putExtra("description", event.description);
            intent.putExtra("date", event.date);
            intent.putExtra("cost", event.cost);
            intent.putExtra("time", event.time);
            intent.putExtra("lat", event.location_lati);
            intent.putExtra("lang", event.location_long);
            intent.putExtra("subcategory", event.subcategories);
            context.startActivity(intent);


        }

    }

}

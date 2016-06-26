package com.example.shailu.locationfetching.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shailu.locationfetching.Model.UberDetailData;
import com.example.shailu.locationfetching.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shailu on 5/5/16.
 */
public class UberListAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    List<UberDetailData> data;

    TextView carType, surge, estimatedPrice, perkm, pickupTime;
    ImageView uber_img, surge_icon;
    RadioButton radio_bt;

    RelativeLayout list_rl;
    Context mContext;

    public UberListAdapter(Context context, ArrayList<UberDetailData> mData) {
        mContext = context;
        data = mData;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public UberDetailData getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.list_uber, null);

        carType = (TextView) rowView.findViewById(R.id.carType);
        surge = (TextView) rowView.findViewById(R.id.surge);
        pickupTime = (TextView) rowView.findViewById(R.id.pickupTime);
        perkm = (TextView) rowView.findViewById(R.id.perkm);
        estimatedPrice = (TextView) rowView.findViewById(R.id.estimatedPrice);
        uber_img = (ImageView) rowView.findViewById(R.id.uber_img);
        surge_icon = (ImageView) rowView.findViewById(R.id.surge_icon);
//        list_rl = (RelativeLayout) rowView.findViewById(R.id.list_rl);
//        radio_bt = (RadioButton) rowView.findViewById(R.id.radioButton);

        carType.setText(data.get(position).getDisplay_name());
        if (data.get(position).getSurge_multiplier() == 1.0) {
            surge.setVisibility(View.GONE);
            surge_icon.setVisibility(View.GONE);
        } else
            surge.setText("" + data.get(position).getSurge_multiplier() + "X");

        estimatedPrice.setText("" + data.get(position).getPriceEstimate());
        perkm.setText("(" + estimatedPrice.getText().toString().charAt(0) + data.get(position).getPerKm() + "/km)");
        pickupTime.setText("in " + data.get(position).getPickupTime() + " min");

        String img_type = carType.getText().toString().toLowerCase();

        if (img_type.equalsIgnoreCase("uberx")) {
            uber_img.setImageResource(R.drawable.uberx);
        } else if (img_type.equalsIgnoreCase("ubersuv")) {
            uber_img.setImageResource(R.drawable.ubersuv);
        } else {
            uber_img.setImageResource(R.drawable.ubergo);
        }


        return rowView;
    }

}



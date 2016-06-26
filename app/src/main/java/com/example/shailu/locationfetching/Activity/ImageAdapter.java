package com.example.shailu.locationfetching.Activity;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shailu.locationfetching.R;

import java.util.ArrayList;

import static com.example.shailu.locationfetching.Activity.IndexActivity.CATEGORYTYPE;

/**
 * Created by shailu on 25/6/16.
 */
public class ImageAdapter extends BaseAdapter {
    private Context context;
    private final String[] categoryValues;
    public static final ArrayList<String> CATEGORYTYPE1 = new ArrayList<>();

    public ImageAdapter(Context context, String[] categoryValues) {
        this.context = context;
        this.categoryValues = categoryValues;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        final Boolean[] flipped = new Boolean[6];
        for (int i=0 ;i<6 ;i++)
            flipped[i] = false;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;


        if (convertView == null) {

            gridView = new View(context);

            gridView = inflater.inflate(R.layout.filter_subcatogry, null);

            TextView textView = (TextView) gridView
                    .findViewById(R.id.grid_item_label);
            textView.setText(categoryValues[position]);
            ImageView imageView = (ImageView) gridView
                    .findViewById(R.id.grid_item_image);
            String mobile = categoryValues[position];

            if (mobile.equals("Cricket")) {
                imageView.setImageResource(R.drawable.cricket);
            } else if (mobile.equals("Football")) {
                imageView.setImageResource(R.drawable.football);
            } else if (mobile.equals("Stand Up")) {
                imageView.setImageResource(R.drawable.stand_up);
            }
            else if (mobile.equals("Live Music")) {
                imageView.setImageResource(R.drawable.band);
            } else if (mobile.equals("Dj Night")) {
                imageView.setImageResource(R.drawable.dj_night);
            }else if (mobile.equals("Ladies Night")) {
                imageView.setImageResource(R.drawable.ladies_night);
            }


        } else {
            gridView = convertView;
        }

        final View finalGridView = gridView;
        if (CATEGORYTYPE1.contains(categoryValues[position])) {
            flipped[position] = false;
            finalGridView.findViewById(R.id.event_filter_ll).setBackgroundColor(Color.parseColor("#FFA024D4"));
        } else {
            flipped[position] = true;
            finalGridView.findViewById(R.id.event_filter_ll).setBackgroundColor(Color.WHITE);
        }
        gridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CATEGORYTYPE1.contains(categoryValues[position])) {
                    if(CATEGORYTYPE1.add(categoryValues[position])) {
                        CATEGORYTYPE.add(position);
                    }
                    finalGridView.findViewById(R.id.event_filter_ll).setBackgroundColor(Color.parseColor("#FFA024D4"));

                } else {
                    CATEGORYTYPE1.remove(categoryValues[position]);
                    finalGridView.findViewById(R.id.event_filter_ll).setBackgroundColor(Color.WHITE);

                }

            }
        });

        return gridView;
    }

    @Override
    public int getCount() {
        return categoryValues.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}

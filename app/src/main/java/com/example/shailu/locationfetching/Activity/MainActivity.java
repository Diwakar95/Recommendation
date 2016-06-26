package com.example.shailu.locationfetching.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.shailu.locationfetching.Adapter.EventsAdapter;
import com.example.shailu.locationfetching.Api.EventsAPI;
import com.example.shailu.locationfetching.Api.EventsParser;
import com.example.shailu.locationfetching.Model.Events;
import com.example.shailu.locationfetching.Network.NetworkCallbackEvents;
import com.example.shailu.locationfetching.Network.NetworkManager;
import com.example.shailu.locationfetching.Network.URLConstants;
import com.example.shailu.locationfetching.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static TextView no_events_tonight;
    public static LinearLayout linlaHeaderProgress;
    ProgressBar progressBar;
    public static LinearLayout eventsList;
    public static RelativeLayout no_events_rl;
    public static Button retry_events;
    public static TextView no_events_quick;
    public static LinearLayout no_events_quick_ll;
    public static RecyclerView listView;
    public static ImageView no_internet_img;
    public EventsAdapter adapter;
    public MainActivity rootView;
    public static TextView goto_filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initViews();
        boolean push = getIntent().getBooleanExtra("push", false);
        if (push) {
            String longi = getIntent().getStringExtra("longi");
            String lati = getIntent().getStringExtra("lati");
            Log.e(TAG, "onCreate: push"+longi);
            Log.e(TAG, "onCreate: push"+lati);

            makeNetworkRequestNearbyEvents(longi, lati);
        } else {
            Log.e(TAG, "onCreate: non_push");
            makeNetworkRequestEvents();
        }
        makeNetworkRequestEvents();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, IndexActivity.class));
            }
        });
    }

    private void makeNetworkRequestNearbyEvents(String longi, String lati) {
        final String currentdate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String urlJsonArry = URLConstants.BASE_URL + URLConstants.NEARBY_EVENTS + URLConstants.RESOURCE_FORMAT;
        urlJsonArry += "?date=" + currentdate;
        urlJsonArry += "&longitude=" + longi;
        urlJsonArry += "&latitude=" + lati;
        urlJsonArry += "&user_interests=";
        for (int i=0;i<IndexActivity.CATEGORYTYPE.size();i++){
            urlJsonArry += IndexActivity.CATEGORYTYPE.get(i) + ",";
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(urlJsonArry, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject res) {

                    ArrayList<Events> result = EventsParser.parseGetEventDetail(res, MainActivity.this);

                    linlaHeaderProgress.setVisibility(View.GONE);

                    if (result.size() > 0) {
                        no_events_quick_ll.setVisibility(View.GONE);
                        Log.e(TAG, "makeNetworkRequestEvents: size>0");
                        adapter.updateList(result);
                        eventsList.setVisibility(View.VISIBLE);
                    } else {
                        Log.e(TAG, "makeNetworkRequestEvents: else_size");
                        adapter.updateList(result);
                        no_events_quick_ll.setVisibility(View.VISIBLE);
                    }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                linlaHeaderProgress.setVisibility(View.GONE);
                Log.e(TAG, String.valueOf(error));
                Toast.makeText(MainActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
            }
        });
        NetworkManager.getInstance(this).getRequestQueue().add(jsonObjectRequest);
    }

    private void initViews() {
        rootView = this;
        no_events_tonight = (TextView) rootView.findViewById(R.id.txt_no_internet);
        linlaHeaderProgress = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgress);
        progressBar = (ProgressBar) rootView.findViewById(R.id.pbHeaderProgress);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FFA024D4"), PorterDuff.Mode.SRC_ATOP);
        linlaHeaderProgress.setVisibility(View.VISIBLE);
        no_internet_img = (ImageView) rootView.findViewById(R.id.no_internet_img);
        eventsList = (LinearLayout) rootView.findViewById(R.id.events_list_ll);
        retry_events = (Button) rootView.findViewById(R.id.retry_events);
        no_events_rl = (RelativeLayout) rootView.findViewById(R.id.no_events_rl);
        no_events_quick = (TextView) rootView.findViewById(R.id.no_events_quick);
        no_events_quick_ll = (LinearLayout) rootView.findViewById(R.id.no_events_quick_ll);
        initRecycleView();
    }



    public void initRecycleView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        final Activity context = this;
        listView = (RecyclerView) findViewById(R.id.events_rv);
        listView.setLayoutManager(layoutManager);

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                adapter = new EventsAdapter(new ArrayList<Events>(), MainActivity.this);
                listView.setAdapter(adapter);
            }
        });

    }
    private void makeNetworkRequestEvents() {
        final String currentdate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        EventsAPI.getAllEvents(this, currentdate, new NetworkCallbackEvents() {
            @Override
            public void onSuccess(ArrayList<Events> result) {
                Log.e("json", "" + result);
                linlaHeaderProgress.setVisibility(View.GONE);

                if (result.size() > 0) {

                    no_events_quick_ll.setVisibility(View.GONE);
                Log.e(TAG, "makeNetworkRequestEvents: size>0");
                    adapter.updateList(result);
                    eventsList.setVisibility(View.VISIBLE);
                } else {
                Log.e(TAG, "makeNetworkRequestEvents: else_size");
                    adapter.updateList(result);
                    no_events_quick_ll.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                linlaHeaderProgress.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
            }
        });
    }


}

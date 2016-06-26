package com.example.shailu.locationfetching.Activity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shailu.locationfetching.Adapter.UserLocation;
import com.example.shailu.locationfetching.Network.URLConstants;
import com.example.shailu.locationfetching.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationService extends Service {
    public static final String BROADCAST_ACTION = "Hello";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;
    private String urlJsonArry = URLConstants.BASE_URL + URLConstants.NEARBY_EVENTS + URLConstants.RESOURCE_FORMAT;
    private static String TAG = IndexActivity.class.getSimpleName();
    private boolean intenetAccess=false;
    private  RequestQueue reQueue;
    private IBinder mBinder=new MyBinder();

    Intent intent;
    int counter = 0;
    public boolean SendRequest(final String lati, final String longi)
    {
        final String currentdate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        urlJsonArry += "?date=" + currentdate;
        urlJsonArry += "&longitude=" + longi;
        urlJsonArry += "&latitude=" + lati;
        urlJsonArry += "&user_interests=";
        for (int i=0;i<IndexActivity.CATEGORYTYPE.size();i++){
            urlJsonArry += IndexActivity.CATEGORYTYPE.get(i) + ",";
        }
        Log.d(TAG, "SendRequest: "+urlJsonArry);
        reQueue = Volley.newRequestQueue(this);
        StringRequest req = new StringRequest(Request.Method.GET, urlJsonArry, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                intenetAccess = true;
                Log.e(TAG,s);

                try {
                    JSONObject res = new JSONObject(s);

                    JSONArray events = res.getJSONArray("events");
                    int events_length = events.length();
                    if (events_length > 0) {
                        Intent intent = new Intent(LocationService.this, MainActivity.class);
                        intent.putExtra("push", true);
                        intent.putExtra("longi", ""+longi);
                        intent.putExtra("lati", ""+lati);
                        Notification notification = new Notification.Builder(LocationService.this)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setTicker("Followed")
                                .setAutoCancel(true)
                                .setContentTitle("Nearby Events")
                                .setContentText(events_length + " events nearby")
                                .setSmallIcon(R.drawable.notification_icon)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification_icon))
                                .build();
                        notification.contentIntent =
                                PendingIntent.getActivity(LocationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        manager.notify((int)Math.random(), notification);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                intenetAccess=false;
                Log.e(TAG, String.valueOf(volleyError));
            }
        });
            int socketTimeout = 30000;//30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            req.setRetryPolicy(policy);
            reQueue.add(req);
        return intenetAccess;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,100000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100000, 0, listener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }


        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(listener);
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }
    public class MyBinder extends Binder
    {

    }

    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            Log.i("*****", "Location changed");

            if (isBetterLocation(loc, previousBestLocation)) {
                loc.getLatitude();
                loc.getLongitude();
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                sendBroadcast(intent);
                Log.e("Latitude", String.valueOf(loc.getLatitude()));
                Log.e("Longitude", String.valueOf(loc.getLongitude()));
                UserLocation.setLocation(loc);

                SendRequest(String.valueOf(loc.getLatitude()), String.valueOf(loc.getLongitude()));

            }
        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    }
}
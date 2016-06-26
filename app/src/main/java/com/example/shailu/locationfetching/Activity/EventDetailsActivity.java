package com.example.shailu.locationfetching.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.shailu.locationfetching.Adapter.UberListAdapter;
import com.example.shailu.locationfetching.Adapter.UserLocation;
import com.example.shailu.locationfetching.Api.EventsAPI;
import com.example.shailu.locationfetching.Api.UberAPI;
import com.example.shailu.locationfetching.Model.Events;
import com.example.shailu.locationfetching.Model.UberData;
import com.example.shailu.locationfetching.Model.UberDetailData;
import com.example.shailu.locationfetching.Model.UberLocation;
import com.example.shailu.locationfetching.Model.User;
import com.example.shailu.locationfetching.Network.NetworkResponseListener;
import com.example.shailu.locationfetching.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;

/**
 * Created by shailu on 4/4/16.
 */
public  class EventDetailsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    public static final String EVENT_ID = "event_id";
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final String TAG = EventDetailsActivity.class.getSimpleName();
    public static TextView no_internet_event;
    public static LinearLayout scroll_ll;
    public static AppBarLayout appBarLayout;
    public static Button retryEventDetail;
    public static ImageView no_internet_img;
    public static ImageButton backButtonEvent;
    public static LinearLayout eventDetailsLoader;
    public static LinearLayout uber_ll;
    public static CoordinatorLayout coordLayoutEvent;
    public static TextView uber_pickuptime;
    public static ProgressBar uber_progressbar;
    public static ProgressBar uber_request_loader;
    TextView f_startsAtTextView;
    TextView f_event_name;
    TextView f_event_date;
    TextView venue;
    TextView f_event_cost;
    ImageView f_eventBackground;
    LinearLayout invite_share_ll;
    boolean isLoaded = false;
    private Events event;
    private ImageView eventImageView;
    private TextView dateTextView;
    private TextView timeTextView;
    private TextView descriptionTextView;
    private TextView directionsTextView;
    private TextView costTextView;
    private TextView uberTextView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private LinearLayout inviteLinearLayout;
    private LinearLayout showVenueLinearLayout;

    TextView venue_txt;
    //for invite
    private TextView cost_note_tv;
    private LocationRequest mLocationRequest;
    private android.location.Location mLastLocation;
    private boolean uber_req = false;
    private GoogleApiClient mGoogleApiLocationClient;
    private CheckBox pint_checkbox;
    private CardView beer_pint_card;

    private boolean hideShow1 = false;
    private Timer t1, t2;
    private EditText userMobile;
    private ProgressDialog progressDialog;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("Event Name");

        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#3c3c3c"));
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Hold on for a moment.");


        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white);
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state.name().equals(State.COLLAPSED.name())) {
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_grey);
                } else if (state.name().equals(State.IDLE.name())) {
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white);
                }
            }
        });

        new Async().execute();

        setProgressBarIndeterminateVisibility(true);


        mGoogleApiLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        createLocationRequest();

    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(100);
    }

    protected void startLocationUpdates() {
        ////Log.d("EventDetailActivity", "start locationupdates");
        if (mGoogleApiLocationClient != null)
            if (mGoogleApiLocationClient.isConnected())
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiLocationClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    protected void stopLocationUpdates() {
//        if (mGoogleApiLocationClient != null)
//            if (mGoogleApiLocationClient.isConnected())
//                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiLocationClient, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    protected void onPause() {
        stopLocationUpdates();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mGoogleApiLocationClient == null) {

            mGoogleApiLocationClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();

        }

        if (mGoogleApiLocationClient != null) {
            mGoogleApiLocationClient.connect();
        }

        if (mLastLocation != null)
            makeUberRideTimeRequest((float) mLastLocation.getLatitude(), (float) mLastLocation.getLongitude());
        else {
            uber_pickuptime.setVisibility(View.GONE);
            uber_progressbar.setVisibility(View.GONE);
        }


        if (mGoogleApiLocationClient != null)
            if (mGoogleApiLocationClient.isConnected()) {
                ////Log.d("EventDetailActivity", "onresume--connected");
                startLocationUpdates();
            }
    }
    private void initViews() {

        no_internet_event = (TextView) findViewById(R.id.no_internet_tv);
        scroll_ll = (LinearLayout) findViewById(R.id.main_rl);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        retryEventDetail = (Button) findViewById(R.id.retry_event_details);
        no_internet_img = (ImageView) findViewById(R.id.no_internet_img);
        backButtonEvent = (ImageButton) findViewById(R.id.backButtonEvent);
        eventDetailsLoader = (LinearLayout) findViewById(R.id.linlaHeaderProgressEventDetail);
        ((ProgressBar) findViewById(R.id.pbHeaderProgress)).getIndeterminateDrawable().setColorFilter(Color.parseColor("#ff9804"), PorterDuff.Mode.SRC_ATOP);
        eventImageView = (ImageView) findViewById(R.id.event_image_iv);
        dateTextView = (TextView) findViewById(R.id.date_tv);
        timeTextView = (TextView) findViewById(R.id.time_tv);
        descriptionTextView = (TextView) findViewById(R.id.description_tv);
        inviteLinearLayout = (LinearLayout) findViewById(R.id.invite_ll);
        showVenueLinearLayout = (LinearLayout) findViewById(R.id.show_venue_ll);
        venue_txt = (TextView) findViewById(R.id.venue_txt);
        uber_ll = (LinearLayout) findViewById(R.id.uber_ll);
        costTextView = (TextView) findViewById(R.id.cost_tv);
        uberTextView = (TextView) findViewById(R.id.uber_tv);
        directionsTextView = (TextView) findViewById(R.id.directions_tv);
        uber_pickuptime = (TextView) findViewById(R.id.uber_pickuptime);
        uber_progressbar = (ProgressBar) findViewById(R.id.uber_progressbar);

        coordLayoutEvent = (CoordinatorLayout) findViewById(R.id.coordLayoutEvent);


        //for invite
        invite_share_ll = (LinearLayout) findViewById(R.id.invite_share_ll);

        f_event_date = (TextView) findViewById(R.id.invite_event_date);
        f_event_name = (TextView) findViewById(R.id.invite_event_name);
        f_event_cost = (TextView) findViewById(R.id.invite_place_card_cost);

        venue = (TextView) findViewById(R.id.invite_bar_name);
        f_eventBackground = (ImageView) findViewById(R.id.invite_event_card_image);
        f_startsAtTextView = (TextView) findViewById(R.id.invite_starts_at_tv);
        // for invite ends

        uber_request_loader = (ProgressBar) findViewById(R.id.uber_request_loader);
        uber_request_loader.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ff9804"), PorterDuff.Mode.SRC_ATOP);
    }
    public void fetchEvent(final int id) {
        EventsAPI.getEventsDetailById(this, id, new NetworkResponseListener() {
            @Override
            public void onSuccess(Object response) {
                event = (Events) response;
                venue_txt.setText(event.venue);
                venue.setText(event.venue);
                ((TextView)findViewById(R.id.address_tv)).setText(event.address);
                Glide.with(getApplicationContext()).load(event.event_image)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                isLoaded = true;
                                return false;
                            }
                        }).into(eventImageView);
                collapsingToolbarLayout.setTitle(event.nameEvent);
                dateTextView.setText(getDate(event.date));
                timeTextView.setText(getTime(event.time));
                descriptionTextView.setText(event.description);

                costTextView.setText(event.cost);
                //for invite
                f_event_date.setText(dateTextView.getText() + ", " + timeTextView.getText());
                ////Log.d(TAG, "onClick: date-time-> " + dateTextView.getText() + ", " + timeTextView.getText());
                f_event_name.setText(event.nameEvent);
                f_event_cost.setText(event.cost);
                venue.setText(event.venue);

                if (!event.cost.equalsIgnoreCase("0")) {
                    f_startsAtTextView.setText("Starts at");
                    f_event_cost.setText("\u20B9" + event.cost);
                } else {
                    f_startsAtTextView.setText("Entry");
                    f_event_cost.setText("FREE");
                }

                implementDirection();
                share();
                uber();

                scroll_ll.setVisibility(View.VISIBLE);
                appBarLayout.setVisibility(View.VISIBLE);
                no_internet_event.setVisibility(View.GONE);
                retryEventDetail.setVisibility(View.GONE);
                no_internet_img.setVisibility(View.GONE);
                backButtonEvent.setVisibility(View.GONE);
                eventDetailsLoader.setVisibility(View.GONE);




            }

            @Override
            public void onError(String response) {
                //Log.e(TAG, response);
                scroll_ll.setVisibility(View.GONE);
                appBarLayout.setVisibility(View.GONE);
                no_internet_event.setVisibility(View.VISIBLE);
                retryEventDetail.setVisibility(View.VISIBLE);
                no_internet_img.setVisibility(View.VISIBLE);
                backButtonEvent.setVisibility(View.VISIBLE);
                eventDetailsLoader.setVisibility(View.GONE);
            }
        });
    }

    private void scrollToUber() {
        final NestedScrollView mNestedScrollView = (NestedScrollView) findViewById(R.id.nestedScroll);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.onNestedPreScroll(coordLayoutEvent, appBarLayout, mNestedScrollView, 0, 1000, new int[2]);
            int targetScroll = mNestedScrollView.getScrollY() + 1000;
            mNestedScrollView.scrollTo(0, targetScroll);
            mNestedScrollView.setSmoothScrollingEnabled(true);
            ViewCompat.setNestedScrollingEnabled(mNestedScrollView, false);
            final int currentScrollY = mNestedScrollView.getScrollY();
            ViewCompat.postOnAnimationDelayed(mNestedScrollView, new Runnable() {
                int currentY = currentScrollY;

                @Override
                public void run() {
                    if (currentScrollY == mNestedScrollView.getScrollY()) {
                        ViewCompat.setNestedScrollingEnabled(mNestedScrollView, true);
                        return;
                    }
                    currentY = mNestedScrollView.getScrollY();
                    ViewCompat.postOnAnimation(mNestedScrollView, this);
                }
            }, 10);
        }
    }


    private String[] getDates() {
        SimpleDateFormat sdf3, sdf1, sdf2, sdf4, sdf;
        String[] dates = new String[4];
        try {
            sdf3 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            sdf1 = new SimpleDateFormat("dd MMM");
            sdf = new SimpleDateFormat("MMM\nyyyy");
            sdf2 = new SimpleDateFormat("hh:mm a");
            sdf4 = new SimpleDateFormat("yyyy-MM-dd");
            //datetime1 = sdf2.format(sdf3.parse(event.time));
            dates[3] = sdf.format(sdf4.parse(event.date));
            dates[2] = sdf1.format(sdf4.parse(event.date));
            dates[0] = dates[2].split(" ")[0];
            dates[1] = sdf2.format(sdf3.parse(event.time));
        } catch (Exception e) {
            e.printStackTrace();

        }
        return dates;
    }

    private void setValue(TextView textView, int price, String freeText) {
        if (price > 0) {
            textView.setText("\u20B9" + price);
        } else {
            textView.setText(freeText);
        }
    }

    private void uber() {


        getUserLocation();

//        if(mLastLocation != null)
        if (User.latitude != null && User.longitude != null && !User.latitude.trim().equals("") && !User.longitude.trim().equals("")) {
            makeUberRideTimeRequest(Float.parseFloat(User.latitude), Float.parseFloat(User.longitude));
        } else {
            //Log.e(TAG, "uber: User.Location  empty");
            uber_pickuptime.setVisibility(View.GONE);
            uber_progressbar.setVisibility(View.GONE);
            uber_progressbar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ff9804"), PorterDuff.Mode.SRC_ATOP);
        }

        uber_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                uber_req = true;
                settingsrequest();

                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    return;
                }

                if (mLastLocation == null) {
                    Toast.makeText(EventDetailsActivity.this, "Please wait we are figuring out your location.", Toast.LENGTH_SHORT).show();
                    return;
                }

                makeUberRideRequest();

//                end
            }
        });
    }

    private void makeUberRideRequest() {

        uber_request_loader.setVisibility(View.VISIBLE);
        uber_ll.setVisibility(View.GONE);

        final UberLocation start = new UberLocation((float) mLastLocation.getLatitude(), (float) mLastLocation.getLongitude());
        final UberLocation end = new UberLocation(Float.parseFloat(event.location_lati), Float.parseFloat(event.location_long));

        UberAPI.getUberDetailData(EventDetailsActivity.this, start, end, new NetworkResponseListener() {
            @Override
            public void onSuccess(Object response) {
                uber_req = false;
                //Log.e("EventDetailActivity", "uberDetailresponse-> " + response);

                makeUberRideTimeRequest(start.getLatitude(), start.getLongitude());

                ArrayList<UberDetailData> uberDetailDataArrayList = (ArrayList<UberDetailData>) response;
                uber_request_loader.setVisibility(View.GONE);
                uber_ll.setVisibility(View.VISIBLE);
                final UberListAdapter uberListAdapter;
                uberListAdapter = new UberListAdapter(EventDetailsActivity.this, uberDetailDataArrayList);
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(EventDetailsActivity.this);
                builder.setAdapter(uberListAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uber_req = false;

                        UberDetailData ub = uberListAdapter.getItem(which);
                        String dropName = event.venue;
                        dropName = dropName.replaceAll(" ", "%20");
                        String dropAddress = event.address;
                        dropAddress = dropAddress.replaceAll(" ", "%20");
                        dropName.trim();
                        dropAddress.trim();

                        dropName += ",";

                        try {
                            PackageManager pm = getPackageManager();
                            pm.getPackageInfo("com.ubercab", PackageManager.GET_ACTIVITIES);
                            String uri =
                                    "uber://?action=setPickup&product_id=" + ub.getProduct_id() + "&pickup[latitude]=" + start.getLatitude() + "&pickup[longitude]=" + start.getLongitude() + "&pickup[nickname]=Current%20Location&dropoff[latitude]=" + end.getLatitude() + "&dropoff[longitude]=" + end.getLongitude() + "&dropoff[nickname]=" + dropName + "&dropoff[formatted_address]=" + dropAddress + "&client_id=" + UberAPI.CLIENT_ID;
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(uri));
                            startActivity(intent);
                        } catch (PackageManager.NameNotFoundException e) {
                            // No Uber app! Open mobile website.
                            String url = "https://m.uber.com/sign-up?client_id=" + UberAPI.CLIENT_ID;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }

                    }
                });

                if (uberDetailDataArrayList.size() > 0) {
                    if (!isFinishing())
                        builder.show();
                } else
                    Toast.makeText(EventDetailsActivity.this, "No Cars Available", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onError(String response) {
                EventDetailsActivity.uber_request_loader.setVisibility(View.GONE);
                uber_ll.setVisibility(View.VISIBLE);
                Toast.makeText(EventDetailsActivity.this, "There was some error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void makeUberRideTimeRequest(float latitude, float longitude) {
        uber_pickuptime.setVisibility(View.GONE);
        uber_progressbar.setVisibility(View.VISIBLE);
        UberAPI.getUberRideTime(this, latitude, longitude, new NetworkResponseListener() {
            @Override
            public void onSuccess(Object response) {
                ////Log.d("UberRideTime", "Success in PlaceDetail " + response);
                UberData u = UberAPI.getMinimumPickupRide((ArrayList<UberData>) response);
                if (u != null) {
                    uber_progressbar.setVisibility(View.GONE);
                    uber_pickuptime.setVisibility(View.VISIBLE);
                    uber_pickuptime.setText("in " + u.getEstimatedTime() + " min");
                } else { // no cars available
                    uber_pickuptime.setVisibility(View.GONE);
                    uber_progressbar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String response) {
                ////Log.d("UberRideTime", "Error in PlaceDetail ");
                uber_progressbar.setVisibility(View.GONE);
                uber_pickuptime.setVisibility(View.GONE);
            }
        });
    }


    private void share() {
        inviteLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/*");
//                View viewX = eventImageView;

                if (!isLoaded) {
                    Toast.makeText(EventDetailsActivity.this, "Please try again after the Image loads.", Toast.LENGTH_LONG).show();
                } else {

                    eventImageView.setDrawingCacheEnabled(true);
                    eventImageView.buildDrawingCache(true);
                    Bitmap bitmap_image = eventImageView.getDrawingCache();

                    f_eventBackground.setImageBitmap(bitmap_image);

                    LinearLayout viewX = invite_share_ll;

                    String state = Environment.getExternalStorageState();
                    File picFile = null;
                    String fileName = "";
                    Bitmap bitmap = null;
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        File picDir = new File(Environment.getExternalStorageDirectory() + "/Events");
                        if (!picDir.exists()) {
                            picDir.mkdir();
                        }
                        viewX.setDrawingCacheEnabled(true);
                        viewX.buildDrawingCache(true);
                        bitmap = viewX.getDrawingCache();

                        fileName = event.nameEvent + ".jpg";
                        picFile = new File(picDir + "/" + fileName);
                        try {
                            picFile.createNewFile();
                            FileOutputStream picOut = new FileOutputStream(picFile);

                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
                            boolean saved = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, picOut);
                            if (saved) {
                                Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();
                            } else {
                                //Error
                                Toast.makeText(getApplicationContext(), "There was some error.", Toast.LENGTH_SHORT).show();
                            }
                            picOut.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        viewX.destroyDrawingCache();
//                        bitmap.recycle();
                    } else {
                        //Error
                        Toast.makeText(getApplicationContext(), "There was some error.", Toast.LENGTH_SHORT).show();

                    }

                    File uriFile = new File(Environment.getExternalStorageDirectory() + "/Events/" + fileName);

                    Uri img_uri = Uri.fromFile(uriFile);
                    //Log.e("img_url->", img_uri.toString());


                    String shareBody = event.nameEvent + " at " + event.venue + " on " + getDate(event.date) + " from " + getTime(event.time) + ".\n";

                    if (event.cost.trim().equalsIgnoreCase("0")) {
                        shareBody += "Entry is FREE. ";
                    } else {
                        shareBody += "Entry starts at \u20B9" + event.cost + ". ";
                    }

                    shareBody += "\n" + "Download Brisky\nhttp://bit.ly/briskyapp";
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, img_uri);
                    view.getContext().startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
            }
        });
    }

    private void implementDirection() {
        directionsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double latitude = Double.parseDouble(event.location_lati);
                double longitude = Double.parseDouble(event.location_long);
                String label = " ";
                String uriBegin = "geo:" + latitude + "," + longitude;
                String query = latitude + "," + longitude + "(" + label + ")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                Uri uri = Uri.parse(uriString);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private String getDate(String date) {
        SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return new SimpleDateFormat("dd MMM, yyyy").format(source.parse(date)).toString();
        } catch (ParseException e) {
            return "";
        }
    }

    private String getTime(String time) {
        SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        try {
            return new SimpleDateFormat("hh:mm a").format(source.parse(time)).toString();
        } catch (ParseException e) {
            return "";
        }
    }



    private boolean isStagAllowed(JSONObject bookingDetails) {
        try {
            return bookingDetails.getBoolean("stag_allowed");
        } catch (JSONException e) {

            return false;
        }
    }

    private boolean isLadiesAllowed(JSONObject bookingDetails) {
        try {
            return bookingDetails.getBoolean("ladies_allowed");
        } catch (JSONException e) {

            return false;
        }
    }

    private boolean isPayAtVenue(JSONObject bookingDetails) {
        try {
            return bookingDetails.optBoolean("pay_at_venue", true);
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        getUserLocation();
//        Toast.makeText(EventDetailsActivity.this, "Connected", Toast.LENGTH_SHORT).show();
        startLocationUpdates();
    }

    private void getUserLocation() {
        if (mGoogleApiLocationClient != null && mGoogleApiLocationClient.isConnected())
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiLocationClient);

        if (mLastLocation != null) {
//            Toast.makeText(EventDetailsActivity.this, "loc not_null", Toast.LENGTH_SHORT).show();
//            Toast.makeText(EventDetailsActivity.this, "lat-> "+mLastLocation.getLatitude()+", long-> "+mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            User.longitude = "" + mLastLocation.getLongitude();
            User.latitude = "" + mLastLocation.getLatitude();
            UserLocation.setLocation(mLastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (mGoogleApiLocationClient != null)
            mGoogleApiLocationClient.connect();

    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        mLastLocation = location;
        getUserLocation();

//        Toast.makeText(getApplicationContext(), "Location changed!", Toast.LENGTH_SHORT).show();
        if (mLastLocation != null) {
            if (uber_req) {

                uber_req = false;
                makeUberRideRequest();

            }

            makeUberRideTimeRequest((float) mLastLocation.getLatitude(), (float) mLastLocation.getLongitude());

        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public void settingsrequest() {
        if (mLocationRequest == null) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(10000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setSmallestDisplacement(100);
        }
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiLocationClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        getUserLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(EventDetailsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
//                        startLocationUpdates();
//                        Toast.makeText(EventDetailsActivity.this, "Yes", Toast.LENGTH_SHORT).show();
                        uber_request_loader.setVisibility(View.VISIBLE);
                        uber_ll.setVisibility(View.GONE);
                        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                        User.GPS = "true";
                        if (locationManager != null) {
                            Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
//                        settingsrequest();//keep asking if imp or do whatever
//                        Toast.makeText(EventDetailsActivity.this, "No", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }

    public abstract static class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {

        private State mCurrentState = State.IDLE;

        @Override
        public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
            if (i == 0) {
                if (mCurrentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED);
                }
                mCurrentState = State.EXPANDED;
            } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED);
                }
                mCurrentState = State.COLLAPSED;
            } else {
                if (mCurrentState != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE);
                }
                mCurrentState = State.IDLE;
            }
        }

        public abstract void onStateChanged(AppBarLayout appBarLayout, State state);

        public enum State {
            EXPANDED,
            COLLAPSED,
            IDLE
        }
    }

    private class Async extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            int id = getIntent().getIntExtra(EVENT_ID, -1);
            if (id == -1) {
                //Log.e(TAG, "Error fetching id");
            } else {
                fetchEvent(id);
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            retryEventDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Async().execute();
                }
            });
            backButtonEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            initViews();


        }
    }

}

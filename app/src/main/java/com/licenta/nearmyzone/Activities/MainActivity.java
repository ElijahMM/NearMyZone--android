package com.licenta.nearmyzone.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.licenta.nearmyzone.CustomView.ChoosePopup;
import com.licenta.nearmyzone.CustomView.SearchPopup;
import com.licenta.nearmyzone.Handlers.DirectionFinder;
import com.licenta.nearmyzone.Handlers.DirectionFinderListener;
import com.licenta.nearmyzone.Handlers.GPSLocation;
import com.licenta.nearmyzone.Handlers.OfflineHandler;
import com.licenta.nearmyzone.Models.GooglePlace;
import com.licenta.nearmyzone.Models.GDirection.Route;
import com.licenta.nearmyzone.Models.User;
import com.licenta.nearmyzone.Models.WeatherResponse;
import com.licenta.nearmyzone.Network.AddressRequest;
import com.licenta.nearmyzone.Network.PlacesRequest;
import com.licenta.nearmyzone.R;
import com.licenta.nearmyzone.Utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.main_view_details)
    RelativeLayout detailsView;
    @BindView(R.id.main_view_toggle_details_imageView)
    ImageView toggleDetailsView;
    @BindView(R.id.weather_image)
    ImageView weatherImageView;
    @BindView(R.id.weather_text)
    TextView weatherTextView;
    @BindView(R.id.main_view_weather_text)
    RelativeLayout weatherView;

    private GPSLocation gpsLocation;
    private GoogleMap gMap;
    private Boolean toggleMenu = false;
    private Marker myMarker = null;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        initComponents(toolbar);
    }

    private void initComponents(Toolbar toolbar) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        navigationView.setNavigationItemSelectedListener(this);
        populateView();


    }

    private void populateView() {
        View header = navigationView.getHeaderView(0);
        TextView navUserName = (TextView) header.findViewById(R.id.main_activity_nav_profile_name);
        ImageView navUserPicture = (ImageView) header.findViewById(R.id.main_activity_nav_profile_picture);
        Glide.with(MainActivity.this).load(User.getInstance().getUserModel().getUserPhotoUrl()).into(navUserPicture);
        navUserName.setText(User.getInstance().getUserModel().getUsername());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
            }
        } catch (Resources.NotFoundException e) {
        }
        if (!Util.askGpsPermission(MainActivity.this)) {
            openDialogForLocation();
        } else {
            startLocationUpdate();
        }

    }

    public void startLocationUpdate() {
        gpsLocation = new GPSLocation();
        gpsLocation.getLocation(MainActivity.this, locationResult);
    }

    private GPSLocation.LocationResult locationResult = new GPSLocation.LocationResult() {
        @Override
        public void gotLocation(final Location location) {
            if (myMarker == null) {
                myMarker = gMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))

                );
            } else {
                myMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            }
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            gMap.moveCamera(center);
            gMap.animateCamera(zoom);

            getWeather(location);
            AddressRequest addressRequest = new AddressRequest();
            addressRequest.getAddress(MainActivity.this, location, addressResult);
            PlacesRequest placesRequest = new PlacesRequest();
            placesRequest.getPlaces(MainActivity.this, location, placeResult);

            setDirections(location);
        }
    };

    private void setDirections(final Location location) {
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                try {
                    new DirectionFinder(
                            MainActivity.this,
                            new LatLng(location.getLatitude(), location.getLongitude()),
                            marker.getPosition(),
                            "walking",
                            new DirectionFinderListener() {
                                @Override
                                public void onDirectionFinderStart() {

                                }

                                @Override
                                public void onDirectionFinderSuccess(List<Route> route) {
                                    clearPolylinePath();

                                    PolylineOptions polylineOptions = new PolylineOptions().
                                            geodesic(true).
                                            color(MainActivity.this.getResources().getColor(android.R.color.holo_red_dark)).
                                            width(16);

                                    if (!route.isEmpty()) {
                                        for (int i = 0; i < route.get(0).points.size(); i++)
                                            polylineOptions.add(route.get(0).points.get(i));
                                    }

                                    polylinePaths.add(gMap.addPolyline(polylineOptions));
                                }
                            }).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    private void clearPolylinePath() {
        if (polylinePaths != null) {
            Log.w("Polyline", "Polyline Remove");
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
        if (polylinePaths != null) {
            polylinePaths.clear();
        }
        originMarkers.clear();
        destinationMarkers.clear();
    }

    AddressRequest.AddressResult addressResult = new AddressRequest.AddressResult() {
        @Override
        public void gotAddress(String s) {
            Log.w("GotAddress", s);
            toolbar.setTitle(s);
        }
    };

    PlacesRequest.PlaceResult placeResult = new PlacesRequest.PlaceResult() {
        @Override
        public void gotPlaces(List<GooglePlace> googlePlaces) {
            Util.showObjectLog(googlePlaces);
            for (final GooglePlace googlePlace : googlePlaces) {
                Glide.with(MainActivity.this)
                        .asBitmap()
                        .load(googlePlace.getIcon())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                gMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(googlePlace.getLocation().getLat(), googlePlace.getLocation().getLon()))
                                        .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                        .title(googlePlace.getName()));
                            }
                        });

            }
        }
    };

    private void getWeather(Location location) {
        String url = "http://api.worldweatheronline.com/premium/v1/weather.ashx?" +
                "key=" + getResources().getString(R.string.weather_api_key) +
                "&q=" + location.getLatitude() + "," + location.getLongitude() +
                "&format=json";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            WeatherResponse weatherResponse = parseWeather(response.getJSONObject("data"));
                            if (weatherResponse != null) {
                                Util.showObjectLog(weatherResponse);
                                Glide.with(MainActivity.this).load(weatherResponse.getWeatherIconUrl()).into(weatherImageView);
                                String weatherCond = "It's " + weatherResponse.getWeatherDesc() + " outside" +
                                        ", temperature " + weatherResponse.getTemp_C() +
                                        ", feels like " + weatherResponse.getFeelsLikeC() +
                                        ", wind " + weatherResponse.getWindspeedKmph() +
                                        ", humidity " + weatherResponse.getHumidity();
                                weatherTextView.setText(weatherCond);
                                weatherView.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        Volley.newRequestQueue(this).add(jsObjRequest);
    }

    private WeatherResponse parseWeather(JSONObject jsonObject) {
        WeatherResponse weatherResponse = new WeatherResponse();
        try {
            if (jsonObject.has("current_condition")) {
                JSONObject jsonObject1 = jsonObject.getJSONArray("current_condition").getJSONObject(0);
                weatherResponse.setTemp_C(jsonObject1.getInt("temp_C"));
                weatherResponse.setWeatherIconUrl(jsonObject1.getJSONArray("weatherIconUrl").getJSONObject(0).getString("value"));
                weatherResponse.setWeatherDesc(jsonObject1.getJSONArray("weatherDesc").getJSONObject(0).getString("value"));
                weatherResponse.setWindspeedKmph(jsonObject1.getString("windspeedKmph"));
                weatherResponse.setHumidity(jsonObject1.getString("humidity"));
                weatherResponse.setFeelsLikeC(jsonObject1.getString("FeelsLikeC"));
                return weatherResponse;
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void openDialogForLocation() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setMessage("In order to get directions to Bizz Cafe you need to activate your phone location");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(settingsIntent, 111);
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    @OnClick({R.id.main_view_toggle_details})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_view_toggle_details:
                if (toggleMenu.equals(false)) {
                    detailsView.setVisibility(View.VISIBLE);
                    toggleDetailsView.setBackground(getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp));
                    toggleMenu = true;
                } else {
                    detailsView.setVisibility(View.GONE);
                    toggleDetailsView.setBackground(getResources().getDrawable(R.drawable.ic_arrow_drop_up_black_24dp));
                    toggleMenu = false;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_find_near) {
            ChoosePopup choosePopup = new ChoosePopup(MainActivity.this);
            choosePopup.init();
            initChosePopupListeners(choosePopup);
            choosePopup.showPopup();
        } else if (id == R.id.nav_find) {
            SearchPopup searchPopup = new SearchPopup(MainActivity.this);
            searchPopup.init();
            searchPopup.setSearchClickListner(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            searchPopup.showPopup();
        } else if (id == R.id.nav_profile) {
            Util.openActivity(MainActivity.this, ProfileActivity.class);
        } else if (id == R.id.nav_logout) {
            OfflineHandler.getInstance().deleteEmail();
            OfflineHandler.getInstance().deletePassword();
            FirebaseAuth.getInstance().signOut();
            Util.openActivityClosingStack(MainActivity.this, LoginActivity.class);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initChosePopupListeners(ChoosePopup choosePopup) {
        choosePopup.setBusClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        choosePopup.setHotelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        choosePopup.setRestaurantClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        choosePopup.setTrainClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            startLocationUpdate();
        }
    }

}

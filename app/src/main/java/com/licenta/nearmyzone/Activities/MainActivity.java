package com.licenta.nearmyzone.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
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
import com.licenta.nearmyzone.Utils.AbsValues;
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

    private static final String API_KEY = "AIzaSyDuv0seZBjZeAnGenZw2QaH81_y3M-D-ao";
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 222;

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

    @BindView(R.id.main_view_details_image)
    ImageView detailsImageView;
    @BindView(R.id.main_view_details_title)
    TextView detailsName;
    @BindView(R.id.main_view_details_desc)
    TextView detailsDescr;
    @BindView(R.id.main_view_details_rev)
    TextView detailsRev;
    @BindView(R.id.main_view_direction)
    FloatingActionButton getDirectionsFAB;

    private GPSLocation gpsLocation;
    private GoogleMap gMap;
    private Boolean toggleMenu = false;
    private Marker myMarker = null;
    private List<GooglePlace> googlePlaceList;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private LatLng currentSelectedMarker;
    private Location myLocation;
    private PlacesRequest placesRequest;

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
        getDirectionsFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveNewRoute(myLocation, currentSelectedMarker);

            }
        });
        googlePlaceList = new ArrayList<>();
        placesRequest = new PlacesRequest();
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
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        if (!Util.askGpsPermission(MainActivity.this)) {
            openDialogForLocation();
        } else {
            startLocationUpdate();
        }
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                getDirectionsFAB.setVisibility(View.VISIBLE);
                detailsView.setVisibility(View.VISIBLE);
                toggleDetailsView.setBackground(getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp));
                toggleMenu = true;
                String pId = GooglePlace.getPlaceID(googlePlaceList, marker.getPosition());
                if (pId != null) {
                    getPlaceDetails(pId);
                }
                currentSelectedMarker = marker.getPosition();
                return false;
            }
        });
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                getDirectionsFAB.setVisibility(View.GONE);
                detailsView.setVisibility(View.GONE);
                toggleDetailsView.setBackground(getResources().getDrawable(R.drawable.ic_arrow_drop_up_black_24dp));
                toggleMenu = false;
                currentSelectedMarker = null;
                clearPolylinePath();
            }
        });
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


            placesRequest.getPlaces(MainActivity.this, location, placeResult);
            placesRequest.getBusPlaces(MainActivity.this, location, placeResult);
            myLocation = location;
            if (currentSelectedMarker != null) {
                retrieveNewRoute(location, currentSelectedMarker);
            }
        }
    };

    private void retrieveNewRoute(Location source, LatLng destination) {
        try {
            new DirectionFinder(
                    MainActivity.this,
                    new LatLng(source.getLatitude(), source.getLongitude()),
                    destination,
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
            googlePlaceList.addAll(googlePlaces);
            Util.showObjectLog(googlePlaces);
            for (final GooglePlace googlePlace : googlePlaces) {

                loadMap(googlePlace);

            }
        }

        @Override
        public void gotAdditionalPlaces(List<GooglePlace> googlePlaces) {
            googlePlaceList.addAll(googlePlaces);
            Util.showObjectLog(googlePlaces);
            for (final GooglePlace googlePlace : googlePlaces) {
                loadMap(googlePlace);
            }
        }

        @Override
        public void gotBusPlaces(List<GooglePlace> googlePlaces) {
            googlePlaceList.addAll(googlePlaces);
            for (final GooglePlace googlePlace : googlePlaces) {
                loadMap(googlePlace);
            }
        }
    };

    private void loadMap(final GooglePlace googlePlace) {
        Glide.with(MainActivity.this)
                .asBitmap()
                .load(googlePlace.getIcon())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        gMap.addMarker(new MarkerOptions()
                                .position(new LatLng(googlePlace.getLocation().getLat(), googlePlace.getLocation().getLon()))
                                .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                .title(googlePlace.getName())
                        );
                    }
                });
    }

    private void findNearPlace(String type) {
        List<GooglePlace> placeList = new ArrayList<>();
        for (GooglePlace googlePlace : googlePlaceList) {
            if (googlePlace.getType().equals(type)) {
                double dist = Util.distBetweenLatLng(
                        new LatLng(myLocation.getLatitude(), myLocation.getLongitude()),
                        new LatLng(googlePlace.getLocation().getLat(), googlePlace.getLocation().getLon())
                );
                if (dist < User.getInstance().getUserModel().getDistance()) {
                    placeList.add(googlePlace);

                }
            }
        }
        if(placeList.size()>0) {
            GooglePlace nearPlace = placeList.get(0);
            Util.showObjectLog(placeList);
            for (GooglePlace googlePlace : placeList) {
                double dist1 = Util.distBetweenLatLng(
                        new LatLng(myLocation.getLatitude(), myLocation.getLongitude()),
                        new LatLng(googlePlace.getLocation().getLat(), googlePlace.getLocation().getLon())
                );
                double dist2 = Util.distBetweenLatLng(
                        new LatLng(myLocation.getLatitude(), myLocation.getLongitude()),
                        new LatLng(nearPlace.getLocation().getLat(), nearPlace.getLocation().getLon())
                );
                if (dist1 < dist2) {
                    nearPlace = googlePlace;
                }
            }
            detailsView.setVisibility(View.VISIBLE);
            getDirectionsFAB.setVisibility(View.VISIBLE);
            getPlaceDetails(nearPlace.getPlace_id());
            currentSelectedMarker = new LatLng(nearPlace.getLocation().getLat(),nearPlace.getLocation().getLon());
        }else{
            Util.showShortToast(MainActivity.this,"No place near you");
        }
    }

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

    private void getPlaceDetails(String placeID) {
        String url = "https://maps.googleapis.com/maps/api/place/details/json?" +
                "placeid=" + placeID +
                "&key=" + API_KEY;
        Log.w("Url", url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Util.showObjectLog(response);
                        try {
                            if (response.has("result")) {
                                JSONObject jsonObject = response.getJSONObject("result");
                                if (jsonObject.has("name")) {
                                    detailsName.setText(jsonObject.getString("name"));
                                }
                                if (jsonObject.has("reviews")) {
                                    detailsDescr.setText(jsonObject.getJSONArray("reviews").getJSONObject(0).getString("text"));
                                }
                                if (jsonObject.has("rating")) {
                                    detailsRev.setText("Rating " + jsonObject.getDouble("rating"));
                                }
                                if (jsonObject.has("photos")) {
                                    String ref = jsonObject.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                                    Glide.with(MainActivity.this)
                                            .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400" +
                                                    "&photoreference=" + ref +
                                                    "&key=" + API_KEY)
                                            .into(detailsImageView);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        Volley.newRequestQueue(this).add(jsonObjectRequest);

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
        dialog.setMessage("In order to get directions you need to activate your phone location");
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

    public void navMenuSearchPlace() {
        try {
            AutocompleteFilter filter =
                    new AutocompleteFilter.Builder()
                            .setCountry("RO")
                            .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(filter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
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
            navMenuSearchPlace();
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

    private void initChosePopupListeners(final ChoosePopup choosePopup) {
        choosePopup.setBusClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findNearPlace(AbsValues.bus_station);
                choosePopup.dismissDialog();
            }
        });
        choosePopup.setHotelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findNearPlace(AbsValues.hospital);
                choosePopup.dismissDialog();
            }
        });
        choosePopup.setRestaurantClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findNearPlace(AbsValues.restaurant);
                choosePopup.dismissDialog();
            }
        });
        choosePopup.setAtmClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findNearPlace(AbsValues.atm);
                choosePopup.dismissDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            startLocationUpdate();
        }
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                detailsView.setVisibility(View.VISIBLE);
                getDirectionsFAB.setVisibility(View.VISIBLE);
                getPlaceDetails(place.getId());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }
}

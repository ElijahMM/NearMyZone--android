package com.licenta.nearmyzone.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.licenta.nearmyzone.CustomView.ChoosePopup;
import com.licenta.nearmyzone.Handlers.GPSLocation;
import com.licenta.nearmyzone.R;
import com.licenta.nearmyzone.Utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.main_view_details)
    RelativeLayout detailsView;
    @BindView(R.id.main_view_toggle_details_imageView)
    ImageView toggleDetailsView;

    private GPSLocation gpsLocation;
    private GoogleMap gMap;
    private Boolean toggleMenu = false;
    private Marker myMarker = null;

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
        navigationView.setNavigationItemSelectedListener(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
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
        public void gotLocation(Location location) {
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
        }
    };

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
            choosePopup.showPopup();
        } else if (id == R.id.nav_find) {

        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_logout) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            startLocationUpdate();
        }
    }
}

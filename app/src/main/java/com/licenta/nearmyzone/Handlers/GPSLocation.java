package com.licenta.nearmyzone.Handlers;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.licenta.nearmyzone.Utils.AbsValues;

/**
 * Created by Morgenstern on 08/23/2017.
 */

@SuppressWarnings("MissingPermission")
public class GPSLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private Context context;

    private static final String TAG = "GPSLocation";
    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;
    private Location mLocation;
    private LocationManager locationManager;
    private LocationResult locationResult;

    public void getLocation(Context context, LocationResult locationResult) {
        this.context = context;
        this.locationResult = locationResult;
        this.context = context;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mGoogleApiClient.connect();
    }

    protected void startLocationUpdates() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(AbsValues.GPS_REQUEST_INTERVAL)
                .setFastestInterval(AbsValues.GPS_REQUEST_INTERVAL);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
        mLocation = new Location("");
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation == null) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, String.valueOf(location.getLatitude()) + " " + String.valueOf(location.getLongitude()));
        locationResult.gotLocation(location);
    }


    public void stopGPSRequests() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        mGoogleApiClient.disconnect();
        mLocationRequest = null;
        locationResult = null;
    }

    public static abstract class LocationResult {
        public abstract void gotLocation(Location location);
    }
}


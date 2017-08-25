package com.licenta.nearmyzone.Handlers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.licenta.nearmyzone.Models.GDirection.Route;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morgenstern on 08/25/2017.
 */

public class DirectionFinder {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";

    private static final String API_KEY = "AIzaSyDuv0seZBjZeAnGenZw2QaH81_y3M-D-ao";
    private DirectionFinderListener listener;  //Call Api Google
    private LatLng origin;
    private LatLng destination;
    private Context context;
    private String travel_mode;

    public DirectionFinder(Context context, LatLng origin, LatLng destination, String travel_mode, DirectionFinderListener listener) {
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
        this.travel_mode = travel_mode;
        this.context = context;
    }

    public void execute() throws IOException {
        listener.onDirectionFinderStart();
        new DownloadRawData().execute(createUrl());
    }

    private String createUrl() throws IOException {

        String urlOrigin = URLEncoder.encode(origin.latitude + "," + origin.longitude, "utf-8");
        String urlDestination = URLEncoder.encode(destination.latitude + "," + destination.longitude, "utf-8");
        Log.w("Path", DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&mode=" + travel_mode + "&key=" + API_KEY);
        return DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&mode=" + travel_mode + "&key=" + API_KEY;

    }


    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    //Call APi Google Matrix
    //Response start/end position, distance,duration
    //Parse JSON
    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

        List<Route> routes = new ArrayList<Route>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            Route route;

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
            JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
            JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");


            route = new Route(overview_polylineJson, jsonDistance, jsonDuration, jsonLeg, jsonStartLocation, jsonEndLocation);
            routes.add(route);
        }

        listener.onDirectionFinderSuccess(routes);
    }


}


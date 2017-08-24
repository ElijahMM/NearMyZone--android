package com.licenta.nearmyzone.Network;

import android.content.Context;
import android.location.Location;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.licenta.nearmyzone.Utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Morgenstern on 08/24/2017.
 */

public class PlacesRequest {
    private static final String API_KEY = "AIzaSyDuv0seZBjZeAnGenZw2QaH81_y3M-D-ao";
    private PlaceResult placeResult;

    public void getPlaces(Context context, Location location, final PlaceResult placeResult1) {
        this.placeResult = placeResult1;
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + location.getLatitude() + "," + location.getLongitude() +
                "&radius=" + "1000" +
                "&types=" + "bus_station" +
                "&sensor=" + "true" +
                "&key=" + API_KEY;
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Util.showObjectLog(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        Volley.newRequestQueue(context).add(jsObjRequest);
    }

    public static abstract class PlaceResult {
        public abstract void gotPlaces();
    }
}

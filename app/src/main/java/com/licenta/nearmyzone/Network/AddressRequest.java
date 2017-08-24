package com.licenta.nearmyzone.Network;

import android.content.Context;
import android.location.Location;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Morgenstern on 08/24/2017.
 */

public class AddressRequest {

    private static final String API_KEY = "AIzaSyDuv0seZBjZeAnGenZw2QaH81_y3M-D-ao";
    private AddressResult addressResult;

    public void getAddress(Context context, Location location, final AddressResult addressResult1) {
        this.addressResult = addressResult1;
        String url = "https://maps.googleapis.com/maps/api/geocode/json?" +
                "latlng=" + location.getLatitude() + "," + location.getLongitude() +
                "&key=" + API_KEY;
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("results")) {
                                JSONObject jsonObject = response.getJSONArray("results").getJSONObject(0);
                                if (jsonObject.has("formatted_address")) {
                                    String address = jsonObject.getString("formatted_address");
                                    addressResult.gotAddress(address);
                                }

                            }
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        Volley.newRequestQueue(context).add(jsObjRequest);
    }

    public static abstract class AddressResult {
        public abstract void gotAddress(String s);
    }
}

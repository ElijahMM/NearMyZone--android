package com.licenta.nearmyzone.Network;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.licenta.nearmyzone.Models.GLocation;
import com.licenta.nearmyzone.Models.GooglePlace;
import com.licenta.nearmyzone.Utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morgenstern on 08/24/2017.
 */

public class PlacesRequest {
    private static final String API_KEY = "AIzaSyDuv0seZBjZeAnGenZw2QaH81_y3M-D-ao";
    private PlaceResult placeResult;
    private String nextPage = "none";

    public void getPlaces(final Context context, final Location location, final PlaceResult placeResult1) {
        this.placeResult = placeResult1;
        final String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + location.getLatitude() + "," + location.getLongitude() +
                "&radius=" + "2000" +
                "&types=" + "hotel|hospital|museum|atm|bus_station|gas_station|taxi_stand|restaurant|pharmacy|store" +
                "hasNextPage=true&" +
                "nextPage()=true" +
                "&sensor=" + "true" +
                "&key=" + API_KEY;
        Log.w("GUrl", "I: " + url);

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<GooglePlace> googlePlaces = new ArrayList<>();
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                googlePlaces.add(parseJSON(jsonArray.getJSONObject(i)));
                            }
                            placeResult.gotPlaces(googlePlaces);
                            if (response.has("next_page_token")) {
                                getAdditionalPlace(context, location, response.getString("next_page_token"));
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
        Volley.newRequestQueue(context).add(jsObjRequest);
    }

    public void getBusPlaces(final Context context, Location location, final PlaceResult placeResult1) {
        this.placeResult = placeResult1;
        String Burl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + location.getLatitude() + "," + location.getLongitude() +
                "&radius=" + "5000" +
                "&types=" + "train_station|bus_station" +
                "&sensor=" + "true" +
                "&key=" + API_KEY;
        Log.w("GUrl", "B: " + Burl);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, Burl,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<GooglePlace> googlePlaces = new ArrayList<>();
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                googlePlaces.add(parseJSON(jsonArray.getJSONObject(i)));
                            }
                            placeResult.gotBusPlaces(googlePlaces);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w("GUrl", error.getLocalizedMessage());
                    }
                });
        Volley.newRequestQueue(context).add(jsObjRequest);
    }

    public void getAdditionalPlace(final Context context, final Location location, final String next) {
        final String Aurl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + location.getLatitude() + "," + location.getLongitude() +
                "&radius=" + "2000" +
                "&types=" + "hotel|hospital|museum|atm|bus_station|gas_station|taxi_stand|restaurant|pharmacy|store" +
                "hasNextPage=true&" +
                "nextPage()=true" +
                "&sensor=" + "true" +
                "&key=" + API_KEY +
                "&pagetoken=" + next;
        Log.w("GUrl", "A: " + Aurl);

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, Aurl,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<GooglePlace> googlePlaces = new ArrayList<>();
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                googlePlaces.add(parseJSON(jsonArray.getJSONObject(i)));
                            }
                            placeResult.gotAdditionalPlaces(googlePlaces);
                            if (response.has("next_page_token")) {
                                getAdditionalPlace(context, location,response.getString("next_page_token"));
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
        Volley.newRequestQueue(context).add(jsObjRequest);
    }

    private GooglePlace parseJSON(JSONObject jsonObject) {
        GooglePlace googlePlace = new GooglePlace();

        try {
            if (jsonObject.has("geometry")) {
                if (jsonObject.getJSONObject("geometry").has("location")) {
                    GLocation gLocation = new GLocation();
                    gLocation.setLat(jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                    gLocation.setLon(jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                    googlePlace.setLocation(gLocation);
                }
            }
            if (jsonObject.has("icon")) {
                googlePlace.setIcon(jsonObject.getString("icon"));
            }
            if (jsonObject.has("place_id")) {
                googlePlace.setPlace_id(jsonObject.getString("place_id"));
            }
            if (jsonObject.has("name")) {
                googlePlace.setName(jsonObject.getString("name"));
            }
            if (jsonObject.has("types")) {
                googlePlace.setType(jsonObject.getJSONArray("types").getString(0));
            }


        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return googlePlace;
    }

    public static abstract class PlaceResult {
        public abstract void gotPlaces(List<GooglePlace> googlePlaces);

        public abstract void gotAdditionalPlaces(List<GooglePlace> googlePlaces);

        public abstract void gotBusPlaces(List<GooglePlace> googlePlaces);
    }
}

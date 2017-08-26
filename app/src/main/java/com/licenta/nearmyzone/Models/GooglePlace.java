package com.licenta.nearmyzone.Models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Morgenstern on 08/25/2017.
 */

public class GooglePlace {

    private String place_id = "";
    private String name = "";
    private String type = "";
    private String icon = "";
    private GLocation location = new GLocation();

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public GLocation getLocation() {
        return location;
    }

    public void setLocation(GLocation location) {
        this.location = location;
    }

    public static String getPlaceID(List<GooglePlace> googlePlaces, LatLng location) {
        for (GooglePlace googlePlace : googlePlaces) {
            if (googlePlace.getLocation().getLat().equals(location.latitude) && googlePlace.getLocation().getLon().equals(location.longitude)) {
                return googlePlace.getPlace_id();
            }
        }
        return null;
    }
}

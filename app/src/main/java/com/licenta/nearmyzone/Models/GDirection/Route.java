package com.licenta.nearmyzone.Models.GDirection;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morgenstern on 08/25/2017.
 */

public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;


    //create route  with JSON from API
    public Route(JSONObject overview_polylineJson, JSONObject jsonDistance, JSONObject jsonDuration, JSONObject jsonLeg, JSONObject jsonStartLocation, JSONObject jsonEndLocation) throws JSONException {

        this.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
        this.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
        this.endAddress = jsonLeg.getString("end_address");
        this.startAddress = jsonLeg.getString("start_address");
        this.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
        this.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
        this.points = decodePolyLine(overview_polylineJson.getString("points"));
    }

    //draw root
    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}

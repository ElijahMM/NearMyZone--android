package com.licenta.nearmyzone.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Morgenstern on 08/22/2017.
 */

public class Util {
    public static void openActivity(Context ctx, Class c) {
        Intent intent = new Intent(ctx, c);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    public static void openActivityClosingStack(Context ctx, Class c) {
        Intent intent = new Intent(ctx, c);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    public static void openActivityClosingParent(Context ctx, Class c) {
        Intent intent = new Intent(ctx, c);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
        ((Activity) ctx).finish();
    }
    //endregion

    //region Toast Region
    public static void showToast(Context ctx, String message) {
        if (ctx != null) {
            try {
                Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void showToast(Context ctx, int message) {
        if (ctx != null) {
            try {
                Toast.makeText(ctx, ctx.getResources().getString(message), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void showShortToast(Context ctx, String message) {
        if (ctx != null) {
            try {
                Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //endregion

    //region Show Log Region
    public static void showObjectLog(Object o) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(o);
        customInfoLog("GSON Object", "Content", json);
    }

    public static void showObjectLog(String objectName, Object o) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(o);
        customInfoLog("GSON " + objectName, "^", json);
    }

    public static void customInfoLog(String activityName, String viewId, int infoMessage) {
        Log.i("--->", " \n");
        Log.i("--->", activityName + "\n---------------------------------------------");
        Log.i("--->" + viewId + "       ", Integer.toString(infoMessage));
        Log.i("--->", "---------------------------------------------\n");
        Log.i("--->", " ");
    }

    public static void customInfoLog(String activityName, String viewId, String infoMessage) {
        Log.i("--->", " \n");
        Log.i("--->", activityName + "\n---------------------------------------------");
        Log.i("--->" + viewId + "       ", infoMessage);
        Log.i("--->", "---------------------------------------------\n");
        Log.i("--->", " ");
    }

    //endregion

    /**
     * @param email email for validation
     * @return validation result
     */
    public static boolean isValidEmail(String email) {
        final String paterm = ".*(\\+[0-9])*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern emailPattern = Pattern.compile(paterm);
        Matcher m = emailPattern.matcher(email);
        return m.matches();
    }

    public static long stringToUnixDate(String stringDate) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Date date = null;
        try {
            date = dateFormat.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long unixDate = (long) date.getTime() / 1000L;
        return unixDate;
    }

    /**
     * conver unix date to date
     *
     * @param unixDate unix date from API
     * @return date in calendar format
     */
    public static Calendar UnixDateToDate(long unixDate) {
        Date date = new Date(unixDate * 1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy "); // the format of your date
        sdf.setTimeZone(TimeZone.getDefault()); // give a timezone reference for formating
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static String UnixDateToString(long unixDate, String dateFormat) {
        Date date = new Date(unixDate * 1000L); // *1000 is to convert seconds to milliseconds
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy "); // the format of your date
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat); // the format of your date
        sdf.setTimeZone(TimeZone.getDefault());

        try {
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Date convertStringToDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        try {
            Date date = null;
            date = format.parse(dateString);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }

    }

    public static String convertDateToString(Date dateValue) throws ParseException {
        SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm dd-MMMM-yyyy");
        String datetime = dateformat.format(dateValue);
        return datetime;
    }

    public static Boolean isLocationEnabled(final Context context) {
        LocationManager locationManager = null;
        boolean gpsIsEnabled = false, networkIsEnabled = false;
        if (locationManager == null)
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        } catch (Exception ex) {
        }
        try {
            networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gpsIsEnabled && !networkIsEnabled) {
            return false;
        }
        return true;
    }


    public static boolean isConnectedToWifi(Context ctx) {
        final ConnectivityManager connMgr = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    /**
     * This function verifies if mobile device is already connected to a wifi network or on mobile data
     */
    public static boolean isConnectedToNet(Context ctx) {
        final ConnectivityManager connMgr = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnected() || mobile.isConnected()) {
            return true;
        }
        return false;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static float distBetweenLatLng(LatLng source, LatLng dest) {
        Location newSource = new Location("");
        newSource.setLatitude(source.latitude);
        newSource.setLongitude(source.longitude);

        Location newDest = new Location("");
        newDest.setLatitude(dest.latitude);
        newDest.setLongitude(dest.longitude);
        return newSource.distanceTo(newDest);
    }

    public static boolean askGpsPermission(final Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return gps_enabled && network_enabled;
    }

    final protected static char[] hexArray = "0123456789abcdef".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String sha1Hash(String toHash) {
//        String hash = null;
//        if (toHash != null && !toHash.isEmpty()) {
//            try {
//                MessageDigest digest = MessageDigest.getInstance("SHA-1");
//                byte[] bytes = toHash.getBytes("UTF-8");
//                digest.update(bytes, 0, bytes.length);
//                bytes = digest.digest();
//
//                // This is ~55x faster than looping and String.formating()
//                hash = bytesToHex(bytes);
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
        return toHash;
    }
}

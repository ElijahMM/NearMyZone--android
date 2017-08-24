package com.licenta.nearmyzone.Handlers;

import android.content.Context;
import android.content.SharedPreferences;

import com.licenta.nearmyzone.AppDelegate;

public class OfflineHandler {

    private static OfflineHandler ourInstance = new OfflineHandler();

    public static OfflineHandler getInstance() {
        return ourInstance;
    }

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String AppPREFERENCES = "ApplicationData";

    private String kPassword = "kPassword";
    private String kEmail = "kEmail";


    private OfflineHandler() {
        Context applicationContext = AppDelegate.getMyContext();
        sharedPreferences = applicationContext.getSharedPreferences(AppPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //region Storing Methods for Objects
    public void storeEmail(String email) {
        editor.putString(kEmail, email);
        editor.commit();
    }

    public void storePassword(String password) {
        editor.putString(kPassword, password);
        editor.commit();
    }


    public String restoreEmail() {
        return sharedPreferences.getString(kEmail, "none");
    }

    public String restorePassword() {
        return sharedPreferences.getString(kPassword, "none");
    }


    public void deletePassword() {
        editor.remove(kPassword);
        editor.commit();
        editor.apply();
    }

    public void deleteEmail() {
        editor.remove(kEmail);
        editor.commit();
        editor.apply();
    }


    public Boolean isEmailStored() {
        return !restoreEmail().equals("none");
    }

    public Boolean isPasswordStored() {
        return !restorePassword().equals("none");
    }
}
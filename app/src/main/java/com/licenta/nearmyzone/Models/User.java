package com.licenta.nearmyzone.Models;

/**
 * Created by Morgenstern on 08/23/2017.
 */

public class User {
    private static final User ourInstance = new User();

    public static User getInstance() {
        return ourInstance;
    }

    private FireBaseUserModel userModel = new FireBaseUserModel();
    private String userPhotoUrl = "";
    private User() {
    }

    public FireBaseUserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(FireBaseUserModel userModel) {
        this.userModel = userModel;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }
}

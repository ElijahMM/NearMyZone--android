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

    private User() {
    }

    public FireBaseUserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(FireBaseUserModel userModel) {
        this.userModel = userModel;
    }


}

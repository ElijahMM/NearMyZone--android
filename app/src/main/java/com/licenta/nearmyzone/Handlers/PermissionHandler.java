package com.licenta.nearmyzone.Handlers;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by Morgenstern on 08/22/2017.
 */

public class PermissionHandler {
    private static PermissionHandler ourInstance = new PermissionHandler();
    private Activity targetActivity;

    private static final String[] PHONE_STATE_PERM = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int PHONE_PERMS = 1337;

    public static PermissionHandler getInstance() {
        return ourInstance;
    }

    private PermissionHandler() {
    }

    public Boolean checkPermissions(Activity thisActivity) {
        this.targetActivity = thisActivity;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!allPermissionOK()) {
                this.targetActivity.requestPermissions(PHONE_STATE_PERM, PHONE_PERMS);
                return false;
            }
        }
        return true;
    }

    private Boolean allPermissionOK() {
        for (String permission : PHONE_STATE_PERM) {
            if (!hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private Boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == this.targetActivity.checkSelfPermission(perm));
    }

}

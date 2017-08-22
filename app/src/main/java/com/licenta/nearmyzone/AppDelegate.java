package com.licenta.nearmyzone;

import android.app.Application;
import android.content.Context;

/**
 * Created by Morgenstern on 08/22/2017.
 */

public class AppDelegate extends Application {

    Context myContext;

    @Override
    public void onCreate() {
        super.onCreate();
        myContext = this;
    }

    public Context getMyContext() {
        return myContext;
    }

}

package com.licenta.nearmyzone.Activities;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.licenta.nearmyzone.R;
import com.licenta.nearmyzone.Utils.Util;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Util.openActivityClosingStack(SplashScreen.this, LoginActivity.class);
            }
        }, 2000);
    }
}

package com.jamesg.forecastr.SplashScreen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.jamesg.forecastr.MainActivity;
import com.jamesg.forecastr.R;
import com.jamesg.forecastr.ForecastrApplication;
import com.jamesg.forecastr.manager.SpotManager;
import com.jamesg.forecastr.utils.Logger;
import com.splunk.mint.Mint;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by James on 17/05/2014.
 */
public class SplashScreen extends Activity {

    @Inject
    SpotManager spotManager;

    @Inject
    Bus bus;

    private static int SPLASH_TIME_OUT = 5000;

    private Handler handler;

    private Boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((ForecastrApplication) getApplication()).inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);

        spotManager.getAllSpots(1);
        spotManager.checkForUpdates(true);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!started) {
                    started = true;

                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);

                    Logger.d("Starting Main Activity Waiting");
                    // close this activity
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    @Subscribe
    public void getMessage(String s) {
        if(s.equals("Update Finished") && !started){
            started = true;
            handler.removeCallbacksAndMessages(null);

            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(i);

            Logger.d("Starting Main Activity");
            finish();
        }
    }

    @Override
    public void onStop() {
        super.onPause();
        bus.unregister(this);
    }
}

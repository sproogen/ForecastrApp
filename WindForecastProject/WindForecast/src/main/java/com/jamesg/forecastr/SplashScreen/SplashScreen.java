package com.jamesg.forecastr.SplashScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.jamesg.forecastr.MainActivity;
import com.jamesg.forecastr.R;
import com.jamesg.forecastr.WindFinderApplication;
import com.jamesg.forecastr.manager.SpotManager;

import javax.inject.Inject;

/**
 * Created by James on 17/05/2014.
 */
public class SplashScreen extends Activity {

    @Inject
    SpotManager spotManager;

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((WindFinderApplication) getApplication()).inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                spotManager.getAllSpots(1);

                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}

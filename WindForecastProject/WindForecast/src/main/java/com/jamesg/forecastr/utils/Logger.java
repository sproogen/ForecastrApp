package com.jamesg.forecastr.utils;

import android.util.Log;

import com.jamesg.forecastr.BuildConfig;

/**
 * Android Log wrapper class that can use {@link String#format(String, Object...)} in logging message
 */
public class Logger {

    private static final String TAG = "WINDFINDER APP";
    private static final String EMPTY = "";
    /**
     * Send a DEBUG log message.
     * @param msg
     * @return
     */
    public static int d(String msg) {
        if (BuildConfig.LOG_ENABLED) {
            Log.d(TAG, msg);
        }
        return 1;
    }

    /**
     * Send a ERROR log message.
     * @param msg
     * @return
     */
    public static int e(String msg) {
        if (BuildConfig.LOG_ENABLED) {
            Log.e(TAG, msg);
        }
        return 1;
    }
}
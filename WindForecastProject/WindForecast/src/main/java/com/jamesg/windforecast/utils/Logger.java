package com.jamesg.windforecast.utils;

import android.util.Log;

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
        return Log.d(TAG, msg);
    }

    /**
     * Send a ERROR log message.
     * @param msg
     * @return
     */
    public static int e(String msg) {
        return Log.e(TAG, msg);
    }
}
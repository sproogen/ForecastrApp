package com.jamesg.forecastr;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Created by James on 17/10/2014.
 */
public class ForecastrApplication extends Application implements Injectable {

    // The following line should be changed to include the correct property id.
    private static final String PROPERTY_ID = "UA-41673510-2";

    public static int GENERAL_TRACKER = 0;

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    private ObjectGraph objectGraph;

    private static ForecastrApplication self;

    @Override
    public void onCreate() {
        super.onCreate();

        self = this;

        objectGraph = ObjectGraph.create(getModules().toArray());

    }

    public  List<Object> getModules() {
        return Arrays.<Object>asList(new AppModule(this));
    }

    @Override
    public void inject(Object o) {
        objectGraph.inject(o);
    }

    public static Injectable getInjectable(Context context) {
        return (Injectable) context.getApplicationContext();
    }

    public void setApplicationGraph(ObjectGraph objectGraph) {
        this.objectGraph = objectGraph;
    }

    public static ForecastrApplication app() {
        return self;
    }

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.app_tracker)
            : analytics.newTracker(PROPERTY_ID);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }


}

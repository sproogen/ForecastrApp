package com.jamesg.windforecast;

import android.app.Application;
import android.content.Context;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Created by James on 17/10/2014.
 */
public class WindFinderApplication extends Application implements Injectable {

    private ObjectGraph objectGraph;

    private static WindFinderApplication self;

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

    public static WindFinderApplication app() {
        return self;
    }


}

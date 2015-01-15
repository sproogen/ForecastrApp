package com.jamesg.forecastr;

import com.google.android.gms.analytics.Tracker;
import com.jamesg.forecastr.SplashScreen.SplashScreen;
import com.jamesg.forecastr.SpotFragment.FavouritesFragment;
import com.jamesg.forecastr.SpotFragment.SpotFragment;
import com.jamesg.forecastr.base.BaseSpotFragment;
import com.jamesg.forecastr.manager.AppManager;
import com.jamesg.forecastr.manager.SpotManager;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by James on 17/10/2014.
 */
@Module(
        injects = {
                SpotFragment.class,
                MainActivity.class,
                DrawerFragment.class,
                SplashScreen.class,
                FavouritesFragment.class,
                AboutFragment.class,
                SpotManager.class,
                BaseSpotFragment.class,
                AppManager.class
        }
        ,library = true
)
public class AppModule {

    protected ForecastrApplication app;

    public AppModule(ForecastrApplication app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public SpotManager provideSpotManager() {
        return new SpotManager(app);
    }

    @Provides
    @Singleton
    public AppManager provideAppManager() {
        return new AppManager(app);
    }

    @Provides
    @Singleton
    public Tracker provideTracker() {
        return app.getTracker(ForecastrApplication.TrackerName.APP_TRACKER);
    }

    @Provides
    @Singleton
    public Bus provideBus() {
        return new Bus();
    }

}

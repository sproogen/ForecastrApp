package com.jamesg.windforecast;

import android.app.Application;

import com.jamesg.windforecast.SplashScreen.SplashScreen;
import com.jamesg.windforecast.SpotFragment.SpotFragment;
import com.jamesg.windforecast.manager.SpotManager;

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
                SplashScreen.class
        }
        ,library = true
)
public class AppModule {

    protected WindFinderApplication app;

    public AppModule(WindFinderApplication app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public SpotManager provideSpotManager() {
        return new SpotManager(app);
    }

}

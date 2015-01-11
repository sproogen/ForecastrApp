package com.jamesg.windforecast;

import com.jamesg.windforecast.SplashScreen.SplashScreen;
import com.jamesg.windforecast.SpotFragment.FavouritesFragment;
import com.jamesg.windforecast.SpotFragment.SpotFragment;
import com.jamesg.windforecast.base.BaseSpotFragment;
import com.jamesg.windforecast.manager.AppManager;
import com.jamesg.windforecast.manager.SpotManager;
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

    protected WindFinderApplication app;

    public AppModule(WindFinderApplication app) {
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
    public Bus provideBus() {
        return new Bus();
    }

}

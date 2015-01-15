package com.jamesg.forecastr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.jamesg.forecastr.base.BaseActivity;
import com.jamesg.forecastr.base.BaseFragment;
import com.jamesg.forecastr.SpotFragment.SpotWrapperFragment;
import com.jamesg.forecastr.data.Spot;
import com.jamesg.forecastr.manager.AppManager;
import com.jamesg.forecastr.manager.SpotManager;
import com.jamesg.forecastr.utils.Logger;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.splunk.mint.Mint;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Stack;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements BaseFragment.BaseFragmentInteractionListener {

    static final int SPOTS_FRAGMENT = 0;
    static final int ABOUT_FRAGMENT = 1;

    @Inject
    SpotManager spotManager;

    @Inject
    AppManager appManager;

    @Inject
    Tracker tracker;

    @Inject
    Bus bus;

    private Stack<BaseFragment> stack;
    private BaseFragment current;
    private int currentID;

    private int dateTab = 0;

    private SlidingMenu menu;

    private String openSpot = "";


    public MainActivity() {
        super(R.string.app_name);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((ForecastrApplication) getApplication()).inject(this);
        super.onCreate(savedInstanceState);

        Mint.initAndStartSession(MainActivity.this, "91e59a0e");
        ((ForecastrApplication) getApplication())
                .getTracker(ForecastrApplication.TrackerName.APP_TRACKER);

        setTitle("Favourite Spots");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);

        stack = new Stack<BaseFragment>();

        if (savedInstanceState != null) {
            current = (SpotWrapperFragment) getSupportFragmentManager().findFragmentByTag("currentFragment");
        }else{
            current = SpotWrapperFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, current,"currentFragment")
                    .commit();
            currentID = SPOTS_FRAGMENT;
        }

        setSlidingActionBarEnabled(true);

        SlidingMenu menu = getSlidingMenu();
        menu.setOnClosedListener(new SlidingMenu.OnClosedListener() {
            @Override
            public void onClosed() {
                //drawerFragment.clearSearch();
                //getActionBar().setDisplayHomeAsUpEnabled(false);
            }
        });

        menu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
               // getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });

        //deleteDatabase("spotsTable");

        /*if(spotManager.getSpot("Aberavon") == null){
            spotManager.addSpot(new Spot("Aberavon",322661));
        }
        if(spotManager.getSpot("Portland Harbour") == null){
            spotManager.addSpot(new Spot("Portland Harbour",354618));
        }
        if(spotManager.getSpot("Westward Ho!") == null){
            spotManager.addSpot(new Spot("Westward Ho!",354583));
        }
        if(spotManager.getSpot("Westbury") == null){
            spotManager.addSpot(new Spot("Westbury",354152));
        }*/

        spotManager.checkForUpdates(false);
        appManager.checkForUpdates();
    }
    @Subscribe
    public void getMessage(String s) {
        if(s.equals("NewAppAvailable")){
            try {
                new AlertDialog.Builder(this)
                        .setTitle("New Version of the app is available.")
                        .setMessage("Version " + appManager.getVersionName() + " is available now. Would you like to update to this version now.")
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url = getString(R.string.base_url)+"releases/latest/WindForecastApp.apk";
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        })
                        .setNegativeButton("Later", null)
                        .show();
            }catch(Exception e){
                //DO NOTHING
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Mint.startSession(MainActivity.this);
        bus.register(this);
        spotManager.checkForUpdates(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Mint.flush();
        Mint.closeSession(MainActivity.this);
        bus.unregister(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    public void removeSpot(Spot spot){
        spotManager.deleteSpot(spot);
        //mainContentFragment.removeSpot(spot.getName());
        drawerFragment.refreshSpots();
        if(currentID == SPOTS_FRAGMENT && openSpot.equals("")){
            ((SpotWrapperFragment)current).removeSpot(spot);
        }
        if(spot.getName().equals(openSpot)){
            allSpots(false);
        }
        Mint.logEvent("Spot Removed - "+spot.getName());
        tracker.setScreenName(null);
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Favourites")
                .setAction("Removed - "+spot.getName())
                .build());
    }

    public void addSpot(String name){
        Spot newSpot = spotManager.getSpot(name);
        if(newSpot == null){
            Toast.makeText(this, "Error : Could not add "+ name + " to favourites.", Toast.LENGTH_SHORT).show();
        }else {
            spotManager.addSpot(newSpot);
            drawerFragment.refreshSpots();
            drawerFragment.setSpot(name);
            spotManager.parseSpotData(newSpot.getName());
            spotManager.checkForUpdates(false);
            Toast.makeText(this, name + " added to favourites.", Toast.LENGTH_SHORT).show();
            Mint.logEvent("Spot Added - " + name);
            tracker.setScreenName(null);
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Spot")
                    .setAction("Added - "+name)
                    .build());
        }
    }

    public void loadSpot(String name, int listClick){
        popBackStack(false);
        openSpot = name;
        ((SpotWrapperFragment)current).loadSpot(name, true, false);
        drawerFragment.setSpot(name);
        Mint.logEvent("Spot Viewed - "+name);
        tracker.setScreenName(null);
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Spot")
                .setAction("Viewed - "+name)
                .build());
    }

    public void loadSearchSpot(String name, int id){
        popBackStack(false);
        boolean search = false;
        if(!spotManager.spotExists(name)) {
            spotManager.searchSpot(new Spot(name, id));
            search = true;
        }
        openSpot = name;
        ((SpotWrapperFragment)current).loadSpot(name, true, search);
        drawerFragment.setSpot(name);
        Mint.logEvent("Spot Searched - "+name);
        tracker.setScreenName(null);
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Search")
                .setAction("Searched - "+name)
                .build());
    }

    public void allSpots(boolean animate){
        popBackStack(animate);
        if(!openSpot.equals("")) {
            openSpot = "";
            ((SpotWrapperFragment)current).closeSpot(animate);
        }
        drawerFragment.closeSpot();
    }

    @Override
    public String openSpot() {
        return openSpot;
    }

    public void transitionToFragment(BaseFragment newFragment, int id, boolean animate){
        stack.push(current);
        current = newFragment;
        currentID = id;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        //transaction.setCustomAnimations(R.anim.no_anim_in, R.anim.exit, R.anim.no_anim_in, R.anim.pop_exit);
        //transaction.setCustomAnimations(R.anim.no_anim_in, R.anim.no_anim_out, R.anim.no_anim_in, R.anim.no_anim_out);

        transaction.replace(R.id.content_frame, current).commit();
        getSupportFragmentManager().executePendingTransactions();

    }

    public void popBackStack(boolean animate){
        if(!stack.empty()) {
            current = stack.pop();
            drawerFragment.closeSpot();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if(animate){
                transaction.setCustomAnimations(R.anim.close_enter, R.anim.close_exit);
            }

            transaction.replace(R.id.content_frame, current, "spotWrapperFragment").commit();
            getSupportFragmentManager().executePendingTransactions();
            currentID = SPOTS_FRAGMENT;
        }
    }

    @Override
    public int getDateTab() {
        return dateTab;
    }

    @Override
    public void setDateTab(int dateTab) {
        this.dateTab = dateTab;
    }

    @Override
    public void onBackPressed() {
        if(!stack.empty()) {
            popBackStack(true);
        }else if(openSpot.equals("")) {
            super.onBackPressed();
        }else{
            allSpots(true);
        }
    }
}

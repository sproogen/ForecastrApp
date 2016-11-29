package com.jamesg.forecastr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
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
import com.splunk.mint.Mint;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.Stack;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements BaseFragment.BaseFragmentInteractionListener {

    static final String SPOTS_FRAGMENT = "spots";
    static final String ABOUT_FRAGMENT = "about";

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
    private String currentID;

    private int dateTab = 0;

    private String openSpot = "";

    public MainActivity() {
        super(R.string.app_name);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((ForecastrApplication) getApplication()).inject(this);
        super.onCreate(savedInstanceState);

        Logger.d("Main Activity Started");

        if (BuildConfig.SPLUNK_ENABLED) {
            Mint.initAndStartSession(MainActivity.this, "91e59a0e");
        }

        setTitle("Favourite Spots");

        stack = new Stack<>();

        spotManager.getAllSpots(1);

        addSpotsToNavigation();

        transitionToFragment(SpotWrapperFragment.newInstance(), SPOTS_FRAGMENT, true);
        navigationView.setCheckedItem(R.id.nav_home);

        //deleteDatabase("spotsTable");

        spotManager.checkForUpdates(true);
        appManager.checkForUpdates();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        // FOR NAVIGATION VIEW ITEM TEXT COLOR
        int[][] state = new int[][] {
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed

        };

        int[] color = new int[] {
                ContextCompat.getColor(this, R.color.dark_grey),
                ContextCompat.getColor(this, R.color.dark_grey),
                ContextCompat.getColor(this, R.color.dark_grey),
                ContextCompat.getColor(this, R.color.dark_grey)
        };

        ColorStateList csl = new ColorStateList(state, color);

        navigationView.setItemTextColor(csl);

    }

    public void addSpotsToNavigation() {
        List<Spot> spots = spotManager.getAllSpots(0);

        Menu navigationMenu = navigationView.getMenu();
        SubMenu favouritesMenu = navigationMenu.findItem(R.id.favourites).getSubMenu();

        int order = 0;
        for (Spot spot : spots) {
            favouritesMenu.add(R.id.favouritesGroup, spot.getId(), order, spot.getName());
            order++;
        }

        favouritesMenu.setGroupCheckable(R.id.favouritesGroup, true, true);
    }

    @Override
    public void navItemSelected(MenuItem menuItem){
        drawer.closeDrawers();

        BaseFragment fragment = null;
        String itemId = null;

        navigationView.setCheckedItem(menuItem.getItemId());

        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                fragment = (SpotWrapperFragment) getSupportFragmentManager().findFragmentByTag(SPOTS_FRAGMENT);
                if (fragment == null) {
                    fragment = SpotWrapperFragment.newInstance();
                }
                itemId = SPOTS_FRAGMENT;
                tabLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.about:
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(ABOUT_FRAGMENT);
                if (fragment == null) {
                    fragment = new AboutFragment();
                }
                itemId = ABOUT_FRAGMENT;
                tabLayout.setVisibility(View.GONE);
                break;
            case R.id.check_updates:
                Snackbar.make(coordinatorLayout, "Checking for updates", Snackbar.LENGTH_SHORT).show();
                break;
            default:
                fragment = (SpotWrapperFragment) getSupportFragmentManager().findFragmentByTag(SPOTS_FRAGMENT);
                if (fragment == null) {
                    fragment = SpotWrapperFragment.newInstance();
                }
                itemId = SPOTS_FRAGMENT;
                tabLayout.setVisibility(View.VISIBLE);
        }

        if (fragment != null) {
            transitionToFragment(fragment, itemId, true);
        }

        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                allSpots(true);
                break;
            case R.id.about:
                break;
            case R.id.check_updates:
                break;
            default:
                loadSpot(String.valueOf(menuItem.getTitle()), 1);
        }
    }

    @Override
    public void tabSelected(TabLayout.Tab tab) {
        this.dateTab = Integer.parseInt(String.valueOf(tab.getTag()));
        SpotWrapperFragment spotFragment = (SpotWrapperFragment) getSupportFragmentManager().findFragmentByTag(SPOTS_FRAGMENT);
        if (spotFragment != null) {
            spotFragment.updateDateTab(this.dateTab);
        }
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
        if (BuildConfig.SPLUNK_ENABLED) {
            Mint.startSession(MainActivity.this);
        }
        bus.register(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (BuildConfig.SPLUNK_ENABLED) {
            Mint.flush();
            Mint.closeSession(MainActivity.this);
        }
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
        //drawerFragment.refreshSpots();
        if(currentID == SPOTS_FRAGMENT && openSpot.equals("")){
            ((SpotWrapperFragment)current).removeSpot(spot);
        }
        if(spot.getName().equals(openSpot)){
            allSpots(false);
        }
        tracker.setScreenName(null);
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Favourite Removed")
                .setAction(spot.getName())
                .build());
    }

    public void addSpot(String name){
        Spot newSpot = spotManager.getSpot(name);
        if(newSpot == null){
            Toast.makeText(this, "Error : Could not add "+ name + " to favourites.", Toast.LENGTH_SHORT).show();
        }else {
            spotManager.addSpot(newSpot);
            //drawerFragment.refreshSpots();
            //drawerFragment.setSpot(name);
            spotManager.parseSpotData(newSpot.getName());
            Toast.makeText(this, name + " added to favourites.", Toast.LENGTH_SHORT).show();
            tracker.setScreenName(null);
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Favourite Added")
                    .setAction(name)
                    .build());
        }
    }

    public void loadSpot(String name, int listClick){
        openSpot = name;
        ((SpotWrapperFragment)current).loadSpot(name, true, false);
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
        //drawerFragment.setSpot(name);
        tracker.setScreenName(null);
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Search")
                .setAction(name)
                .build());
    }

    public void allSpots(boolean animate){
        popBackStack(animate);
        if(!openSpot.equals("")) {
            openSpot = "";
            ((SpotWrapperFragment)current).closeSpot(animate);
        }
        //drawerFragment.closeSpot();
    }

    @Override
    public String openSpot() {
        return openSpot;
    }

    @Override
    public void toggle() {

    }

    @Override
    public void toggle(boolean animate) {

    }

    public void transitionToFragment(final BaseFragment newFragment, final String id, boolean animate){
        if (current != null) {
            stack.push(current);
        }
        current = newFragment;
        currentID = id;

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (!newFragment.isAdded()) {
                        // update the main content by replacing fragments
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentTransaction.replace(R.id.frame, newFragment, id).commit();
                        getSupportFragmentManager().executePendingTransactions();
                    }
                } catch (Exception e) {
                    //MEH
                }
            }
        };

        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
    }

    public void popBackStack(boolean animate){
        if(!stack.empty()) {
            try {
                current = stack.pop();
                if (current != null) {
                    //drawerFragment.closeSpot();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                    if (animate) {
                        transaction.setCustomAnimations(R.anim.close_enter, R.anim.close_exit);
                    }

                    //transaction.replace(R.id.content_frame, current, "spotWrapperFragment").commit();
                    getSupportFragmentManager().executePendingTransactions();
                    currentID = SPOTS_FRAGMENT;
                }
            }catch (Exception e){
                //Failed to pop and replace current fragment. Do Nothing
            }
        }
    }

    @Override
    public int getDateTab() {
        return dateTab;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        Log.d("BACKSTACK", openSpot);
        if(!stack.empty()) {
            //When on the About or similar fragment.
            popBackStack(true);
            return;
        }else if(!openSpot.equals("")){
            //When on a spot fragment.
            allSpots(true);
            return;
        }
        super.onBackPressed();
    }
}

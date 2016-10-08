package com.jamesg.forecastr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
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

    private SpotWrapperFragment spotFragment;

    public MainActivity() {
        super(R.string.app_name);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((ForecastrApplication) getApplication()).inject(this);
        super.onCreate(savedInstanceState);

        Logger.d("Main Activity Started");

        Mint.initAndStartSession(MainActivity.this, "91e59a0e");

        setTitle("Favourite Spots");

        stack = new Stack<>();

        //spotManager.getAllSpots(1);

//        if (savedInstanceState != null) {
//            current = (SpotWrapperFragment) getSupportFragmentManager().findFragmentByTag("currentFragment");
//        }else{
//            current = SpotWrapperFragment.newInstance();
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.content_frame, current,"currentFragment")
//                    .commit();
//            currentID = SPOTS_FRAGMENT;
//        }

        transitionToFragment(SpotWrapperFragment.newInstance(), SPOTS_FRAGMENT, true);
        navigationView.setCheckedItem(R.id.nav_home);

        //deleteDatabase("spotsTable");

        //spotManager.checkForUpdates(true);
        //appManager.checkForUpdates();
    }

    @Override
    public void navItemSelected(MenuItem menuItem){
        drawer.closeDrawers();

        BaseFragment fragment;
        String itemId;

        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                if (spotFragment == null) {
                    spotFragment = SpotWrapperFragment.newInstance();
                }
                fragment = spotFragment;
                itemId = SPOTS_FRAGMENT;
                break;
            case R.id.about:
                fragment = new AboutFragment();
                itemId = ABOUT_FRAGMENT;
                break;
            default:
                fragment = new AboutFragment();
                itemId = ABOUT_FRAGMENT;
        }

        transitionToFragment(fragment, itemId, true);
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
            drawerFragment.refreshSpots();
            drawerFragment.setSpot(name);
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
        popBackStack(false);
        openSpot = name;
        ((SpotWrapperFragment)current).loadSpot(name, true, false);
        drawerFragment.setSpot(name);
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
        drawerFragment.closeSpot();
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
        stack.push(current);
        current = newFragment;
        currentID = id;

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, newFragment, id);
                fragmentTransaction.commitAllowingStateLoss();
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
                    drawerFragment.closeSpot();
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
    public void setDateTab(int dateTab) {
        this.dateTab = dateTab;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!stack.empty()) {
            //When on the About or similar fragment.
            popBackStack(true);
        }else if(openSpot.equals("")) {
            //When on favourite fragment.
            super.onBackPressed();
        }else{
            //When on a spot fragment.
            allSpots(true);
        }
    }
}

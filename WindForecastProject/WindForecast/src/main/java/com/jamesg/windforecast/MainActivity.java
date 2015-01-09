package com.jamesg.windforecast;

import android.os.Message;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.jamesg.windforecast.base.BaseActivity;
import com.jamesg.windforecast.base.BaseFragment;
import com.jamesg.windforecast.SpotFragment.SpotWrapperFragment;
import com.jamesg.windforecast.data.Spot;
import com.jamesg.windforecast.manager.SpotManager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.splunk.mint.Mint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Stack;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements BaseFragment.BaseFragmentInteractionListener {

    static final int SPOTS_FRAGMENT = 0;
    static final int ABOUT_FRAGMENT = 1;

    @Inject
    SpotManager spotManager;

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
        ((WindFinderApplication) getApplication()).inject(this);
        super.onCreate(savedInstanceState);

        Mint.initAndStartSession(MainActivity.this, "91e59a0e");

        setContentView(R.layout.content_frame);

        setTitle("Favourite Spots");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);

        stack = new Stack<BaseFragment>();

        if (savedInstanceState != null) {
            //current = (SpotWrapperFragment) getSupportFragmentManager().findFragmentByTag("spotWrapperFragment");
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
                drawerFragment.clearSearch();
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
    }

    public void addSpot(String name){
        Spot newSpot = spotManager.getSpot(name);
        spotManager.addSpot(newSpot);
        drawerFragment.refreshSpots();
        spotManager.checkForUpdates(false);
        Toast.makeText(this, name + "added to favourites.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        spotManager.checkForUpdates(false);
    }

    public void loadSpot(String name, int listClick){
        popBackStack(false);
        openSpot = name;
        ((SpotWrapperFragment)current).loadSpot(name, true, false);
        drawerFragment.setSpot(name);
    }

    public void loadSearchSpot(String name, int id){
        popBackStack(false);
        spotManager.searchSpot(new Spot(name, id));
        openSpot = name;
        ((SpotWrapperFragment)current).loadSpot(name, true, true);
        drawerFragment.setSpot(name);
    }

    public void allSpots(boolean animate){
        popBackStack(false);
        if(!openSpot.equals("")) {
            openSpot = "";
            ((SpotWrapperFragment)current).closeSpot(animate);
        }
        drawerFragment.closeSpot();
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

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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements BaseFragment.BaseFragmentInteractionListener {

    @Inject
    SpotManager spotManager;

    private SpotWrapperFragment spotWrapperFragment;
    private AboutFragment aboutFragment;

    private int dateTab = 0;

    private SlidingMenu menu;

    private String openSpot = "";

    private boolean aboutOpen = false;


    public MainActivity() {
        super(R.string.app_name);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((WindFinderApplication) getApplication()).inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_frame);

        setTitle("Favourite Spots");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);

        if (savedInstanceState != null) {
            spotWrapperFragment = (SpotWrapperFragment) getSupportFragmentManager().findFragmentByTag("spotWrapperFragment");
        }else{
            spotWrapperFragment = SpotWrapperFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, spotWrapperFragment,"spotWrapperFragment")
                    .commit();
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

        if(spotManager.getSpot("Aberavon") == null){
            spotManager.addSpot(new Spot("Aberavon",322661));
        }
        /*if(data.getSpot("Portland Harbour") == null){
            data.addSpot(new Spot("Portland Harbour",354618));
        }
        if(data.getSpot("Westward Ho!") == null){
            data.addSpot(new Spot("Westward Ho!",354583));
        }
        if(data.getSpot("Westbury") == null){
            data.addSpot(new Spot("Westbury",354152));
        }*/

        //checkForUpdates(false);
    }

    public void removeSpot(Spot spot){
        spotManager.deleteSpot(spot);
        //mainContentFragment.removeSpot(spot.getName());
        this.refreshDraw();
        if(spot.getName().equals(openSpot)){
            closeSpot(false);
        }
    }

    public void addSpot(String name){
        Spot newSpot = spotManager.getSpot(name);
        spotManager.addSpot(newSpot);
        //mainContentFragment.addSpot(name);
        checkForUpdates(false);
        Toast.makeText(this, name + "added to favourites.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForUpdates(false);
    }

    public void checkForUpdates(boolean force){
        final boolean forceUpdate = force;
        Thread background = new Thread(new Runnable() {

            public void run() {
                for (Spot s : spotManager.getAllSpots(0)){
                    Log.d("WINDFINDER APP", "Checking Spot - "+s.getName());
                    if(s.getRawData() == null || (System.currentTimeMillis()-s.getUpdateTime()) > 3600000 || forceUpdate){ //3600000
                        spotManager.get_data_for_location(s);
                        threadMsg(s.getName());
                    }else{
                        SimpleDateFormat formatter = new SimpleDateFormat("D");
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(s.getUpdateTime());
                        String updatedDay = formatter.format(calendar.getTime());
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        String nowDay = formatter.format(calendar.getTime());
                        if(!updatedDay.equals(nowDay)){
                            spotManager.get_data_for_location(s);
                            threadMsg(s.getName());
                        }
                    }
                }
            }

            private void threadMsg(String msg) {
                if (!msg.equals(null) && !msg.equals("")) {
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", msg);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                }
            }

            private final Handler handler = new Handler() {
                public void handleMessage(Message msg) {
                    String spotName = msg.getData().getString("message");
                    Log.d("WINDFINDER APP", "Updated Spot - "+spotName);
                    //mainContentFragment.updateSpot(spotName);
                }
            };
        });
        background.start();
    }

    public void loadSpot(String name, int listClick){
        closeAbout(false);
        openSpot = name;
        //mainContentFragment.loadSpot(name,listClick, 0);
        drawerFragment.setSpot(name);
    }

    public void loadSearchSpot(String name, int id){
        closeAbout(false);
        spotManager.searchSpot(new Spot(name, id));
        openSpot = name;
        //mainContentFragment.loadSpot(name,1 , 1);
        //drawerFragment.setSpot(name);
    }

    public void closeSpot(boolean animate){
        closeAbout(false);
        openSpot = "";
        //mainContentFragment.closeSpot(animate);
        drawerFragment.closeSpot();
    }

    public void about(){
        aboutOpen = true;
        aboutFragment = new AboutFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        //transaction.setCustomAnimations(R.anim.no_anim_in, R.anim.exit, R.anim.no_anim_in, R.anim.pop_exit);

        //transaction.setCustomAnimations(R.anim.no_anim_in, R.anim.no_anim_out, R.anim.no_anim_in, R.anim.no_anim_out);

        transaction.replace(R.id.content_frame, aboutFragment).commit();
        getSupportFragmentManager().executePendingTransactions();

    }

    public void closeAbout(boolean animate){
        if(aboutOpen) {
            aboutOpen = false;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if(animate){
                transaction.setCustomAnimations(R.anim.close_enter, R.anim.close_exit);
                drawerFragment.closeSpot();
            }

            transaction.replace(R.id.content_frame, spotWrapperFragment, "spotWrapperFragment").commit();
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
        if(aboutOpen) {
            closeAbout(true);
        }else if(openSpot.equals("")) {
            super.onBackPressed();
        }else{
            closeSpot(true);
        }
    }
}

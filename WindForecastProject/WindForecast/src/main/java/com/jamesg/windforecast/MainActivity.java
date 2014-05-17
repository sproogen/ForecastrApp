package com.jamesg.windforecast;

import android.os.Message;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jamesg.windforecast.data.Data;
import com.jamesg.windforecast.data.Spot;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends BaseActivity {

    private MainContentFragment mainContentFragment;

    public static Data data;

    private SlidingMenu menu;

    private String openSpot = "";

    private int overviewDisplay = 0; // 0 = today
                                     // 1 = tomorrow
                                     // 2 = 7 day

    public MainActivity() {
        super(R.string.app_name);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Favourite Spots");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);

        if(data == null){
            data = new Data(this);
        }

        if (savedInstanceState != null) {
            this.overviewDisplay = savedInstanceState.getInt("overviewDisplay");
            mainContentFragment = (MainContentFragment) getSupportFragmentManager().findFragmentByTag("mainContentFragment");
        }else{
            mainContentFragment = new MainContentFragment(overviewDisplay);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, mainContentFragment,"mainContentFragment")
                    .commit();
        }

        // set the Above View
        setContentView(R.layout.content_frame);

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

        /*if(data.getSpot("Aberavon") == null){
            data.addSpot(new Spot("Aberavon",322661));
        }
        if(data.getSpot("Portland Harbour") == null){
            data.addSpot(new Spot("Portland Harbour",354618));
        }
        if(data.getSpot("Westward Ho!") == null){
            data.addSpot(new Spot("Westward Ho!",354583));
        }
        if(data.getSpot("Westbury") == null){
            data.addSpot(new Spot("Westbury",354152));
        }*/

        checkForUpdates(false);
    }

    public void removeSpot(Spot spot){
        data.deleteSpot(spot);
        mainContentFragment.removeSpot(spot.getName());
        this.refreshDraw();
        if(spot.getName().equals(openSpot)){
            closeSpot();
        }
    }

    public void addSpot(String name){
        Spot newSpot = data.getSpot(name);
        data.addSpot(newSpot);
        mainContentFragment.addSpot(name);
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
                for (Spot s : MainActivity.data.getAllSpots(0)){
                    Log.d("WINDFINDER APP", "Checking Spot - "+s.getName());
                    if(s.getRawData() == null || (System.currentTimeMillis()-s.getUpdateTime()) > 3600000 || forceUpdate){ //3600000
                        data.get_data_for_location(s);
                        threadMsg(s.getName());
                    }else{
                        SimpleDateFormat formatter = new SimpleDateFormat("D");
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(s.getUpdateTime());
                        String updatedDay = formatter.format(calendar.getTime());
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        String nowDay = formatter.format(calendar.getTime());
                        if(!updatedDay.equals(nowDay)){
                            data.get_data_for_location(s);
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
                    mainContentFragment.updateSpot(spotName);
                }
            };
        });
        background.start();
    }

    public void overviewTypeOnClick(View v) {
        //Log.d("WINDFINDER APP", v.getId()+"");
        if(v.getId() == R.id.todaySelector) overviewDisplay = 0;
        else if(v.getId() == R.id.tomorrowSelector) overviewDisplay = 1;
        else if(v.getId() == R.id.sevenDaySelector) overviewDisplay = 2;

        mainContentFragment.updateOverviewDisplay(overviewDisplay);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("overviewDisplay", overviewDisplay);
    }

    public void loadSpot(String name, int listClick){
        openSpot = name;
        mainContentFragment.loadSpot(name,0);
        drawerFragment.setSpot(name);
    }

    public void loadSearchSpot(String name, int id){
        data.searchSpot(new Spot(name, id));
        openSpot = name;
        mainContentFragment.loadSpot(name, 1);
        //drawerFragment.setSpot(name);
    }

    public void closeSpot(){
        openSpot = "";
        mainContentFragment.closeSpot();
        drawerFragment.closeSpot();
    }

    @Override
    public void onBackPressed() {
        if(openSpot.equals("")){
            super.onBackPressed();
        }else{
            closeSpot();
        }
    }
}

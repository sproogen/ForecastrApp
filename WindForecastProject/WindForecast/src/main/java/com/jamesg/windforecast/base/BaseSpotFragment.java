package com.jamesg.windforecast.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;

import com.jamesg.windforecast.WindFinderApplication;
import com.jamesg.windforecast.cards.HeaderCard;
import com.jamesg.windforecast.cards.WindCard;
import com.jamesg.windforecast.data.Spot;
import com.jamesg.windforecast.utils.Logger;

import java.util.ArrayList;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class BaseSpotFragment extends BaseFragment {

    public BaseSpotFragment() {
        // Required empty public constructor
    }

    public void updateDateTab(int newDateTab){}

    public void updateSpotData(){}
}

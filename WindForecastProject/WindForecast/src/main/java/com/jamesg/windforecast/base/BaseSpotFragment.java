package com.jamesg.windforecast.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.jamesg.windforecast.R;
import com.jamesg.windforecast.WindFinderApplication;
import com.jamesg.windforecast.cards.HeaderCard;
import com.jamesg.windforecast.cards.WindCard;
import com.jamesg.windforecast.data.Spot;
import com.jamesg.windforecast.manager.SpotManager;
import com.jamesg.windforecast.utils.Logger;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class BaseSpotFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Inject
    Bus bus;

    @Inject
    SpotManager spotManager;

    private SwipeRefreshLayout swipeLayout;

    public BaseSpotFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((WindFinderApplication) getActivity().getApplication()).inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spot, container, false);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.refresh_1,
                R.color.refresh_2,
                R.color.refresh_3,
                R.color.refresh_4);

        onCreateSpotsView(inflater, container, savedInstanceState, view);
        return view;
    }

    public void onCreateSpotsView(LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState, View view) {
    }

    public void updateDateTab(int newDateTab){}

    public void updateSpotData(){}

    @Override
    public void onRefresh() {
        spotManager.checkForUpdates(true);
    }

    public void updateFinished(){
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }
}

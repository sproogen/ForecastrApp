package com.jamesg.forecastr.base;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jamesg.forecastr.R;
import com.jamesg.forecastr.ForecastrApplication;
import com.jamesg.forecastr.manager.SpotManager;
import com.jamesg.forecastr.utils.Logger;
import com.squareup.otto.Bus;

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

    private int viewID = R.layout.fragment_spot;

    public BaseSpotFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((ForecastrApplication) getActivity().getApplication()).inject(this);
        super.onCreate(savedInstanceState);
    }

    public int getViewID(){
        return viewID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(getViewID(), container, false);
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

    public void updateStarted(){
        swipeLayout.setRefreshing(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(spotManager.isUpdating()) {
                    Logger.d("Base Fragment isUpdating");
                    updateStarted();
                }
            }
        }, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }
}

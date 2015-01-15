package com.jamesg.forecastr.SpotFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.jamesg.forecastr.ForecastrApplication;
import com.jamesg.forecastr.R;
import com.jamesg.forecastr.base.BaseSpotFragment;
import com.jamesg.forecastr.cards.HeaderCard;
import com.jamesg.forecastr.cards.LoadingCard;
import com.jamesg.forecastr.cards.MapCard;
import com.jamesg.forecastr.cards.SwellCard;
import com.jamesg.forecastr.cards.WeatherCard;
import com.jamesg.forecastr.cards.WindCard;
import com.jamesg.forecastr.data.Spot;
import com.jamesg.forecastr.data.SpotSearchedEvent;
import com.jamesg.forecastr.data.SpotUpdatedEvent;
import com.jamesg.forecastr.manager.SpotManager;
import com.jamesg.forecastr.utils.Logger;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class SpotFragment extends BaseSpotFragment {

    @Inject
    SpotManager spotManager;

    @Inject
    Tracker tracker;

    private static final String SPOT_NAME = "Spot_Name";
    private static final String SPOT_SEARCHED = "Spot_Searched";

    // TODO: Rename and change types of parameters
    private String spotName;
    private boolean search;

    WindCard windCard;
    HeaderCard headerCard;
    WeatherCard weatherCard;
    SwellCard swellCard;
    MapCard mapCard;
    LoadingCard loadingCard;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param name Spot Name.
     * @return A new instance of fragment SpotFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SpotFragment newInstance(String name, boolean search) {
        SpotFragment fragment = new SpotFragment();
        Bundle args = new Bundle();
        args.putString(SPOT_NAME, name);
        args.putBoolean(SPOT_SEARCHED, search);
        fragment.setArguments(args);
        return fragment;
    }

    public SpotFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((ForecastrApplication) getActivity().getApplication()).inject(this);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            spotName = getArguments().getString(SPOT_NAME);
            search = getArguments().getBoolean(SPOT_SEARCHED);
        }

        int dateTab = 0;
        if (this.mListener != null) {
            dateTab = mListener.getDateTab();
        }
        Spot spot = spotManager.getSpot(spotName);
        if (spot != null) {
            getActivity().setTitle(spot.getName());

            headerCard = new HeaderCard(getActivity(), spot, dateTab);
            if (!search) {
                windCard = new WindCard(getActivity(), spot, dateTab);
                weatherCard = new WeatherCard(getActivity(), spot, dateTab);
                if (spot.hasSwell()) {
                    swellCard = new SwellCard(getActivity(), spot, dateTab);
                }
                mapCard = new MapCard(getActivity(), spot, dateTab, getActivity().getSupportFragmentManager());
            } else {
                loadingCard = new LoadingCard(getActivity());

                spotManager.getDataForSpot(spot);
            }
        }
    }

    @Subscribe
    public void getMessage(String s) {
        Logger.d("BUS MESSAGE baseSpotFragment - " + s);
        if (s.equals("Update Finished")) {
            try {
                updateFinished();
            } catch (Exception e) {
                //DO NOTHING
            }
        }
    }

    @Subscribe
    public void onSpotSearched(SpotSearchedEvent s) {
        Logger.d("BUS SPOT SEARCHED baseSpotFragment - " + s.getName());
        if (s.getName().equals(spotName)) {
            spotUpdated(s.getSpot());
        }
    }

    @Subscribe
    public void onSpotUpdated(SpotUpdatedEvent s) {
        Logger.d("BUS SPOT UPDATED baseSpotFragment - " + s.getName());
        if (s.getName().equals(spotName)) {
            updateSpotData();
        }
    }

    public void spotUpdated(Spot spot) {
        spotManager.searchSpot(spot);
        headerCard.setSearch(true);
        updateCards(spot);
    }


    public void updateCards(Spot spot){
        int dateTab = 0;
        if(this.mListener != null){
            dateTab = mListener.getDateTab();
        }
        windCard = new WindCard(getActivity(), spot, dateTab);
        weatherCard = new WeatherCard(getActivity(), spot, dateTab);
        if (spot.hasSwell()) {
            swellCard = new SwellCard(getActivity(), spot, dateTab);
        }
        mapCard = new MapCard(getActivity(), spot, dateTab, getActivity().getSupportFragmentManager());

        LinearLayout content_body = (LinearLayout) getActivity().findViewById(R.id.content_body);
        content_body.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        if(headerCard != null) content_body.addView(headerCard.getView(inflater));
        if(windCard != null) content_body.addView(windCard.getView(inflater));
        if(weatherCard != null) content_body.addView(weatherCard.getView(inflater));
        if(swellCard != null) content_body.addView(swellCard.getView(inflater));
        if(mapCard != null) content_body.addView(mapCard.getView(inflater));
    }


    @Override
    public void onCreateSpotsView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState, View view) {
        LinearLayout content_body = (LinearLayout) view.findViewById(R.id.content_body);

        if(headerCard != null) content_body.addView(headerCard.getView(inflater));
        if(loadingCard != null) content_body.addView(loadingCard.getView(inflater));
        if(windCard != null) content_body.addView(windCard.getView(inflater));
        if(weatherCard != null) content_body.addView(weatherCard.getView(inflater));
        if(swellCard != null) content_body.addView(swellCard.getView(inflater));
        if(mapCard != null) content_body.addView(mapCard.getView(inflater));
    }

    @Override
    public void updateDateTab(int newDateTab){
        if(headerCard != null) headerCard.updateView(newDateTab);
        if(windCard != null) windCard.updateView(newDateTab);
        if(weatherCard != null) weatherCard.updateView(newDateTab);
        if(swellCard != null) swellCard.updateView(newDateTab);
        if(mapCard != null) mapCard.updateView(newDateTab);
    }

    @Override
    public void updateSpotData(){
        if(headerCard != null) headerCard.updateView();
        if(windCard != null) windCard.updateView();
        if(weatherCard != null) weatherCard.updateView();
        if(swellCard != null) swellCard.updateView();
        if(mapCard != null) mapCard.updateView();
    }
}

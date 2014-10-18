package com.jamesg.windforecast.SpotFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jamesg.windforecast.WindFinderApplication;
import com.jamesg.windforecast.R;
import com.jamesg.windforecast.base.BaseSpotFragment;
import com.jamesg.windforecast.cards.HeaderCard;
import com.jamesg.windforecast.cards.MapCard;
import com.jamesg.windforecast.cards.SwellCard;
import com.jamesg.windforecast.cards.WeatherCard;
import com.jamesg.windforecast.cards.WindCard;
import com.jamesg.windforecast.data.Spot;
import com.jamesg.windforecast.manager.SpotManager;

import javax.inject.Inject;

public class SpotFragment extends BaseSpotFragment {

    @Inject
    SpotManager spotManager;

    private static final String SPOT_NAME = "Spot_Name";

    // TODO: Rename and change types of parameters
    private String spotName;

    WindCard windCard;
    HeaderCard headerCard;
    WeatherCard weatherCard;
    SwellCard swellCard;
    MapCard mapCard;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param name Spot Name.
     * @return A new instance of fragment SpotFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SpotFragment newInstance(String name) {
        SpotFragment fragment = new SpotFragment();
        Bundle args = new Bundle();
        args.putString(SPOT_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }
    public SpotFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((WindFinderApplication) getActivity().getApplication()).inject(this);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            spotName = getArguments().getString(SPOT_NAME);
        }
        int dateTab = 0;
        if(this.mListener != null){
            dateTab = mListener.getDateTab();
        }
        Spot spot = spotManager.getSpot(spotName);
        if(spot != null) {
            getActivity().setTitle(spot.getName());

            headerCard = new HeaderCard(getActivity(), spot, dateTab, false);
            windCard = new WindCard(getActivity(), spot, dateTab);
            weatherCard = new WeatherCard(getActivity(), spot, dateTab);
            if(spot.hasSwell()){
                swellCard = new SwellCard(getActivity(), spot, dateTab);
            }
            mapCard = new MapCard(getActivity(), spot, dateTab, getActivity().getSupportFragmentManager());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spot, container, false);
        LinearLayout content_body = (LinearLayout) view.findViewById(R.id.content_body);

        if(headerCard != null) content_body.addView(headerCard.getView(inflater));
        if(windCard != null) content_body.addView(windCard.getView(inflater));
        if(weatherCard != null) content_body.addView(weatherCard.getView(inflater));
        if(swellCard != null) content_body.addView(swellCard.getView(inflater));
        if(mapCard != null) content_body.addView(mapCard.getView(inflater));

        return view;
    }

    @Override
    public void updateDateTab(int newDateTab){
        if(headerCard != null) headerCard.updateView(newDateTab);
        if(windCard != null) windCard.updateView(newDateTab);
        if(weatherCard != null) weatherCard.updateView(newDateTab);
        if(swellCard != null) swellCard.updateView(newDateTab);
        if(mapCard != null) mapCard.updateView(newDateTab);
    }
}

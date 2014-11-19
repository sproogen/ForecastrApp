package com.jamesg.windforecast.SpotFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jamesg.windforecast.R;
import com.jamesg.windforecast.WindFinderApplication;
import com.jamesg.windforecast.base.BaseSpotFragment;
import com.jamesg.windforecast.base.CardBase;
import com.jamesg.windforecast.cards.HeaderCard;
import com.jamesg.windforecast.cards.WindCard;
import com.jamesg.windforecast.data.Spot;
import com.jamesg.windforecast.manager.SpotManager;
import com.jamesg.windforecast.utils.Logger;

import java.util.ArrayList;

import javax.inject.Inject;

public class FavouritesFragment extends BaseSpotFragment {

    @Inject
    SpotManager spotManager;

    private static final String SPOT_NAME = "Spot_Name";

    // TODO: Rename and change types of parameters
    private String spotName;

    ArrayList<CardBase> cards;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SpotFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavouritesFragment newInstance() {
        FavouritesFragment fragment = new FavouritesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public FavouritesFragment() {
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
        cards = new ArrayList<CardBase>();

        getActivity().setTitle("Favourite Spots");
        cards.add(new HeaderCard(getActivity(), "Favourite Spots", dateTab));

        for (Spot s : spotManager.getAllSpots(0)) {
            if(s != null) {
                cards.add( new WindCard(getActivity(), s, dateTab, true));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spot, container, false);
        LinearLayout content_body = (LinearLayout) view.findViewById(R.id.content_body);

        for (final CardBase card : cards) {
            if (card != null) {
                View cardView = card.getView(inflater);
                if(!card.isHeader()) {
                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mListener.loadSpot(card.getTitle(), 1);
                        }
                    });
                }
                content_body.addView(cardView);
            }
        }

        return view;
    }

    @Override
    public void updateDateTab(int newDateTab){
        for (CardBase card : cards) {
            if (card != null) card.updateView(newDateTab);
        }
    }

    @Override
    public void updateSpotData(){
        for (CardBase card : cards) {
            if (card != null) card.updateView();
        }
    }

    public void removeSpot(Spot spot){
        LinearLayout content_body = (LinearLayout) getActivity().findViewById(R.id.content_body);

        int i = 0;
        for (final CardBase card : cards) {
            if (card != null) {
                if(card.getTitle().equals(spot.getName())){
                    content_body.removeViewAt(i);
                    break;
                }
                i++;
            }
        }
    }
}

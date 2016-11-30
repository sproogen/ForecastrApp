package com.jamesg.forecastr.SpotFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.jamesg.forecastr.ForecastrApplication;
import com.jamesg.forecastr.R;
import com.jamesg.forecastr.base.BaseSpotFragment;
import com.jamesg.forecastr.base.CardBase;
import com.jamesg.forecastr.cards.InfoCard;
import com.jamesg.forecastr.cards.WindCard;
import com.jamesg.forecastr.data.Spot;
import com.jamesg.forecastr.data.SpotUpdatedEvent;
import com.jamesg.forecastr.manager.SpotManager;
import com.jamesg.forecastr.utils.Logger;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;

public class SearchFragment extends BaseSpotFragment {

    @Inject
    SpotManager spotManager;

    @Inject
    Tracker tracker;

    ArrayList<CardBase> cards;
    private FavouritesAdapter favouritesAdapter;
    private ListView content_body;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SpotFragment.
     */
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public int getViewID() {
        return R.layout.fragment_search;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((ForecastrApplication) getActivity().getApplication()).inject(this);
        super.onCreate(savedInstanceState);

        // Set screen name.
        tracker.setScreenName("Search");
        // Send a screen view.
        tracker.send(new HitBuilders.AppViewBuilder().build());

        int dateTab = 0;
        if (this.mListener != null) {
            dateTab = mListener.getDateTab();
        }
        cards = new ArrayList<CardBase>();

        getActivity().setTitle("Search");

//        if (spotManager.getSpotsCount() > 0) {
//            for (Spot s : spotManager.getAllSpots(0)) {
//                if (s != null) {
//                    cards.add(new WindCard(getActivity(), s, dateTab, true));
//                }
//            }
//        } else {
//            cards.add(new InfoCard(getActivity(), dateTab));
//        }
    }

    @Subscribe
    public void getMessage(String s) {
        Logger.d("BUS MESSAGE baseSpotFragment - " + s);
        if (s.equals("Update Finished")) {
            try {
                updateFinished();
            } catch(Exception e) {
                //DO NOTHING
            }
        } else if(s.equals("Update Started")) {
            try {
                updateStarted();
            } catch(Exception e) {
                //DO NOTHING
            }
        }
    }

    @Override
    public void onCreateSpotsView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState, View view) {
        // Inflate the layout for this fragment
        content_body = (ListView) view.findViewById(R.id.content_body);

        favouritesAdapter = new FavouritesAdapter(getActivity(), cards);
        content_body.setAdapter(favouritesAdapter);
        content_body.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                CardBase card = cards.get(position);
                if (!card.isHeader()) {
                    mListener.loadSpot(card.getTitle(), 1);
                }
            }
        });
    }

    public class FavouritesAdapter extends ArrayAdapter<CardBase> {
        public FavouritesAdapter(Context context, ArrayList<CardBase> cards) {
            super(context, 0, cards);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            CardBase card = getItem(position);

            View view = card.getView(LayoutInflater.from(getContext()), false);

            if(view != null) {
                return view;
            }

            return new View(getContext());
        }
    }
}

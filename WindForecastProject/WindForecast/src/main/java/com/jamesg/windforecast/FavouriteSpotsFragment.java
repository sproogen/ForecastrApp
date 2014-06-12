package com.jamesg.windforecast;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jamesg.windforecast.data.Spot;

import java.util.ArrayList;

public class FavouriteSpotsFragment extends Fragment {

    private CustomCardAdapter cardsAdapter;
    private ListView cardsList;
    private int overviewDisplay = 0;
    private String openSpot = "";
    private int searchSpot = 0;

    public FavouriteSpotsFragment() {
    }

    public FavouriteSpotsFragment(int overviewDisplay) {
        this.overviewDisplay = overviewDisplay;
    }

    public FavouriteSpotsFragment(int overviewDisplay, String name) {
        this.overviewDisplay = overviewDisplay;
        this.openSpot = name;
    }

    public FavouriteSpotsFragment(int overviewDisplay, String name, int search) {
        this.overviewDisplay = overviewDisplay;
        this.openSpot = name;
        this.searchSpot = search;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cards_list_view, container, false);

        if (savedInstanceState != null) {
            this.overviewDisplay = savedInstanceState.getInt("overviewDisplay");
        }

        // Initializes a CardAdapter with a blue accent color and basic popup menu for each card
        cardsAdapter = new CustomCardAdapter(getActivity(),overviewDisplay, openSpot, getActivity().getSupportFragmentManager());
        cardsAdapter.setAccentColorRes(R.color.green);
        //cardsAdapter.setPopupMenu(R.menu.card_popup, this); // the popup menu callback is this activity

        cardsList = (ListView) rootView.findViewById(R.id.listview);
        cardsList.setDivider(null);
        cardsList.setAdapter(cardsAdapter);
        cardsList.setSelector(android.R.color.transparent);
        if(openSpot.equals("")){
            cardsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> av, View view, int i, long l) {
                    CustomCard card = cardsAdapter.getItem(i);
                    if (!card.getType().equals("header")) {
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).loadSpot(card.getTitle(), 0);
                        }
                    }
                }
            });
        }else{
            cardsList.setClickable(false);
        }

        if(openSpot.equals("")) {
            getActivity().setTitle("Favourite Spots");
            cardsAdapter.add(new CustomCard("Favourite Spots", "header"));
            ArrayList<CustomCard> appSpots = new ArrayList<CustomCard>();
            for (Spot s : MainActivity.data.getAllSpots(1)) {
                CustomCard c = new CustomCard(s.getName(), "wind");
                c.setSpot(s);
                appSpots.add(c);
            }
            cardsAdapter.addAll(appSpots);
        }else if(searchSpot == 1){
            Spot s = MainActivity.data.getSpot(openSpot);
            getActivity().setTitle(s.getName());
            cardsAdapter.add(new CustomCard(s.getName(),"headerAdd"));
        }else{
            Spot s = MainActivity.data.getSpot(openSpot);
            getActivity().setTitle(s.getName());
            cardsAdapter.add(new CustomCard(s.getName(),"header"));
            cardsAdapter.add(new CustomCard("Wind","wind"));
            if(s.hasSwell()){
                cardsAdapter.add(new CustomCard("Swell","swell"));
            }
            cardsAdapter.add(new CustomCard("Weather","weather"));
            cardsAdapter.add(new CustomCard("Map","map"));
        }

        return rootView;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("WINDFINDER APP", "Remove Map");
        cardsAdapter.removeMap();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void updateOverviewDisplay(int overviewDisplay){
        this.overviewDisplay = overviewDisplay;
        cardsAdapter.updateOverviewDisplay(overviewDisplay);
    }

    public void updateSpot(String name){
        MainActivity.data.parseSpotData(name);
        for(int i=0;i<cardsAdapter.getCount();i++){
            if(((CustomCard)cardsAdapter.getItem(i)).getTitle().equals(name)){
                CustomCard customCard = (CustomCard)cardsAdapter.getItem(i);
                //cardsAdapter.update(customCard);
                return;
            }
        }
    }

    public void removeSpot(String name){
        Spot s = MainActivity.data.getSpot(name);
        for(int i=0;i<cardsAdapter.getCount();i++){
            if(((CustomCard)cardsAdapter.getItem(i)).getTitle().equals(name)){
                CustomCard customCard = (CustomCard)cardsAdapter.getItem(i);
                cardsAdapter.remove(customCard);
                return;
            }
        }
    }

    public void addSpot(String name){
        Spot s = MainActivity.data.getSpot(name);
        cardsAdapter.add((CustomCard) new CustomCard(s.getName(), "wind"));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("overviewDisplay", overviewDisplay);
    }
}
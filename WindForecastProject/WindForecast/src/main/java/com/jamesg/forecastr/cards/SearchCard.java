package com.jamesg.forecastr.cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.jamesg.forecastr.R;
import com.jamesg.forecastr.base.CardBase;

/**
 * Created by James on 16/10/2016.
 */
public class SearchCard extends CardBase {

    public SearchCard(Context context, int dateTab){
        this.context = context;
        this.dateTab = dateTab;
    }

    @Override
    public String getTitle() {
        return "Search";
    }

    @Override
    public boolean isHeader() {
        return true;
    }

    public View getView(LayoutInflater inflater, boolean animate) {

        View view = inflater.inflate(R.layout.card_layout_search, null);
        view.setTag(getTag());

        return view;
    }
}

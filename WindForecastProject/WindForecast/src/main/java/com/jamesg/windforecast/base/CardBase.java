package com.jamesg.windforecast.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.jamesg.windforecast.data.Spot;

/**
 * Created by James on 17/12/13.
 */
public class CardBase {

    public Context context;

    public Spot spot;
    public int dateTab;

    public String titleText = "";
    public String subtitleText = "";


    public String getTitle() {
        return titleText;
    }

    public String getSubtitle() {
        return subtitleText;
    }

    public boolean isHeader() {
        return false;
    }

    public boolean isClickable() {
        return false;
    }

    public View getView(LayoutInflater inflater){ return null; }

    public void updateView(int newDateTab){
        this.dateTab = newDateTab;
    }

    public void updateView(){
        updateView(dateTab);
    }

    public String getTag() {
        return getTitle();
    }
}

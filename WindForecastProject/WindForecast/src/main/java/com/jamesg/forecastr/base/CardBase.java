package com.jamesg.forecastr.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.jamesg.forecastr.data.Spot;

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

    public View getView(LayoutInflater inflater, boolean animate){ return null; }

    public void setDateTab(int newDateTab){
        this.dateTab = newDateTab;
    }

    public void updateView(int newDateTab){
        this.dateTab = newDateTab;
    }

    public void updateView(){
        try {
            updateView(dateTab);
        }catch(Exception e){
            //Error updating card, do nothing
        }
    }

    public String getTag() {
        return getTitle();
    }
}

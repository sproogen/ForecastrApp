package com.jamesg.windforecast.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.jamesg.windforecast.data.Spot;

/**
 * Created by James on 17/12/13.
 */
public class CustomCardBase {

    public Context context;

    public Spot spot;
    public int dateTab;

    private String type = "";
    private String title = "";
    private String subtitle = "";

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getTag() {
        return getTitle();
    }
}

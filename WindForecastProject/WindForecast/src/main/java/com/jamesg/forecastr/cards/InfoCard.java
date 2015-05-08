package com.jamesg.forecastr.cards;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.jamesg.forecastr.R;
import com.jamesg.forecastr.base.CardBase;
import com.jamesg.forecastr.data.Spot;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by James on 17/10/2014.
 */
public class InfoCard extends CardBase {

    public InfoCard(Context context, int dateTab){
        this.context = context;
        this.dateTab = dateTab;
    }

    @Override
    public String getTitle() {
        return "Info";
    }

    @Override
    public boolean isHeader() {
        return true;
    }

    public View getView(LayoutInflater inflater, boolean animate) {

        View view = inflater.inflate(R.layout.card_layout_info, null);
        view.setTag(getTag());

        return view;
    }
}

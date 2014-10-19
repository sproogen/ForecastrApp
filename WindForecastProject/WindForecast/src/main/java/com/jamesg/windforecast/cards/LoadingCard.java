package com.jamesg.windforecast.cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.jamesg.windforecast.MainActivity;
import com.jamesg.windforecast.R;
import com.jamesg.windforecast.base.CardBase;
import com.jamesg.windforecast.data.Spot;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by James on 17/10/2014.
 */
public class LoadingCard extends CardBase {

    public LoadingCard(Context context){
        this.context = context;
    }

    @Override
    public String getTitle() {
        if(spot != null){
            return spot.getName();
        } else {
            return super.getTitle();
        }
    }

    @Override
    public String getTag() {
        return "loading";
    }

    public View getView(LayoutInflater inflater){

        View view = inflater.inflate(R.layout.card_layout_loading, null);
        view.setTag(getTag());

        return view;
    }

    public void updateView(int newDateTab){
        super.updateView(newDateTab);
    }
}

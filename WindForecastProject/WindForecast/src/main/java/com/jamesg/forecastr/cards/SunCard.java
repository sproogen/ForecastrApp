package com.jamesg.forecastr.cards;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.jamesg.forecastr.R;
import com.jamesg.forecastr.base.CardBase;
import com.jamesg.forecastr.data.Spot;
import com.jamesg.forecastr.data.TimestampData;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by James on 17/10/2014.
 */
public class SunCard extends CardBase {

    public SunCard(Context context, Spot spot, int dateTab){
        this.context = context;
        this.spot = spot;
        this.dateTab = dateTab;
    }

    @Override
    public String getTitle() {
        return "Sunrise & Sunset";
    }

    public View getView(LayoutInflater inflater){

        View view = inflater.inflate(R.layout.card_layout_sun, null);
        view.setTag(getTag());

        boolean animate = true;

        Calendar c = null;
        SimpleDateFormat df = new SimpleDateFormat("EEE");
        if(dateTab == 2) {
            view.setVisibility(View.GONE);
            return view;
        }

        TextSwitcher sunRiseText = (TextSwitcher) view.findViewById(R.id.sunRiseText);
        TextView sunRisetv = (TextView) sunRiseText.getCurrentView();
        if(sunRisetv == null){
            sunRiseText.setFactory(new ViewSwitcher.ViewFactory() {

                public View makeView() {
                    TextView myText = new TextView(context);
                    myText.setGravity(Gravity.CENTER);

                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                            Gravity.CENTER);
                    myText.setLayoutParams(params);

                    myText.setTextSize(18);
                    myText.setTextColor(Color.BLACK);
                    return myText;
                }
            });
        }
        sunRiseText.setInAnimation(AnimationUtils.loadAnimation(context,
                R.anim.fade_in));
        sunRiseText.setOutAnimation(context, R.anim.fade_out);

        TextSwitcher sunSetText = (TextSwitcher) view.findViewById(R.id.sunSetText);
        TextView sunSettv = (TextView) sunSetText.getCurrentView();
        if(sunSettv == null){
            sunSetText.setFactory(new ViewSwitcher.ViewFactory() {

                public View makeView() {
                    TextView myText = new TextView(context);
                    myText.setGravity(Gravity.CENTER);

                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                            Gravity.CENTER);
                    myText.setLayoutParams(params);

                    myText.setTextSize(18);
                    myText.setTextColor(Color.BLACK);
                    return myText;
                }
            });
        }

        sunRiseText.setInAnimation(AnimationUtils.loadAnimation(context,
                R.anim.fade_in));
        sunRiseText.setOutAnimation(context, R.anim.fade_out);
        sunSetText.setInAnimation(AnimationUtils.loadAnimation(context,
                R.anim.fade_in));
        sunSetText.setOutAnimation(context, R.anim.fade_out);

        sunRiseText.setText(spot.getSunRise(dateTab));
        sunSetText.setText(spot.getSunSet(dateTab));

        return view;
    }

    public void updateView(int newDateTab){
        super.updateView(newDateTab);

        View view = ((Activity)context).findViewById(R.id.content_body).findViewWithTag(getTitle());

        boolean animate = true;

        Calendar c = null;
        SimpleDateFormat df = new SimpleDateFormat("EEE");
        if(dateTab == 2) {
            view.setVisibility(View.GONE);
            return;
        }else{
            view.setVisibility(View.VISIBLE);
        }

        TextSwitcher sunRiseText = (TextSwitcher) view.findViewById(R.id.sunRiseText);
        TextView sunRisetv = (TextView) sunRiseText.getCurrentView();
        if(sunRisetv == null){
            sunRiseText.setFactory(new ViewSwitcher.ViewFactory() {

                public View makeView() {
                    TextView myText = new TextView(context);
                    myText.setGravity(Gravity.CENTER);

                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                            Gravity.CENTER);
                    myText.setLayoutParams(params);

                    myText.setTextSize(18);
                    myText.setTextColor(Color.BLACK);
                    return myText;
                }
            });
        }
        sunRiseText.setInAnimation(AnimationUtils.loadAnimation(context,
                R.anim.fade_in));
        sunRiseText.setOutAnimation(context, R.anim.fade_out);

        TextSwitcher sunSetText = (TextSwitcher) view.findViewById(R.id.sunSetText);
        TextView sunSettv = (TextView) sunSetText.getCurrentView();
        if(sunSettv == null){
            sunSetText.setFactory(new ViewSwitcher.ViewFactory() {

                public View makeView() {
                    TextView myText = new TextView(context);
                    myText.setGravity(Gravity.CENTER);

                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                            Gravity.CENTER);
                    myText.setLayoutParams(params);

                    myText.setTextSize(18);
                    myText.setTextColor(Color.BLACK);
                    return myText;
                }
            });
        }

        sunRiseText.setInAnimation(AnimationUtils.loadAnimation(context,
                R.anim.fade_in));
        sunRiseText.setOutAnimation(context, R.anim.fade_out);
        sunSetText.setInAnimation(AnimationUtils.loadAnimation(context,
                R.anim.fade_in));
        sunSetText.setOutAnimation(context, R.anim.fade_out);

        sunRiseText.setText(spot.getSunRise(dateTab));
        sunSetText.setText(spot.getSunSet(dateTab));
    }

}

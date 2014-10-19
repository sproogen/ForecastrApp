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
public class HeaderCard extends CardBase {

    TextView title;
    TextView subtitle;
    TextView button;

    Boolean search = false;

    public HeaderCard(Context context, Spot spot, int dateTab, boolean search){
        this.context = context;
        this.spot = spot;
        this.dateTab = dateTab;
        this.search = search;
    }

    public HeaderCard(Context context, String titleText, int dateTab){
        this.context = context;
        this.dateTab = dateTab;
        this.titleText = titleText;
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
        return "header";
    }

    @Override
    public boolean isHeader() {
        return true;
    }

    public View getView(LayoutInflater inflater){

        View view = inflater.inflate(R.layout.card_layout_header, null);
        view.setTag(getTag());

        title = (TextView) view.findViewById(android.R.id.title);
        subtitle = (TextView) view.findViewById(android.R.id.content);
        button = (TextView) view.findViewById(android.R.id.button1);

        title.setText(getTitle());

        if (search) { // && !MainActivity.data.isFavourite(titleTxt)
            button.setVisibility(View.VISIBLE);
            button.setBackgroundColor(context.getResources().getColor(R.color.green));
            String buttonTxt = "Add to Favourites";
            button.setText(buttonTxt);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(context instanceof MainActivity) {
                        ((MainActivity) context).addSpot(getTitle());
                        button.setVisibility(View.GONE);
                    }
                }
            });
        } else button.setVisibility(View.INVISIBLE);

        if(dateTab == 0){
            if(subtitle.getVisibility() == View.INVISIBLE){
                AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f );
                fadeIn.setDuration(400);
                subtitle.startAnimation(fadeIn);
                subtitle.setVisibility(View.VISIBLE);
            }
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MMM");
            subtitle.setText(df.format(c.getTime()));
        }else if(dateTab == 1){
            if(subtitle.getVisibility() == View.INVISIBLE){
                AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f );
                fadeIn.setDuration(400);
                subtitle.startAnimation(fadeIn);
                subtitle.setVisibility(View.VISIBLE);
            }
            Calendar c = Calendar.getInstance();
            c.add(c.DAY_OF_MONTH, 1);
            SimpleDateFormat df = new SimpleDateFormat("dd/MMM");
            subtitle.setText(df.format(c.getTime()));
        }else{
            if(subtitle.getVisibility() == View.VISIBLE){
                AlphaAnimation fadeOut = new AlphaAnimation(1.0f , 0.0f );
                fadeOut.setDuration(400);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        subtitle.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                subtitle.startAnimation(fadeOut);

            }
        }
        return view;
    }

    public void updateView(int newDateTab){
        super.updateView(newDateTab);

        if(dateTab == 0){
            if(subtitle.getVisibility() == View.INVISIBLE){
                AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f );
                fadeIn.setDuration(400);
                subtitle.startAnimation(fadeIn);
                subtitle.setVisibility(View.VISIBLE);
            }
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MMM");
            subtitle.setText(df.format(c.getTime()));
        }else if(dateTab == 1){
            if(subtitle.getVisibility() == View.INVISIBLE){
                AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f );
                fadeIn.setDuration(400);
                subtitle.startAnimation(fadeIn);
                subtitle.setVisibility(View.VISIBLE);
            }
            Calendar c = Calendar.getInstance();
            c.add(c.DAY_OF_MONTH, 1);
            SimpleDateFormat df = new SimpleDateFormat("dd/MMM");
            subtitle.setText(df.format(c.getTime()));
        }else{
            if(subtitle.getVisibility() == View.VISIBLE){
                AlphaAnimation fadeOut = new AlphaAnimation(1.0f , 0.0f );
                fadeOut.setDuration(400);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        subtitle.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                subtitle.startAnimation(fadeOut);

            }
        }
    }
}

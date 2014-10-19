package com.jamesg.windforecast.cards;

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

import com.jamesg.windforecast.R;
import com.jamesg.windforecast.base.CardBase;
import com.jamesg.windforecast.data.Spot;
import com.jamesg.windforecast.data.TimestampData;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by James on 17/10/2014.
 */
public class WeatherCard extends CardBase {

    private TextView titleTextView;

    public WeatherCard(Context context, Spot spot, int dateTab){
        this.context = context;
        this.spot = spot;
        this.dateTab = dateTab;
    }

    @Override
    public String getTitle() {
        return "Weather";
    }

    public View getView(LayoutInflater inflater){

        View view = inflater.inflate(R.layout.card_layout_weather, null);
        view.setTag(getTag());

        boolean animate = true;

        titleTextView = (TextView) view.findViewById(android.R.id.title);
        titleTextView.setText(getTitle());

        Calendar c = null;
        SimpleDateFormat df = new SimpleDateFormat("EEE");
        if(dateTab == 2) c = Calendar.getInstance();

        int timeSteps = 7;
        if(dateTab == 2){
            timeSteps = spot.getSevenDayDayCount();
        }
        for(int i=0; i < 7; i++){

            boolean weatherChange = true;
            boolean tempChange = true;

            int timeStamp = (i+1)*3;

            TimestampData intervalData = null;
            if(dateTab == 0){
                intervalData = spot.getTodayTimestamp(timeStamp);
            }else if(dateTab == 1){
                intervalData = spot.getTomorrowTimestamp(timeStamp);
            }else {
                if(i < timeSteps)intervalData = spot.getSevenDayDay(i);
                else intervalData = null;
            }

            //Log.d("WINDFINDER APP", "Column" + i);
            int colID = context.getResources().getIdentifier("column"+(i+1), "id", context.getPackageName());
            View column = view.findViewById(colID);

            TextView time = (TextView) column.findViewById(R.id.valueText);

            if(dateTab == 0 || dateTab == 1){
                int timeID = context.getResources().getIdentifier("time"+(i+1), "string", context.getPackageName());
                time.setText(timeID);
            }else{
                time.setText(df.format(c.getTime()));
                c.add(c.DAY_OF_MONTH, 1);
            }

            int weatherVal = -1; String weatherString = "";
            int tempVal = -1; String tempString = "";

            if(intervalData != null){
                weatherVal = intervalData.weather;
                weatherString = Integer.toString(weatherVal);
                tempVal = intervalData.temp;
                tempString = Integer.toString(tempVal);
            }

            TextView weatherType = (TextView) column.findViewById(R.id.weatherType);
            TextSwitcher temp = (TextSwitcher) column.findViewById(R.id.temp);
            TextView tv = (TextView) temp.getCurrentView();
            if(tv == null){
                temp.setFactory(new ViewSwitcher.ViewFactory() {

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

            if(animate){
                temp.setInAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.fade_in));
                temp.setOutAnimation(context, R.anim.fade_out);
            }else{
                temp.setInAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.no_anim_in));
                temp.setOutAnimation(context, R.anim.no_anim_out);
            }

            String oldWeatherString = weatherType.getText().toString();
            if(oldWeatherString == "") oldWeatherString = "0";

            final int oldWeatherVal = Integer.parseInt(oldWeatherString);

            weatherType.setText(weatherString);
            if(!tempString.equals("")) tempString = tempString + "\u00B0";
            temp.setText(tempString);

            final ImageView weatherImage = (ImageView)column.findViewById(R.id.weatherImage);

            int weatherToDrawable = R.drawable.wind_icon_null;

            if(weatherVal == 0){ weatherToDrawable = R.drawable.weather_0; if(oldWeatherVal == 0) weatherChange = false;}
            else if(weatherVal == 1){weatherToDrawable =  R.drawable.weather_1;if(oldWeatherVal == 1) weatherChange = false;}
            else if(weatherVal == 2){weatherToDrawable =  R.drawable.weather_2;if(oldWeatherVal == 2) weatherChange = false;}
            else if(weatherVal == 3){weatherToDrawable =  R.drawable.weather_3;if(oldWeatherVal == 3) weatherChange = false;}
            else if(weatherVal == 5){weatherToDrawable =  R.drawable.weather_5;if(oldWeatherVal == 5) weatherChange = false;}
            else if(weatherVal == 6){weatherToDrawable =  R.drawable.weather_6;if(oldWeatherVal == 6) weatherChange = false;}
            else if(weatherVal == 7){weatherToDrawable =  R.drawable.weather_7;if(oldWeatherVal == 7) weatherChange = false;}
            else if(weatherVal == 8){weatherToDrawable =  R.drawable.weather_8;if(oldWeatherVal == 8) weatherChange = false;}
            else if(weatherVal == 9){weatherToDrawable =  R.drawable.weather_9;if(oldWeatherVal == 9) weatherChange = false;}
            else if(weatherVal == 10){weatherToDrawable =  R.drawable.weather_10;if(oldWeatherVal == 10) weatherChange = false;}
            else if(weatherVal == 11){weatherToDrawable =  R.drawable.weather_11;if(oldWeatherVal == 11) weatherChange = false;}
            else if(weatherVal == 12){weatherToDrawable =  R.drawable.weather_12;if(oldWeatherVal == 12) weatherChange = false;}
            else if(weatherVal == 13){weatherToDrawable =  R.drawable.weather_13;if(oldWeatherVal == 13) weatherChange = false;}
            else if(weatherVal == 14){weatherToDrawable =  R.drawable.weather_14;if(oldWeatherVal == 14) weatherChange = false;}
            else if(weatherVal == 15){weatherToDrawable =  R.drawable.weather_15;if(oldWeatherVal == 15) weatherChange = false;}
            else if(weatherVal == 16){weatherToDrawable =  R.drawable.weather_16;if(oldWeatherVal == 16) weatherChange = false;}
            else if(weatherVal == 17){weatherToDrawable =  R.drawable.weather_17;if(oldWeatherVal == 17) weatherChange = false;}
            else if(weatherVal == 18){weatherToDrawable =  R.drawable.weather_18;if(oldWeatherVal == 18) weatherChange = false;}
            else if(weatherVal == 19){weatherToDrawable =  R.drawable.weather_19;if(oldWeatherVal == 19) weatherChange = false;}
            else if(weatherVal == 20){weatherToDrawable =  R.drawable.weather_20;if(oldWeatherVal == 20) weatherChange = false;}
            else if(weatherVal == 21){weatherToDrawable =  R.drawable.weather_21;if(oldWeatherVal == 21) weatherChange = false;}
            else if(weatherVal == 22){weatherToDrawable =  R.drawable.weather_22;if(oldWeatherVal == 22) weatherChange = false;}
            else if(weatherVal == 23){weatherToDrawable =  R.drawable.weather_23;if(oldWeatherVal == 23) weatherChange = false;}
            else if(weatherVal == 24){weatherToDrawable =  R.drawable.weather_24;if(oldWeatherVal == 24) weatherChange = false;}
            else if(weatherVal == 25){weatherToDrawable =  R.drawable.weather_25;if(oldWeatherVal == 25) weatherChange = false;}
            else if(weatherVal == 26){weatherToDrawable =  R.drawable.weather_26;if(oldWeatherVal == 26) weatherChange = false;}
            else if(weatherVal == 27){weatherToDrawable =  R.drawable.weather_27;if(oldWeatherVal == 27) weatherChange = false;}
            else if(weatherVal == 28){weatherToDrawable =  R.drawable.weather_28;if(oldWeatherVal == 28) weatherChange = false;}
            else if(weatherVal == 29){weatherToDrawable =  R.drawable.weather_29;if(oldWeatherVal == 29) weatherChange = false;}
            else if(weatherVal == 30){weatherToDrawable =  R.drawable.weather_30;if(oldWeatherVal == 30) weatherChange = false;}

            if(weatherChange){
                final TransitionDrawable windTransitionDrawable =
                        new TransitionDrawable(new Drawable[] {
                                weatherImage.getDrawable(),
                                context.getResources().getDrawable(weatherToDrawable)
                        });
                weatherImage.setImageDrawable(windTransitionDrawable);
                windTransitionDrawable.setCrossFadeEnabled(true);
                if(animate) windTransitionDrawable.startTransition(400);
                else windTransitionDrawable.startTransition(0);
            }else{
                weatherImage.setImageDrawable(context.getResources().getDrawable(weatherToDrawable));
            }
        }

        return view;
    }

    public void updateView(int newDateTab){
        super.updateView(newDateTab);

        View view = ((Activity)context).findViewById(R.id.content_body).findViewWithTag(getTitle());

        boolean animate = true;

        Calendar c = null;
        SimpleDateFormat df = new SimpleDateFormat("EEE");
        if(dateTab == 2) c = Calendar.getInstance();

        int timeSteps = 7;
        if(dateTab == 2){
            timeSteps = spot.getSevenDayDayCount();
        }
        for(int i=0; i < 7; i++){

            boolean weatherChange = true;
            boolean tempChange = true;

            int timeStamp = (i+1)*3;

            TimestampData intervalData = null;
            if(dateTab == 0){
                intervalData = spot.getTodayTimestamp(timeStamp);
            }else if(dateTab == 1){
                intervalData = spot.getTomorrowTimestamp(timeStamp);
            }else {
                if(i < timeSteps)intervalData = spot.getSevenDayDay(i);
                else intervalData = null;
            }

            //Log.d("WINDFINDER APP", "Column" + i);
            int colID = context.getResources().getIdentifier("column"+(i+1), "id", context.getPackageName());
            View column = view.findViewById(colID);

            TextView time = (TextView) column.findViewById(R.id.valueText);

            if(dateTab == 0 || dateTab == 1){
                int timeID = context.getResources().getIdentifier("time"+(i+1), "string", context.getPackageName());
                time.setText(timeID);
            }else{
                time.setText(df.format(c.getTime()));
                c.add(c.DAY_OF_MONTH, 1);
            }

            int weatherVal = -1; String weatherString = "";
            int tempVal = -1; String tempString = "";

            if(intervalData != null){
                weatherVal = intervalData.weather;
                weatherString = Integer.toString(weatherVal);
                tempVal = intervalData.temp;
                tempString = Integer.toString(tempVal);
            }

            TextView weatherType = (TextView) column.findViewById(R.id.weatherType);
            TextSwitcher temp = (TextSwitcher) column.findViewById(R.id.temp);
            TextView tv = (TextView) temp.getCurrentView();
            if(tv == null){
                temp.setFactory(new ViewSwitcher.ViewFactory() {

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

            if(animate){
                temp.setInAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.fade_in));
                temp.setOutAnimation(context, R.anim.fade_out);
            }else{
                temp.setInAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.no_anim_in));
                temp.setOutAnimation(context, R.anim.no_anim_out);
            }

            String oldWeatherString = weatherType.getText().toString();
            if(oldWeatherString == "") oldWeatherString = "0";

            final int oldWeatherVal = Integer.parseInt(oldWeatherString);

            weatherType.setText(weatherString);
            if(!tempString.equals("")) tempString = tempString + "\u00B0";
            temp.setText(tempString);

            final ImageView weatherImage = (ImageView)column.findViewById(R.id.weatherImage);

            int weatherToDrawable = R.drawable.wind_icon_null;

            if(weatherVal == 0){ weatherToDrawable = R.drawable.weather_0; if(oldWeatherVal == 0) weatherChange = false;}
            else if(weatherVal == 1){weatherToDrawable =  R.drawable.weather_1;if(oldWeatherVal == 1) weatherChange = false;}
            else if(weatherVal == 2){weatherToDrawable =  R.drawable.weather_2;if(oldWeatherVal == 2) weatherChange = false;}
            else if(weatherVal == 3){weatherToDrawable =  R.drawable.weather_3;if(oldWeatherVal == 3) weatherChange = false;}
            else if(weatherVal == 5){weatherToDrawable =  R.drawable.weather_5;if(oldWeatherVal == 5) weatherChange = false;}
            else if(weatherVal == 6){weatherToDrawable =  R.drawable.weather_6;if(oldWeatherVal == 6) weatherChange = false;}
            else if(weatherVal == 7){weatherToDrawable =  R.drawable.weather_7;if(oldWeatherVal == 7) weatherChange = false;}
            else if(weatherVal == 8){weatherToDrawable =  R.drawable.weather_8;if(oldWeatherVal == 8) weatherChange = false;}
            else if(weatherVal == 9){weatherToDrawable =  R.drawable.weather_9;if(oldWeatherVal == 9) weatherChange = false;}
            else if(weatherVal == 10){weatherToDrawable =  R.drawable.weather_10;if(oldWeatherVal == 10) weatherChange = false;}
            else if(weatherVal == 11){weatherToDrawable =  R.drawable.weather_11;if(oldWeatherVal == 11) weatherChange = false;}
            else if(weatherVal == 12){weatherToDrawable =  R.drawable.weather_12;if(oldWeatherVal == 12) weatherChange = false;}
            else if(weatherVal == 13){weatherToDrawable =  R.drawable.weather_13;if(oldWeatherVal == 13) weatherChange = false;}
            else if(weatherVal == 14){weatherToDrawable =  R.drawable.weather_14;if(oldWeatherVal == 14) weatherChange = false;}
            else if(weatherVal == 15){weatherToDrawable =  R.drawable.weather_15;if(oldWeatherVal == 15) weatherChange = false;}
            else if(weatherVal == 16){weatherToDrawable =  R.drawable.weather_16;if(oldWeatherVal == 16) weatherChange = false;}
            else if(weatherVal == 17){weatherToDrawable =  R.drawable.weather_17;if(oldWeatherVal == 17) weatherChange = false;}
            else if(weatherVal == 18){weatherToDrawable =  R.drawable.weather_18;if(oldWeatherVal == 18) weatherChange = false;}
            else if(weatherVal == 19){weatherToDrawable =  R.drawable.weather_19;if(oldWeatherVal == 19) weatherChange = false;}
            else if(weatherVal == 20){weatherToDrawable =  R.drawable.weather_20;if(oldWeatherVal == 20) weatherChange = false;}
            else if(weatherVal == 21){weatherToDrawable =  R.drawable.weather_21;if(oldWeatherVal == 21) weatherChange = false;}
            else if(weatherVal == 22){weatherToDrawable =  R.drawable.weather_22;if(oldWeatherVal == 22) weatherChange = false;}
            else if(weatherVal == 23){weatherToDrawable =  R.drawable.weather_23;if(oldWeatherVal == 23) weatherChange = false;}
            else if(weatherVal == 24){weatherToDrawable =  R.drawable.weather_24;if(oldWeatherVal == 24) weatherChange = false;}
            else if(weatherVal == 25){weatherToDrawable =  R.drawable.weather_25;if(oldWeatherVal == 25) weatherChange = false;}
            else if(weatherVal == 26){weatherToDrawable =  R.drawable.weather_26;if(oldWeatherVal == 26) weatherChange = false;}
            else if(weatherVal == 27){weatherToDrawable =  R.drawable.weather_27;if(oldWeatherVal == 27) weatherChange = false;}
            else if(weatherVal == 28){weatherToDrawable =  R.drawable.weather_28;if(oldWeatherVal == 28) weatherChange = false;}
            else if(weatherVal == 29){weatherToDrawable =  R.drawable.weather_29;if(oldWeatherVal == 29) weatherChange = false;}
            else if(weatherVal == 30){weatherToDrawable =  R.drawable.weather_30;if(oldWeatherVal == 30) weatherChange = false;}

            if(weatherChange){
                final TransitionDrawable windTransitionDrawable =
                        new TransitionDrawable(new Drawable[] {
                                weatherImage.getDrawable(),
                                context.getResources().getDrawable(weatherToDrawable)
                        });
                weatherImage.setImageDrawable(windTransitionDrawable);
                windTransitionDrawable.setCrossFadeEnabled(true);
                if(animate) windTransitionDrawable.startTransition(400);
                else windTransitionDrawable.startTransition(0);
            }else{
                weatherImage.setImageDrawable(context.getResources().getDrawable(weatherToDrawable));
            }
        }

    }

}

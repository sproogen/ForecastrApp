package com.jamesg.windforecast.cards;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
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
public class SwellCard extends CardBase {

    private TextView titleTextView;

    public SwellCard(Context context, Spot spot, int dateTab){
        this.context = context;
        this.spot = spot;
        this.dateTab = dateTab;
    }

    @Override
    public String getTitle() {
        return "Swell";
    }

    public View getView(LayoutInflater inflater){

        View view = inflater.inflate(R.layout.card_layout_swell, null);
        view.setTag(getTag());

        boolean animate = true;

        titleTextView = (TextView) view.findViewById(android.R.id.title);
        titleTextView.setText(getTitle());

        //Log.d("WINDFINDER APP",thisSpot.getName() + " Sun Rise - "+ thisSpot.getSunRise());
        //Log.d("WINDFINDER APP",thisSpot.getName() + " Sun Set - "+ thisSpot.getSunSet());

        Calendar c = null;
        SimpleDateFormat df = new SimpleDateFormat("EEE");
        if(dateTab == 2) c = Calendar.getInstance();

        int timeSteps = 7;
        if(dateTab == 2){
            timeSteps = spot.getSevenDayDayCount();
        }
        for(int i=0; i < 7; i++){

            boolean waveColorChange = true;
            boolean periodColorChange = true;

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

            double windVal = -1; String windString = "";
            double gustVal = -1; String gustString = "";
            int windDirection = 0;

            if(intervalData != null){
                windVal = intervalData.swellPeriod;
                windString = Double.toString(windVal);
                gustVal = intervalData.swellHeight;
                gustString = Double.toString(gustVal);

                windDirection = intervalData.swellDirection;
            }

            String oldWindString;
            String oldGustString;

            TextSwitcher wind = (TextSwitcher) column.findViewById(R.id.windSpeedSwitcher);
            TextView windTextView = (TextView) wind.getCurrentView();
            if(windTextView == null){
                wind.setFactory(new ViewSwitcher.ViewFactory() {

                    public View makeView() {
                        TextView myText = new TextView(context);
                        myText.setGravity(Gravity.CENTER);

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                                Gravity.CENTER);
                        myText.setLayoutParams(params);

                        myText.setTextSize(14);
                        myText.setTextColor(Color.BLACK);
                        return myText;
                    }
                });
                oldWindString = "";
            }else{
                oldWindString = windTextView.getText().toString();
            }
            if(animate){
                wind.setInAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.fade_in));
                wind.setOutAnimation(context, R.anim.fade_out);
            }else{
                wind.setInAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.no_anim_in));
                wind.setOutAnimation(context, R.anim.no_anim_out);
            }

            TextSwitcher gust = (TextSwitcher) column.findViewById(R.id.gustSpeedSwitcher);
            TextView gustTextView = (TextView) gust.getCurrentView();
            if(gustTextView == null){
                gust.setFactory(new ViewSwitcher.ViewFactory() {

                    public View makeView() {
                        TextView myText = new TextView(context);
                        myText.setGravity(Gravity.CENTER);

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                                Gravity.CENTER);
                        myText.setLayoutParams(params);

                        myText.setTextSize(14);
                        myText.setTextColor(Color.WHITE);
                        return myText;
                    }
                });
                oldGustString = "";
            }else{
                oldGustString = gustTextView.getText().toString();
            }
            if(animate){
                gust.setInAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.fade_in));
                gust.setOutAnimation(context, R.anim.fade_out);
            }else{
                gust.setInAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.no_anim_in));
                gust.setOutAnimation(context, R.anim.no_anim_out);
            }

            if(oldWindString == "") oldWindString = "0";
            if(oldGustString == "") oldGustString = "0";

            final double oldWindVal = Double.parseDouble(oldWindString);
            final double oldGustVal = Double.parseDouble(oldGustString);

            wind.setText(windString);
            gust.setText(gustString);

            final ImageView gustImage = (ImageView)column.findViewById(R.id.gustImage);
            final ImageView windImage = (ImageView)column.findViewById(R.id.windImage);

            int windToDrawable;
            int gustToDrawable;

            /*if(windVal >= 45){ windToDrawable = R.drawable.swell_icon_12; if(oldWindVal >= 45) waveColorChange = false;}
            else if(windVal >= 40){windToDrawable =  R.drawable.swell_icon_11;if(oldWindVal >= 40 && oldWindVal < 45) waveColorChange = false;}
            else if(windVal >= 35){windToDrawable =  R.drawable.swell_icon_10;if(oldWindVal >= 35 && oldWindVal < 40) waveColorChange = false;}
            else if(windVal >= 30){windToDrawable =  R.drawable.swell_icon_9;if(oldWindVal >= 30 && oldWindVal < 35) waveColorChange = false;}
            else if(windVal >= 25){windToDrawable =  R.drawable.swell_icon_8;if(oldWindVal >= 25 && oldWindVal < 30) waveColorChange = false;}
            else if(windVal >= 22){windToDrawable =  R.drawable.swell_icon_7;if(oldWindVal >= 22 && oldWindVal < 25) waveColorChange = false;}
            else if(windVal >= 18){windToDrawable =  R.drawable.swell_icon_6;if(oldWindVal >= 18 && oldWindVal < 22) waveColorChange = false;}
            else if(windVal >= 14){windToDrawable =  R.drawable.swell_icon_5;if(oldWindVal >= 14 && oldWindVal < 18) waveColorChange = false;}
            else if(windVal >= 10){windToDrawable =  R.drawable.swell_icon_4;if(oldWindVal >= 10 && oldWindVal < 14) waveColorChange = false;}
            else if(windVal >= 6){windToDrawable =  R.drawable.swell_icon_3;if(oldWindVal >= 6 && oldWindVal < 10) waveColorChange = false;}
            else if(windVal >= 3){windToDrawable =  R.drawable.swell_icon_2;if(oldWindVal >= 3 && oldWindVal < 6) waveColorChange = false;}
            else if(windVal >= 1){windToDrawable =  R.drawable.swell_icon_1;if(oldWindVal >= 1 && oldWindVal < 3) waveColorChange = false;}
            else {windToDrawable =  R.drawable.swell_icon_0;if(oldWindVal == 0) waveColorChange = false;}*/

            if(gustVal >= 14.3){ gustToDrawable = R.drawable.swell_icon_12; if(oldGustVal >= 14.3) periodColorChange = false;}
            else if(gustVal >= 13.1){gustToDrawable =  R.drawable.swell_icon_11;if(oldGustVal >= 13.1 && oldGustVal < 14.3) periodColorChange = false;}
            else if(gustVal >= 11.8){gustToDrawable =  R.drawable.swell_icon_10;if(oldGustVal >= 11.8 && oldGustVal < 13.1) periodColorChange = false;}
            else if(gustVal >= 10.6){gustToDrawable =  R.drawable.swell_icon_9;if(oldGustVal >= 10.6 && oldGustVal < 11.8) periodColorChange = false;}
            else if(gustVal >= 9.4){gustToDrawable =  R.drawable.swell_icon_8;if(oldGustVal >= 9.4 && oldGustVal < 10.6) periodColorChange = false;}
            else if(gustVal >= 8.2){gustToDrawable =  R.drawable.swell_icon_7;if(oldGustVal >= 8.2 && oldGustVal < 9.4) periodColorChange = false;}
            else if(gustVal >= 5.7){gustToDrawable =  R.drawable.swell_icon_6;if(oldGustVal >= 5.7 && oldGustVal < 8.2) periodColorChange = false;}
            else if(gustVal >= 4.5){gustToDrawable =  R.drawable.swell_icon_5;if(oldGustVal >= 4.5 && oldGustVal < 5.7) periodColorChange = false;}
            else if(gustVal >= 3.3){gustToDrawable =  R.drawable.swell_icon_4;if(oldGustVal >= 3.3 && oldGustVal < 4.5) periodColorChange = false;}
            else if(gustVal >= 2.1){gustToDrawable =  R.drawable.swell_icon_3;if(oldGustVal >= 2.1 && oldGustVal < 3.3) periodColorChange = false;}
            else if(gustVal >= 0.9){gustToDrawable =  R.drawable.swell_icon_2;if(oldGustVal >= 0.9 && oldGustVal < 2.1) periodColorChange = false;}
            else if(gustVal >= 0){gustToDrawable =  R.drawable.swell_icon_1;if(oldGustVal >= 0 && oldGustVal < 0.9) periodColorChange = false;}
            else {gustToDrawable =  R.drawable.wind_icon_null;if(oldGustVal == 0) periodColorChange = false;}

            windToDrawable =  R.drawable.wind_icon_null;
            waveColorChange = false;

            if(waveColorChange){
                final TransitionDrawable windTransitionDrawable =
                        new TransitionDrawable(new Drawable[] {
                                windImage.getDrawable(),
                                context.getResources().getDrawable(windToDrawable)
                        });
                windImage.setImageDrawable(windTransitionDrawable);
                windTransitionDrawable.setCrossFadeEnabled(true);
                if(animate)windTransitionDrawable.startTransition(400);
                else windTransitionDrawable.startTransition(0);
            }else{
                windImage.setImageDrawable(context.getResources().getDrawable(windToDrawable));
            }
            if(periodColorChange){
                final TransitionDrawable gustTransitionDrawable =
                        new TransitionDrawable(new Drawable[] {
                                gustImage.getDrawable(),
                                context.getResources().getDrawable(gustToDrawable)
                        });
                gustImage.setImageDrawable(gustTransitionDrawable);
                gustTransitionDrawable.setCrossFadeEnabled(true);
                if(animate)gustTransitionDrawable.startTransition(400);
                else gustTransitionDrawable.startTransition(0);
            }else{
                gustImage.setImageDrawable(context.getResources().getDrawable(gustToDrawable));
            }



            TextView currentDirection = (TextView)column.findViewById(R.id.direction);

            int currentDirectionVal = Integer.parseInt(currentDirection.getText().toString());

            //Log.d("WINDFINDER APP", "rotation FROM - " + currentDirectionVal);

            currentDirection.setText(windDirection+"");

            float rotateAmount = windDirection - currentDirectionVal;

            //Log.d("WINDFINDER APP", "rotation TO - " + windDirection);

            final int windDirectionFinal = windDirection;

            if(currentDirectionVal != windDirection){

                if(rotateAmount > 180) rotateAmount = rotateAmount - 360;
                else if(rotateAmount < -180) rotateAmount = rotateAmount + 360;

                final RotateAnimation rotateAnim = new RotateAnimation(0f, rotateAmount,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);

                rotateAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        //windImage.setRotation(windDirectionFinal);
                        //gustImage.setRotation(windDirectionFinal);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                        animation.setDuration(1);
                        windImage.startAnimation(animation);
                        gustImage.startAnimation(animation);
                        windImage.setRotation(windDirectionFinal);
                        gustImage.setRotation(windDirectionFinal);
                        windImage.clearAnimation();
                        gustImage.clearAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                if(animate) rotateAnim.setDuration(400);
                else rotateAnim.setDuration(0);
                rotateAnim.setFillAfter(true);
                rotateAnim.setFillBefore(false);
                gustImage.startAnimation(rotateAnim);
                windImage.startAnimation(rotateAnim);
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

            boolean waveColorChange = true;
            boolean periodColorChange = true;

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

            double windVal = -1; String windString = "";
            double gustVal = -1; String gustString = "";
            int windDirection = 0;

            if(intervalData != null){
                windVal = intervalData.swellPeriod;
                windString = Double.toString(windVal);
                gustVal = intervalData.swellHeight;
                gustString = Double.toString(gustVal);

                windDirection = intervalData.swellDirection;
            }

            String oldWindString;
            String oldGustString;

            TextSwitcher wind = (TextSwitcher) column.findViewById(R.id.windSpeedSwitcher);
            TextView windTextView = (TextView) wind.getCurrentView();
            if(windTextView == null){
                wind.setFactory(new ViewSwitcher.ViewFactory() {

                    public View makeView() {
                        TextView myText = new TextView(context);
                        myText.setGravity(Gravity.CENTER);

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                                Gravity.CENTER);
                        myText.setLayoutParams(params);

                        myText.setTextSize(14);
                        myText.setTextColor(Color.BLACK);
                        return myText;
                    }
                });
                oldWindString = "";
            }else{
                oldWindString = windTextView.getText().toString();
            }
            if(animate){
                wind.setInAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.fade_in));
                wind.setOutAnimation(context, R.anim.fade_out);
            }else{
                wind.setInAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.no_anim_in));
                wind.setOutAnimation(context, R.anim.no_anim_out);
            }

            TextSwitcher gust = (TextSwitcher) column.findViewById(R.id.gustSpeedSwitcher);
            TextView gustTextView = (TextView) gust.getCurrentView();
            if(gustTextView == null){
                gust.setFactory(new ViewSwitcher.ViewFactory() {

                    public View makeView() {
                        TextView myText = new TextView(context);
                        myText.setGravity(Gravity.CENTER);

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                                Gravity.CENTER);
                        myText.setLayoutParams(params);

                        myText.setTextSize(14);
                        myText.setTextColor(Color.WHITE);
                        return myText;
                    }
                });
                oldGustString = "";
            }else{
                oldGustString = gustTextView.getText().toString();
            }
            if(animate){
                gust.setInAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.fade_in));
                gust.setOutAnimation(context, R.anim.fade_out);
            }else{
                gust.setInAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.no_anim_in));
                gust.setOutAnimation(context, R.anim.no_anim_out);
            }

            if(oldWindString == "") oldWindString = "0";
            if(oldGustString == "") oldGustString = "0";

            final double oldWindVal = Double.parseDouble(oldWindString);
            final double oldGustVal = Double.parseDouble(oldGustString);

            wind.setText(windString);
            gust.setText(gustString);

            final ImageView gustImage = (ImageView)column.findViewById(R.id.gustImage);
            final ImageView windImage = (ImageView)column.findViewById(R.id.windImage);

            int windToDrawable;
            int gustToDrawable;

            /*if(windVal >= 45){ windToDrawable = R.drawable.swell_icon_12; if(oldWindVal >= 45) waveColorChange = false;}
            else if(windVal >= 40){windToDrawable =  R.drawable.swell_icon_11;if(oldWindVal >= 40 && oldWindVal < 45) waveColorChange = false;}
            else if(windVal >= 35){windToDrawable =  R.drawable.swell_icon_10;if(oldWindVal >= 35 && oldWindVal < 40) waveColorChange = false;}
            else if(windVal >= 30){windToDrawable =  R.drawable.swell_icon_9;if(oldWindVal >= 30 && oldWindVal < 35) waveColorChange = false;}
            else if(windVal >= 25){windToDrawable =  R.drawable.swell_icon_8;if(oldWindVal >= 25 && oldWindVal < 30) waveColorChange = false;}
            else if(windVal >= 22){windToDrawable =  R.drawable.swell_icon_7;if(oldWindVal >= 22 && oldWindVal < 25) waveColorChange = false;}
            else if(windVal >= 18){windToDrawable =  R.drawable.swell_icon_6;if(oldWindVal >= 18 && oldWindVal < 22) waveColorChange = false;}
            else if(windVal >= 14){windToDrawable =  R.drawable.swell_icon_5;if(oldWindVal >= 14 && oldWindVal < 18) waveColorChange = false;}
            else if(windVal >= 10){windToDrawable =  R.drawable.swell_icon_4;if(oldWindVal >= 10 && oldWindVal < 14) waveColorChange = false;}
            else if(windVal >= 6){windToDrawable =  R.drawable.swell_icon_3;if(oldWindVal >= 6 && oldWindVal < 10) waveColorChange = false;}
            else if(windVal >= 3){windToDrawable =  R.drawable.swell_icon_2;if(oldWindVal >= 3 && oldWindVal < 6) waveColorChange = false;}
            else if(windVal >= 1){windToDrawable =  R.drawable.swell_icon_1;if(oldWindVal >= 1 && oldWindVal < 3) waveColorChange = false;}
            else {windToDrawable =  R.drawable.swell_icon_0;if(oldWindVal == 0) waveColorChange = false;}*/

            if(gustVal >= 14.3){ gustToDrawable = R.drawable.swell_icon_12; if(oldGustVal >= 14.3) periodColorChange = false;}
            else if(gustVal >= 13.1){gustToDrawable =  R.drawable.swell_icon_11;if(oldGustVal >= 13.1 && oldGustVal < 14.3) periodColorChange = false;}
            else if(gustVal >= 11.8){gustToDrawable =  R.drawable.swell_icon_10;if(oldGustVal >= 11.8 && oldGustVal < 13.1) periodColorChange = false;}
            else if(gustVal >= 10.6){gustToDrawable =  R.drawable.swell_icon_9;if(oldGustVal >= 10.6 && oldGustVal < 11.8) periodColorChange = false;}
            else if(gustVal >= 9.4){gustToDrawable =  R.drawable.swell_icon_8;if(oldGustVal >= 9.4 && oldGustVal < 10.6) periodColorChange = false;}
            else if(gustVal >= 8.2){gustToDrawable =  R.drawable.swell_icon_7;if(oldGustVal >= 8.2 && oldGustVal < 9.4) periodColorChange = false;}
            else if(gustVal >= 5.7){gustToDrawable =  R.drawable.swell_icon_6;if(oldGustVal >= 5.7 && oldGustVal < 8.2) periodColorChange = false;}
            else if(gustVal >= 4.5){gustToDrawable =  R.drawable.swell_icon_5;if(oldGustVal >= 4.5 && oldGustVal < 5.7) periodColorChange = false;}
            else if(gustVal >= 3.3){gustToDrawable =  R.drawable.swell_icon_4;if(oldGustVal >= 3.3 && oldGustVal < 4.5) periodColorChange = false;}
            else if(gustVal >= 2.1){gustToDrawable =  R.drawable.swell_icon_3;if(oldGustVal >= 2.1 && oldGustVal < 3.3) periodColorChange = false;}
            else if(gustVal >= 0.9){gustToDrawable =  R.drawable.swell_icon_2;if(oldGustVal >= 0.9 && oldGustVal < 2.1) periodColorChange = false;}
            else if(gustVal >= 0){gustToDrawable =  R.drawable.swell_icon_1;if(oldGustVal >= 0 && oldGustVal < 0.9) periodColorChange = false;}
            else {gustToDrawable =  R.drawable.wind_icon_null;if(oldGustVal == 0) periodColorChange = false;}

            windToDrawable =  R.drawable.wind_icon_null;
            waveColorChange = false;

            if(waveColorChange){
                final TransitionDrawable windTransitionDrawable =
                        new TransitionDrawable(new Drawable[] {
                                windImage.getDrawable(),
                                context.getResources().getDrawable(windToDrawable)
                        });
                windImage.setImageDrawable(windTransitionDrawable);
                windTransitionDrawable.setCrossFadeEnabled(true);
                if(animate)windTransitionDrawable.startTransition(400);
                else windTransitionDrawable.startTransition(0);
            }else{
                windImage.setImageDrawable(context.getResources().getDrawable(windToDrawable));
            }
            if(periodColorChange){
                final TransitionDrawable gustTransitionDrawable =
                        new TransitionDrawable(new Drawable[] {
                                gustImage.getDrawable(),
                                context.getResources().getDrawable(gustToDrawable)
                        });
                gustImage.setImageDrawable(gustTransitionDrawable);
                gustTransitionDrawable.setCrossFadeEnabled(true);
                if(animate)gustTransitionDrawable.startTransition(400);
                else gustTransitionDrawable.startTransition(0);
            }else{
                gustImage.setImageDrawable(context.getResources().getDrawable(gustToDrawable));
            }



            TextView currentDirection = (TextView)column.findViewById(R.id.direction);

            int currentDirectionVal = Integer.parseInt(currentDirection.getText().toString());

            //Log.d("WINDFINDER APP", "rotation FROM - " + currentDirectionVal);

            currentDirection.setText(windDirection+"");

            float rotateAmount = windDirection - currentDirectionVal;

            //Log.d("WINDFINDER APP", "rotation TO - " + windDirection);

            final int windDirectionFinal = windDirection;

            if(currentDirectionVal != windDirection){

                if(rotateAmount > 180) rotateAmount = rotateAmount - 360;
                else if(rotateAmount < -180) rotateAmount = rotateAmount + 360;

                final RotateAnimation rotateAnim = new RotateAnimation(0f, rotateAmount,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);

                rotateAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        //windImage.setRotation(windDirectionFinal);
                        //gustImage.setRotation(windDirectionFinal);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                        animation.setDuration(1);
                        windImage.startAnimation(animation);
                        gustImage.startAnimation(animation);
                        windImage.setRotation(windDirectionFinal);
                        gustImage.setRotation(windDirectionFinal);
                        windImage.clearAnimation();
                        gustImage.clearAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                if(animate) rotateAnim.setDuration(400);
                else rotateAnim.setDuration(0);
                rotateAnim.setFillAfter(true);
                rotateAnim.setFillBefore(false);
                gustImage.startAnimation(rotateAnim);
                windImage.startAnimation(rotateAnim);
            }
        }
    }
}

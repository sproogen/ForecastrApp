package com.jamesg.windforecast;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;
import android.os.Handler;

import com.afollestad.cardsui.CardAdapter;
import com.afollestad.cardsui.CardBase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jamesg.windforecast.data.Spot;
import com.jamesg.windforecast.data.TimestampData;
import android.support.v4.app.FragmentManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CustomCardAdapter extends CardAdapter {

    private Context context;

    private int overviewDisplay = 0;
    private String openSpot = "";

    private Spot thisSpot;

    private GoogleMap googleMap;
    private int mapSpot = -1;
    private int mapType = GoogleMap.MAP_TYPE_NORMAL;
    private FragmentManager fragmentManager;

    private TextSwitcher mSwitcher;

    public CustomCardAdapter(Context context, int overviewDisplay, String openSpot, FragmentManager fragmentManager) {
        super(context);
        this.context = context;
        this.overviewDisplay = overviewDisplay;
        this.openSpot = openSpot;
        this.fragmentManager = fragmentManager;

        if(!openSpot.equals("")){
            thisSpot = MainActivity.data.getSpot(openSpot);
        }

        // Register three custom card layouts...
        // You must register them so that list items that use these layouts can be recycled by the system correctly.
        registerLayout(R.layout.card_layout_header);
        registerLayout(R.layout.card_layout_overview);
        registerLayout(R.layout.card_layout_weather);
        registerLayout(R.layout.card_layout_swell);
        registerLayout(R.layout.card_layout_map);
    }

    @Override
    protected boolean onProcessThumbnail(ImageView icon, CardBase card) {
        // Optional. If a custom layout has a view with the ID @android:id/icon, this is called.
        // RETURN TRUE: the card thumbnail view is visible.
        // RETURN FALSE: the card thumbnail view is gone (appears to not be there at all).
        return super.onProcessThumbnail(icon, card);
    }

    @Override
    protected boolean onProcessTitle(TextView title, CardBase card, int accentColor) {
        // Optional. If a custom layout has a view with the ID @android:id/title, this is called.
        return super.onProcessTitle(title, card, accentColor);
    }

    @Override
    protected boolean onProcessContent(TextView content, CardBase card) {
        // Optional. If a custom layout has a view with the ID @android:id/content, this is called.
        return super.onProcessContent(content, card);
    }

    @Override
    protected boolean onProcessMenu(View view, CardBase card) {
        // Optional. If a custom layout has a view with the ID @android:id/button1, this is called.
        // Do not call the super function if you want to override the default behavior. The super function attaches a popup menu.
        return super.onProcessMenu(view, card);
    }

    @Override
    public View onViewCreated(int index, View recycled, CardBase item) {
        if (((CustomCard)item).getType().equals("header")) {
            return setupHeader(item, recycled);
        }else if(((CustomCard)item).getType().equals("wind")){
            return setupWindCard(index,recycled,item);
        }else if(((CustomCard)item).getType().equals("weather")){
            return setupWeatherCard(index,recycled,item);
        }else if(((CustomCard)item).getType().equals("swell")){
            return setupSwellCard(index,recycled,item);
        }else if(((CustomCard)item).getType().equals("map")){
            return setupMapCard(index,recycled,item);
        }else{
            return setupBlankCard(index, recycled, item);
        }
    }

    public View setupBlankCard(int index, View recycled, CardBase item) {

        View card = super.onViewCreated(index, recycled, item);

        return card;
    }

    public View setupHeader(CardBase header, View recycled) {
        TextView title = (TextView) recycled.findViewById(android.R.id.title);
        if (title == null)
            throw new RuntimeException("Your header layout must contain a TextView with the ID @android:id/title.");
        final TextView subtitle = (TextView) recycled.findViewById(android.R.id.content);
        if (subtitle == null)
            throw new RuntimeException("Your header layout must contain a TextView with the ID @android:id/content.");
        title.setText(header.getTitle());
        if (header.getContent() != null && !header.getContent().trim().isEmpty()) {
            //subtitle.setVisibility(View.VISIBLE);
            subtitle.setText(header.getContent());
        } //else subtitle.setVisibility(View.GONE);
        TextView button = (TextView) recycled.findViewById(android.R.id.button1);
        if (button == null)
            throw new RuntimeException("The header layout must contain a TextView with the ID @android:id/button1.");
        if (header.getActionCallback() != null) {
            button.setVisibility(View.VISIBLE);
            button.setBackgroundColor(mAccentColor);
            String titleTxt = header.getActionTitle();
            if (header.getActionTitle() == null || header.getActionTitle().trim().isEmpty())
                titleTxt = getContext().getString(R.string.see_more);
            button.setText(titleTxt);
        } else button.setVisibility(View.GONE);

        if(overviewDisplay == 0){
            if(subtitle.getVisibility() == View.GONE){
                MyScaler scale = new MyScaler(1.0f, 1.0f, 0.0f, 1.0f, subtitle);
                scale.setDuration(400);
                scale.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        subtitle.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {}

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                subtitle.startAnimation(scale);
            }
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MMM");
            subtitle.setText(df.format(c.getTime()));
        }else if(overviewDisplay == 1){
            if(subtitle.getVisibility() == View.GONE){
                subtitle.setVisibility(View.VISIBLE);
                MyScaler scale = new MyScaler(1.0f, 1.0f, 0.0f, 1.0f, subtitle);
                scale.setDuration(400);
                scale.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        subtitle.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {}

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                subtitle.startAnimation(scale);
            }
            Calendar c = Calendar.getInstance();
            c.add(c.DAY_OF_MONTH, 1);
            SimpleDateFormat df = new SimpleDateFormat("dd/MMM");
            subtitle.setText(df.format(c.getTime()));
        }else{
            if(subtitle.getVisibility() == View.VISIBLE){
                MyScaler scale = new MyScaler(1.0f, 1.0f, 1.0f, 0.0f, subtitle);
                scale.setDuration(400);
                scale.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        subtitle.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                subtitle.startAnimation(scale);
            }
        }
        return recycled;
    }

    public View setupMapCard(int index, View recycled, CardBase item) {

        View card = super.onViewCreated(index, recycled, item);

        SupportMapFragment mapFragment = ((SupportMapFragment)
                fragmentManager.findFragmentByTag("mapFragment"));

        if(mapFragment==null){
            mapFragment = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(R.id.map, mapFragment, "mapFragment")
                    .addToBackStack(null)
                    .commit();
        }else{
            //removeMap();
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){

            @Override
            public void run() {
                googleMap = ((SupportMapFragment)
                        fragmentManager.findFragmentByTag("mapFragment")).getMap();
                if(googleMap != null) {
                    if(mapSpot == -1){
                        mapSpot = thisSpot.getId();
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                        LatLng spotLatLng = new LatLng(Double.parseDouble(thisSpot.getLatitude()), Double.parseDouble(thisSpot.getLongitude()));
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions()
                                .position(spotLatLng)
                                .title(thisSpot.getName())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                        googleMap.getUiSettings().setCompassEnabled(false);
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        googleMap.getUiSettings().setAllGesturesEnabled(false);

                        float cameraZoom = 10;

                        googleMap.setMapType(mapType);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(spotLatLng, cameraZoom));
                    }
                }else {
                    handler.postDelayed(this, 500);
                }
            }
        },500);

        return card;
    }

    public void removeMap(){
        try{
            SupportMapFragment mapFragment = ((SupportMapFragment)
                    fragmentManager.findFragmentByTag("mapFragment"));
            if(mapFragment != null){
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.remove(mapFragment);
                ft.commit();
            }
        }catch(Exception e){
            //DO NOTHING
        }
    }

    public View setupWeatherCard(int index, View recycled, CardBase item) {

        boolean animate = true;

        TextView titleTextView = (TextView)recycled.findViewById(android.R.id.title);
        final String oldtitle = titleTextView.getText().toString();

        View card = super.onViewCreated(index, recycled, item);

        if(openSpot.equals("")){
            thisSpot = MainActivity.data.getSpot(item.getTitle());
        }
        //thisSpot.parseRawData();

        if(!oldtitle.equals(item.getTitle())) animate = false;

        Calendar c = null;
        SimpleDateFormat df = new SimpleDateFormat("EEE");
        if(overviewDisplay == 2) c = Calendar.getInstance();

        int timeSteps = 7;
        if(overviewDisplay == 2){
            timeSteps = thisSpot.getSevenDayDayCount();
        }
        for(int i=0; i < 7; i++){

            boolean weatherChange = true;
            boolean tempChange = true;

            int timeStamp = (i+1)*3;

            TimestampData intervalData = null;
            if(overviewDisplay == 0){
                intervalData = thisSpot.getTodayTimestamp(timeStamp);
            }else if(overviewDisplay == 1){
                intervalData = thisSpot.getTomorrowTimestamp(timeStamp);
            }else {
                if(i < timeSteps)intervalData = thisSpot.getSevenDayDay(i);
                else intervalData = null;
            }

            //Log.d("WINDFINDER APP", "Column" + i);
            int colID = context.getResources().getIdentifier("column"+(i+1), "id", context.getPackageName());
            View column = card.findViewById(colID);

            TextView time = (TextView) column.findViewById(R.id.valueText);

            if(overviewDisplay == 0 || overviewDisplay == 1){
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
                temp.setFactory(new ViewFactory() {

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
        return card;
    }

    public View setupWindCard(int index, View recycled, CardBase item) {

        boolean animate = true;

        TextView titleTextView = (TextView)recycled.findViewById(android.R.id.title);
        final String oldtitle = titleTextView.getText().toString();

        View card = super.onViewCreated(index, recycled, item);

        if(openSpot.equals("")){
            thisSpot = MainActivity.data.getSpot(item.getTitle());
        }

        //Log.d("WINDFINDER APP",thisSpot.getName() + " Sun Rise - "+ thisSpot.getSunRise());
        //Log.d("WINDFINDER APP",thisSpot.getName() + " Sun Set - "+ thisSpot.getSunSet());

        if(!oldtitle.equals(item.getTitle())) animate = false;

        Calendar c = null;
        SimpleDateFormat df = new SimpleDateFormat("EEE");
        if(overviewDisplay == 2) c = Calendar.getInstance();

        int timeSteps = 7;
        if(overviewDisplay == 2){
            timeSteps = thisSpot.getSevenDayDayCount();
        }
        for(int i=0; i < 7; i++){

            boolean windColorChange = true;
            boolean gustColorChange = true;

            int timeStamp = (i+1)*3;

            TimestampData intervalData = null;
            if(overviewDisplay == 0){
                intervalData = thisSpot.getTodayTimestamp(timeStamp);
            }else if(overviewDisplay == 1){
                intervalData = thisSpot.getTomorrowTimestamp(timeStamp);
            }else {
                if(i < timeSteps)intervalData = thisSpot.getSevenDayDay(i);
                else intervalData = null;
            }

            //Log.d("WINDFINDER APP", "Column" + i);
            int colID = context.getResources().getIdentifier("column"+(i+1), "id", context.getPackageName());
            View column = card.findViewById(colID);

            TextView time = (TextView) column.findViewById(R.id.valueText);

            if(overviewDisplay == 0 || overviewDisplay == 1){
                int timeID = context.getResources().getIdentifier("time"+(i+1), "string", context.getPackageName());
                time.setText(timeID);
            }else{
                time.setText(df.format(c.getTime()));
                c.add(c.DAY_OF_MONTH, 1);
            }

            int windVal = 0; String windString = "";
            int gustVal = 0; String gustString = "";
            int windDirection = 0;

            if(intervalData != null){
                windVal = intervalData.windSpeed;
                windString = Integer.toString(windVal);
                gustVal = intervalData.windGust;
                gustString = Integer.toString(gustVal);

                windDirection = intervalData.windDirection;
            }

            String oldWindString;
            String oldGustString;

            TextSwitcher wind = (TextSwitcher) column.findViewById(R.id.windSpeed);
            TextView windTextView = (TextView) wind.getCurrentView();
            if(windTextView == null){
                wind.setFactory(new ViewFactory() {

                    public View makeView() {
                        TextView myText = new TextView(context);
                        myText.setGravity(Gravity.CENTER);

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                                Gravity.CENTER);
                        myText.setLayoutParams(params);

                        myText.setTextSize(18);
                        myText.setTextColor(Color.WHITE);
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

            TextSwitcher gust = (TextSwitcher) column.findViewById(R.id.gustSpeed);
            TextView gustTextView = (TextView) gust.getCurrentView();
            if(gustTextView == null){
                gust.setFactory(new ViewFactory() {

                    public View makeView() {
                        TextView myText = new TextView(context);
                        myText.setGravity(Gravity.CENTER);

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                                Gravity.CENTER);
                        myText.setLayoutParams(params);

                        myText.setTextSize(18);
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

            final int oldWindVal = Integer.parseInt(oldWindString);
            final int oldGustVal = Integer.parseInt(oldGustString);

            wind.setText(windString);
            gust.setText(gustString);

            final ImageView gustImage = (ImageView)column.findViewById(R.id.gustImage);
            final ImageView windImage = (ImageView)column.findViewById(R.id.windImage);

            int windToDrawable;
            int gustToDrawable;

            if(windVal >= 45){ windToDrawable = R.drawable.wind_icon_12; if(oldWindVal >= 45) windColorChange = false;}
            else if(windVal >= 40){windToDrawable =  R.drawable.wind_icon_11;if(oldWindVal >= 40 && oldWindVal < 45) windColorChange = false;}
            else if(windVal >= 35){windToDrawable =  R.drawable.wind_icon_10;if(oldWindVal >= 35 && oldWindVal < 40) windColorChange = false;}
            else if(windVal >= 30){windToDrawable =  R.drawable.wind_icon_9;if(oldWindVal >= 30 && oldWindVal < 35) windColorChange = false;}
            else if(windVal >= 25){windToDrawable =  R.drawable.wind_icon_8;if(oldWindVal >= 25 && oldWindVal < 30) windColorChange = false;}
            else if(windVal >= 22){windToDrawable =  R.drawable.wind_icon_7;if(oldWindVal >= 22 && oldWindVal < 25) windColorChange = false;}
            else if(windVal >= 18){windToDrawable =  R.drawable.wind_icon_6;if(oldWindVal >= 18 && oldWindVal < 22) windColorChange = false;}
            else if(windVal >= 14){windToDrawable =  R.drawable.wind_icon_5;if(oldWindVal >= 14 && oldWindVal < 18) windColorChange = false;}
            else if(windVal >= 10){windToDrawable =  R.drawable.wind_icon_4;if(oldWindVal >= 10 && oldWindVal < 14) windColorChange = false;}
            else if(windVal >= 6){windToDrawable =  R.drawable.wind_icon_3;if(oldWindVal >= 6 && oldWindVal < 10) windColorChange = false;}
            else if(windVal >= 3){windToDrawable =  R.drawable.wind_icon_2;if(oldWindVal >= 3 && oldWindVal < 6) windColorChange = false;}
            else if(windVal >= 1){windToDrawable =  R.drawable.wind_icon_1;if(oldWindVal >= 1 && oldWindVal < 3) windColorChange = false;}
            else {windToDrawable =  R.drawable.wind_icon_0;if(oldWindVal == 0) windColorChange = false;}

            if(gustVal >= 45){ gustToDrawable = R.drawable.wind_icon_12; if(oldGustVal >= 45) gustColorChange = false;}
            else if(gustVal >= 40){gustToDrawable =  R.drawable.wind_icon_11;if(oldGustVal >= 40 && oldGustVal < 45) gustColorChange = false;}
            else if(gustVal >= 35){gustToDrawable =  R.drawable.wind_icon_10;if(oldGustVal >= 35 && oldGustVal < 40) gustColorChange = false;}
            else if(gustVal >= 30){gustToDrawable =  R.drawable.wind_icon_9;if(oldGustVal >= 30 && oldGustVal < 35) gustColorChange = false;}
            else if(gustVal >= 25){gustToDrawable =  R.drawable.wind_icon_8;if(oldGustVal >= 25 && oldGustVal < 30) gustColorChange = false;}
            else if(gustVal >= 22){gustToDrawable =  R.drawable.wind_icon_7;if(oldGustVal >= 22 && oldGustVal < 25) gustColorChange = false;}
            else if(gustVal >= 18){gustToDrawable =  R.drawable.wind_icon_6;if(oldGustVal >= 18 && oldGustVal < 22) gustColorChange = false;}
            else if(gustVal >= 14){gustToDrawable =  R.drawable.wind_icon_5;if(oldGustVal >= 14 && oldGustVal < 18) gustColorChange = false;}
            else if(gustVal >= 10){gustToDrawable =  R.drawable.wind_icon_4;if(oldGustVal >= 10 && oldGustVal < 14) gustColorChange = false;}
            else if(gustVal >= 6){gustToDrawable =  R.drawable.wind_icon_3;if(oldGustVal >= 6 && oldGustVal < 10) gustColorChange = false;}
            else if(gustVal >= 3){gustToDrawable =  R.drawable.wind_icon_2;if(oldGustVal >= 3 && oldGustVal < 6) gustColorChange = false;}
            else if(gustVal >= 1){gustToDrawable =  R.drawable.wind_icon_1;if(oldGustVal >= 1 && oldGustVal < 3) gustColorChange = false;}
            else {gustToDrawable =  R.drawable.wind_icon_0;if(oldGustVal == 0) gustColorChange = false;}

            if(windColorChange){
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
            if(gustColorChange){
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
        return card;
    }

    public View setupSwellCard(int index, View recycled, CardBase item) {

        boolean animate = true;

        TextView titleTextView = (TextView)recycled.findViewById(android.R.id.title);
        final String oldtitle = titleTextView.getText().toString();

        View card = super.onViewCreated(index, recycled, item);

        if(openSpot.equals("")){
            thisSpot = MainActivity.data.getSpot(item.getTitle());
        }
        //thisSpot.parseRawData();

        //Log.d("WINDFINDER APP",thisSpot.getName() + " Sun Rise - "+ thisSpot.getSunRise());
        //Log.d("WINDFINDER APP",thisSpot.getName() + " Sun Set - "+ thisSpot.getSunSet());

        if(!oldtitle.equals(item.getTitle())) animate = false;

        Calendar c = null;
        SimpleDateFormat df = new SimpleDateFormat("EEE");
        if(overviewDisplay == 2) c = Calendar.getInstance();

        int timeSteps = 7;
        if(overviewDisplay == 2){
            timeSteps = thisSpot.getSevenDayDayCount();
        }
        for(int i=0; i < 7; i++){

            boolean waveColorChange = true;
            boolean periodColorChange = true;

            int timeStamp = (i+1)*3;

            TimestampData intervalData = null;
            if(overviewDisplay == 0){
                intervalData = thisSpot.getTodayTimestamp(timeStamp);
            }else if(overviewDisplay == 1){
                intervalData = thisSpot.getTomorrowTimestamp(timeStamp);
            }else {
                if(i < timeSteps)intervalData = thisSpot.getSevenDayDay(i);
                else intervalData = null;
            }

            //Log.d("WINDFINDER APP", "Column" + i);
            int colID = context.getResources().getIdentifier("column"+(i+1), "id", context.getPackageName());
            View column = card.findViewById(colID);

            TextView time = (TextView) column.findViewById(R.id.valueText);

            if(overviewDisplay == 0 || overviewDisplay == 1){
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

            TextSwitcher wind = (TextSwitcher) column.findViewById(R.id.windSpeed);
            TextView windTextView = (TextView) wind.getCurrentView();
            if(windTextView == null){
                wind.setFactory(new ViewFactory() {

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

            TextSwitcher gust = (TextSwitcher) column.findViewById(R.id.gustSpeed);
            TextView gustTextView = (TextView) gust.getCurrentView();
            if(gustTextView == null){
                gust.setFactory(new ViewFactory() {

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
        return card;
    }

    public class MyScaler extends ScaleAnimation {

        private View mView;

        private LinearLayout.LayoutParams mLayoutParams;

        private int mMarginBottomFromY, mMarginBottomToY;

        public MyScaler(float fromX, float toX, float fromY, float toY, View view) {
            super(fromX, toX, fromY, toY);
            mView = view;
            mLayoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
            int height = mView.getHeight();
            mMarginBottomFromY = (int) (height * fromY) - height;
            mMarginBottomToY = (int) (height * toY) - height;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                int newMarginBottom = mMarginBottomFromY
                        + (int) ((mMarginBottomToY - mMarginBottomFromY) * interpolatedTime);
                mLayoutParams.setMargins(mLayoutParams.leftMargin, mLayoutParams.topMargin,
                        mLayoutParams.rightMargin, newMarginBottom);
                mView.getParent().requestLayout();
            }
        }
    }

    public void updateOverviewDisplay(int overviewDisplay){
        this.overviewDisplay = overviewDisplay;
        notifyDataSetChanged();
    }
}
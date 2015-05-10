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
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.jamesg.forecastr.R;
import com.jamesg.forecastr.base.CardBase;
import com.jamesg.forecastr.data.Spot;
import com.jamesg.forecastr.data.TimestampData;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by James on 17/10/2014.
 */
public class TideCard extends CardBase {

    private TextView titleTextView;

    public TideCard(Context context, Spot spot, int dateTab){
        this.context = context;
        this.spot = spot;
        this.dateTab = dateTab;
    }

    @Override
    public String getTitle() {
        return "Tide";
    }

    public View getView(LayoutInflater inflater){

        View view = inflater.inflate(R.layout.card_layout_tide, null);
        view.setTag(getTag());

        titleTextView = (TextView) view.findViewById(android.R.id.title);
        titleTextView.setText(getTitle());

        boolean animate = true;

        Calendar c = null;
        SimpleDateFormat df = new SimpleDateFormat("EEE");
        if(dateTab == 2) {
            view.setVisibility(View.GONE);
            return view;
        }

        ArrayList<TimestampData> tideTimestampData = spot.getTide(dateTab);

        if(tideTimestampData.size() > 0) {

            LinearLayout row1 = (LinearLayout) view.findViewById(R.id.row1);
            row1.setVisibility(View.VISIBLE);

            ImageView row1_image = (ImageView) view.findViewById(R.id.row1_image);
            TextSwitcher row1_time = (TextSwitcher) view.findViewById(R.id.row1_time);
            TextView row1_time_tv = (TextView) row1_time.getCurrentView();
            if (row1_time_tv == null) {
                row1_time.setFactory(new ViewSwitcher.ViewFactory() {

                    public View makeView() {
                        TextView myText = new TextView(context);

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
            row1_time.setInAnimation(AnimationUtils.loadAnimation(context,
                    R.anim.fade_in));
            row1_time.setOutAnimation(context, R.anim.fade_out);

            TextSwitcher row1_height = (TextSwitcher) view.findViewById(R.id.row1_height);
            TextView row1_height_tv = (TextView) row1_height.getCurrentView();
            if (row1_height_tv == null) {
                row1_height.setFactory(new ViewSwitcher.ViewFactory() {

                    public View makeView() {
                        TextView myText = new TextView(context);

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
            row1_height.setInAnimation(AnimationUtils.loadAnimation(context,
                    R.anim.fade_in));
            row1_height.setOutAnimation(context, R.anim.fade_out);

            TimestampData tide1 = tideTimestampData.get(0);

            row1_time.setText(tide1.tideTime);
            row1_height.setText(tide1.tideValue + "m");

            if(tide1.tideHL.equals("H")){
                row1_image.setImageDrawable(context.getResources().getDrawable(R.drawable.tide_high));
            }else{
                row1_image.setImageDrawable(context.getResources().getDrawable(R.drawable.tide_low));
            }

            if(tideTimestampData.size() > 1) {

                LinearLayout row2 = (LinearLayout) view.findViewById(R.id.row2);
                row2.setVisibility(View.VISIBLE);

                ImageView row2_image = (ImageView) view.findViewById(R.id.row2_image);
                TextSwitcher row2_time = (TextSwitcher) view.findViewById(R.id.row2_time);
                TextView row2_time_tv = (TextView) row2_time.getCurrentView();
                if (row2_time_tv == null) {
                    row2_time.setFactory(new ViewSwitcher.ViewFactory() {

                        public View makeView() {
                            TextView myText = new TextView(context);

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
                row2_time.setInAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.fade_in));
                row2_time.setOutAnimation(context, R.anim.fade_out);

                TextSwitcher row2_height = (TextSwitcher) view.findViewById(R.id.row2_height);
                TextView row2_height_tv = (TextView) row2_height.getCurrentView();
                if (row2_height_tv == null) {
                    row2_height.setFactory(new ViewSwitcher.ViewFactory() {

                        public View makeView() {
                            TextView myText = new TextView(context);

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
                row2_height.setInAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.fade_in));
                row2_height.setOutAnimation(context, R.anim.fade_out);

                TimestampData tide2 = tideTimestampData.get(1);

                row2_time.setText(tide2.tideTime);
                row2_height.setText(tide2.tideValue + "m");

                if(tide2.tideHL.equals("H")){
                    row2_image.setImageDrawable(context.getResources().getDrawable(R.drawable.tide_high));
                }else{
                    row2_image.setImageDrawable(context.getResources().getDrawable(R.drawable.tide_low));
                }

                if(tideTimestampData.size() > 2) {

                    LinearLayout row3 = (LinearLayout) view.findViewById(R.id.row3);
                    row3.setVisibility(View.VISIBLE);

                    ImageView row3_image = (ImageView) view.findViewById(R.id.row3_image);
                    TextSwitcher row3_time = (TextSwitcher) view.findViewById(R.id.row3_time);
                    TextView row3_time_tv = (TextView) row3_time.getCurrentView();
                    if (row3_time_tv == null) {
                        row3_time.setFactory(new ViewSwitcher.ViewFactory() {

                            public View makeView() {
                                TextView myText = new TextView(context);

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
                    row3_time.setInAnimation(AnimationUtils.loadAnimation(context,
                            R.anim.fade_in));
                    row3_time.setOutAnimation(context, R.anim.fade_out);

                    TextSwitcher row3_height = (TextSwitcher) view.findViewById(R.id.row3_height);
                    TextView row3_height_tv = (TextView) row3_height.getCurrentView();
                    if (row3_height_tv == null) {
                        row3_height.setFactory(new ViewSwitcher.ViewFactory() {

                            public View makeView() {
                                TextView myText = new TextView(context);

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
                    row3_height.setInAnimation(AnimationUtils.loadAnimation(context,
                            R.anim.fade_in));
                    row3_height.setOutAnimation(context, R.anim.fade_out);

                    TimestampData tide3 = tideTimestampData.get(2);

                    row3_time.setText(tide3.tideTime);
                    row3_height.setText(tide3.tideValue + "m");

                    if(tide3.tideHL.equals("H")){
                        row3_image.setImageDrawable(context.getResources().getDrawable(R.drawable.tide_high));
                    }else{
                        row3_image.setImageDrawable(context.getResources().getDrawable(R.drawable.tide_low));
                    }

                    if(tideTimestampData.size() > 3) {

                        LinearLayout row4 = (LinearLayout) view.findViewById(R.id.row4);
                        row4.setVisibility(View.VISIBLE);

                        ImageView row4_image = (ImageView) view.findViewById(R.id.row4_image);
                        TextSwitcher row4_time = (TextSwitcher) view.findViewById(R.id.row4_time);
                        TextView row4_time_tv = (TextView) row4_time.getCurrentView();
                        if (row4_time_tv == null) {
                            row4_time.setFactory(new ViewSwitcher.ViewFactory() {

                                public View makeView() {
                                    TextView myText = new TextView(context);

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
                        row4_time.setInAnimation(AnimationUtils.loadAnimation(context,
                                R.anim.fade_in));
                        row4_time.setOutAnimation(context, R.anim.fade_out);

                        TextSwitcher row4_height = (TextSwitcher) view.findViewById(R.id.row4_height);
                        TextView row4_height_tv = (TextView) row4_height.getCurrentView();
                        if (row4_height_tv == null) {
                            row4_height.setFactory(new ViewSwitcher.ViewFactory() {

                                public View makeView() {
                                    TextView myText = new TextView(context);

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
                        row4_height.setInAnimation(AnimationUtils.loadAnimation(context,
                                R.anim.fade_in));
                        row4_height.setOutAnimation(context, R.anim.fade_out);

                        TimestampData tide4 = tideTimestampData.get(3);

                        row4_time.setText(tide4.tideTime);
                        row4_height.setText(tide4.tideValue + "m");

                        if(tide4.tideHL.equals("H")){
                            row4_image.setImageDrawable(context.getResources().getDrawable(R.drawable.tide_high));
                        }else{
                            row4_image.setImageDrawable(context.getResources().getDrawable(R.drawable.tide_low));
                        }
                    }else{
                        LinearLayout row4 = (LinearLayout) view.findViewById(R.id.row4);
                        row4.setVisibility(View.GONE);
                    }
                }else{
                    LinearLayout row3 = (LinearLayout) view.findViewById(R.id.row3);
                    row3.setVisibility(View.GONE);
                    LinearLayout row4 = (LinearLayout) view.findViewById(R.id.row4);
                    row4.setVisibility(View.GONE);
                }
            }else{
                LinearLayout row2 = (LinearLayout) view.findViewById(R.id.row2);
                row2.setVisibility(View.GONE);
                LinearLayout row3 = (LinearLayout) view.findViewById(R.id.row3);
                row3.setVisibility(View.GONE);
                LinearLayout row4 = (LinearLayout) view.findViewById(R.id.row4);
                row4.setVisibility(View.GONE);
            }
        }else{
            LinearLayout row1 = (LinearLayout) view.findViewById(R.id.row1);
            row1.setVisibility(View.GONE);
            LinearLayout row2 = (LinearLayout) view.findViewById(R.id.row2);
            row2.setVisibility(View.GONE);
            LinearLayout row3 = (LinearLayout) view.findViewById(R.id.row3);
            row3.setVisibility(View.GONE);
            LinearLayout row4 = (LinearLayout) view.findViewById(R.id.row4);
            row4.setVisibility(View.GONE);
        }
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

        ArrayList<TimestampData> tideTimestampData = spot.getTide(dateTab);

        if(tideTimestampData.size() > 0) {

            LinearLayout row1 = (LinearLayout) view.findViewById(R.id.row1);
            row1.setVisibility(View.VISIBLE);

            ImageView row1_image = (ImageView) view.findViewById(R.id.row1_image);
            TextSwitcher row1_time = (TextSwitcher) view.findViewById(R.id.row1_time);
            TextView row1_time_tv = (TextView) row1_time.getCurrentView();
            if (row1_time_tv == null) {
                row1_time.setFactory(new ViewSwitcher.ViewFactory() {

                    public View makeView() {
                        TextView myText = new TextView(context);

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
            row1_time.setInAnimation(AnimationUtils.loadAnimation(context,
                    R.anim.fade_in));
            row1_time.setOutAnimation(context, R.anim.fade_out);

            TextSwitcher row1_height = (TextSwitcher) view.findViewById(R.id.row1_height);
            TextView row1_height_tv = (TextView) row1_height.getCurrentView();
            if (row1_height_tv == null) {
                row1_height.setFactory(new ViewSwitcher.ViewFactory() {

                    public View makeView() {
                        TextView myText = new TextView(context);

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
            row1_height.setInAnimation(AnimationUtils.loadAnimation(context,
                    R.anim.fade_in));
            row1_height.setOutAnimation(context, R.anim.fade_out);

            TimestampData tide1 = tideTimestampData.get(0);

            row1_time.setText(tide1.tideTime);
            row1_height.setText(tide1.tideValue + "m");

            if(tide1.tideHL.equals("H")){
                final TransitionDrawable windTransitionDrawable =
                    new TransitionDrawable(new Drawable[] {
                            row1_image.getDrawable(),
                            context.getResources().getDrawable(R.drawable.tide_high)
                    });
                row1_image.setImageDrawable(windTransitionDrawable);
                windTransitionDrawable.setCrossFadeEnabled(true);
                windTransitionDrawable.startTransition(400);
            }else{
                final TransitionDrawable windTransitionDrawable =
                        new TransitionDrawable(new Drawable[] {
                                row1_image.getDrawable(),
                                context.getResources().getDrawable(R.drawable.tide_low)
                        });
                row1_image.setImageDrawable(windTransitionDrawable);
                windTransitionDrawable.setCrossFadeEnabled(true);
                windTransitionDrawable.startTransition(400);
            }

            if(tideTimestampData.size() > 1) {

                LinearLayout row2 = (LinearLayout) view.findViewById(R.id.row2);
                row2.setVisibility(View.VISIBLE);

                ImageView row2_image = (ImageView) view.findViewById(R.id.row2_image);
                TextSwitcher row2_time = (TextSwitcher) view.findViewById(R.id.row2_time);
                TextView row2_time_tv = (TextView) row2_time.getCurrentView();
                if (row2_time_tv == null) {
                    row2_time.setFactory(new ViewSwitcher.ViewFactory() {

                        public View makeView() {
                            TextView myText = new TextView(context);

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
                row2_time.setInAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.fade_in));
                row2_time.setOutAnimation(context, R.anim.fade_out);

                TextSwitcher row2_height = (TextSwitcher) view.findViewById(R.id.row2_height);
                TextView row2_height_tv = (TextView) row2_height.getCurrentView();
                if (row2_height_tv == null) {
                    row2_height.setFactory(new ViewSwitcher.ViewFactory() {

                        public View makeView() {
                            TextView myText = new TextView(context);

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
                row2_height.setInAnimation(AnimationUtils.loadAnimation(context,
                        R.anim.fade_in));
                row2_height.setOutAnimation(context, R.anim.fade_out);

                TimestampData tide2 = tideTimestampData.get(1);

                row2_time.setText(tide2.tideTime);
                row2_height.setText(tide2.tideValue + "m");

                if(tide2.tideHL.equals("H")){
                    final TransitionDrawable windTransitionDrawable =
                            new TransitionDrawable(new Drawable[] {
                                    row2_image.getDrawable(),
                                    context.getResources().getDrawable(R.drawable.tide_high)
                            });
                    row2_image.setImageDrawable(windTransitionDrawable);
                    windTransitionDrawable.setCrossFadeEnabled(true);
                    windTransitionDrawable.startTransition(400);
                }else{
                    final TransitionDrawable windTransitionDrawable =
                            new TransitionDrawable(new Drawable[] {
                                    row2_image.getDrawable(),
                                    context.getResources().getDrawable(R.drawable.tide_low)
                            });
                    row2_image.setImageDrawable(windTransitionDrawable);
                    windTransitionDrawable.setCrossFadeEnabled(true);
                    windTransitionDrawable.startTransition(400);
                }

                if(tideTimestampData.size() > 2) {

                    LinearLayout row3 = (LinearLayout) view.findViewById(R.id.row3);
                    row3.setVisibility(View.VISIBLE);

                    ImageView row3_image = (ImageView) view.findViewById(R.id.row3_image);
                    TextSwitcher row3_time = (TextSwitcher) view.findViewById(R.id.row3_time);
                    TextView row3_time_tv = (TextView) row3_time.getCurrentView();
                    if (row3_time_tv == null) {
                        row3_time.setFactory(new ViewSwitcher.ViewFactory() {

                            public View makeView() {
                                TextView myText = new TextView(context);

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
                    row3_time.setInAnimation(AnimationUtils.loadAnimation(context,
                            R.anim.fade_in));
                    row3_time.setOutAnimation(context, R.anim.fade_out);

                    TextSwitcher row3_height = (TextSwitcher) view.findViewById(R.id.row3_height);
                    TextView row3_height_tv = (TextView) row3_height.getCurrentView();
                    if (row3_height_tv == null) {
                        row3_height.setFactory(new ViewSwitcher.ViewFactory() {

                            public View makeView() {
                                TextView myText = new TextView(context);

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
                    row3_height.setInAnimation(AnimationUtils.loadAnimation(context,
                            R.anim.fade_in));
                    row3_height.setOutAnimation(context, R.anim.fade_out);

                    TimestampData tide3 = tideTimestampData.get(2);

                    row3_time.setText(tide3.tideTime);
                    row3_height.setText(tide3.tideValue + "m");

                    if(tide3.tideHL.equals("H")){
                        final TransitionDrawable windTransitionDrawable =
                                new TransitionDrawable(new Drawable[] {
                                        row3_image.getDrawable(),
                                        context.getResources().getDrawable(R.drawable.tide_high)
                                });
                        row3_image.setImageDrawable(windTransitionDrawable);
                        windTransitionDrawable.setCrossFadeEnabled(true);
                        windTransitionDrawable.startTransition(400);
                    }else{
                        final TransitionDrawable windTransitionDrawable =
                                new TransitionDrawable(new Drawable[] {
                                        row3_image.getDrawable(),
                                        context.getResources().getDrawable(R.drawable.tide_low)
                                });
                        row3_image.setImageDrawable(windTransitionDrawable);
                        windTransitionDrawable.setCrossFadeEnabled(true);
                        windTransitionDrawable.startTransition(400);
                    }

                    if(tideTimestampData.size() > 3) {

                        LinearLayout row4 = (LinearLayout) view.findViewById(R.id.row4);
                        row4.setVisibility(View.VISIBLE);

                        ImageView row4_image = (ImageView) view.findViewById(R.id.row4_image);
                        TextSwitcher row4_time = (TextSwitcher) view.findViewById(R.id.row4_time);
                        TextView row4_time_tv = (TextView) row4_time.getCurrentView();
                        if (row4_time_tv == null) {
                            row4_time.setFactory(new ViewSwitcher.ViewFactory() {

                                public View makeView() {
                                    TextView myText = new TextView(context);

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
                        row4_time.setInAnimation(AnimationUtils.loadAnimation(context,
                                R.anim.fade_in));
                        row4_time.setOutAnimation(context, R.anim.fade_out);

                        TextSwitcher row4_height = (TextSwitcher) view.findViewById(R.id.row4_height);
                        TextView row4_height_tv = (TextView) row4_height.getCurrentView();
                        if (row4_height_tv == null) {
                            row4_height.setFactory(new ViewSwitcher.ViewFactory() {

                                public View makeView() {
                                    TextView myText = new TextView(context);

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
                        row4_height.setInAnimation(AnimationUtils.loadAnimation(context,
                                R.anim.fade_in));
                        row4_height.setOutAnimation(context, R.anim.fade_out);

                        TimestampData tide4 = tideTimestampData.get(3);

                        row4_time.setText(tide4.tideTime);
                        row4_height.setText(tide4.tideValue + "m");

                        if(tide4.tideHL.equals("H")){
                            final TransitionDrawable windTransitionDrawable =
                                    new TransitionDrawable(new Drawable[] {
                                            row4_image.getDrawable(),
                                            context.getResources().getDrawable(R.drawable.tide_high)
                                    });
                            row4_image.setImageDrawable(windTransitionDrawable);
                            windTransitionDrawable.setCrossFadeEnabled(true);
                            windTransitionDrawable.startTransition(400);
                        }else{
                            final TransitionDrawable windTransitionDrawable =
                                    new TransitionDrawable(new Drawable[] {
                                            row4_image.getDrawable(),
                                            context.getResources().getDrawable(R.drawable.tide_low)
                                    });
                            row4_image.setImageDrawable(windTransitionDrawable);
                            windTransitionDrawable.setCrossFadeEnabled(true);
                            windTransitionDrawable.startTransition(400);
                        }
                    }else{
                        LinearLayout row4 = (LinearLayout) view.findViewById(R.id.row4);
                        row4.setVisibility(View.GONE);
                    }
                }else{
                    LinearLayout row3 = (LinearLayout) view.findViewById(R.id.row3);
                    row3.setVisibility(View.GONE);
                    LinearLayout row4 = (LinearLayout) view.findViewById(R.id.row4);
                    row4.setVisibility(View.GONE);
                }
            }else{
                LinearLayout row2 = (LinearLayout) view.findViewById(R.id.row2);
                row2.setVisibility(View.GONE);
                LinearLayout row3 = (LinearLayout) view.findViewById(R.id.row3);
                row3.setVisibility(View.GONE);
                LinearLayout row4 = (LinearLayout) view.findViewById(R.id.row4);
                row4.setVisibility(View.GONE);
            }
        }else{
            LinearLayout row1 = (LinearLayout) view.findViewById(R.id.row1);
            row1.setVisibility(View.GONE);
            LinearLayout row2 = (LinearLayout) view.findViewById(R.id.row2);
            row2.setVisibility(View.GONE);
            LinearLayout row3 = (LinearLayout) view.findViewById(R.id.row3);
            row3.setVisibility(View.GONE);
            LinearLayout row4 = (LinearLayout) view.findViewById(R.id.row4);
            row4.setVisibility(View.GONE);
        }
    }

}

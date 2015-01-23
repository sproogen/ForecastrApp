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

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.text.SimpleDateFormat;
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

        ValueLineChart mCubicValueLineChart = (ValueLineChart) view.findViewById(R.id.tideChart);

        ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

        series.addPoint(new ValueLinePoint("00:00", 11f));
        series.addPoint(new ValueLinePoint("02:38", 0.7f));
        series.addPoint(new ValueLinePoint("08:47", 12.6f));
        series.addPoint(new ValueLinePoint("15:02", 0.6f));
        series.addPoint(new ValueLinePoint("21:09", 12.3f));
        series.addPoint(new ValueLinePoint("24:00", 1.6f));

        mCubicValueLineChart.addSeries(series);
        mCubicValueLineChart.startAnimation();

        return view;
    }

    public void updateView(int newDateTab){
        super.updateView(newDateTab);

        View view = ((Activity)context).findViewById(R.id.content_body).findViewWithTag(getTitle());

    }

}

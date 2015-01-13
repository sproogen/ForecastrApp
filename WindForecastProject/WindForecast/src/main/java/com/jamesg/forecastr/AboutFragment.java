package com.jamesg.forecastr;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jamesg.forecastr.base.BaseFragment;
import com.jamesg.forecastr.data.Spot;
import com.jamesg.forecastr.manager.SpotManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

public class AboutFragment extends BaseFragment {

    @Inject
    SpotManager spotManager;

    public AboutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((WindFinderApplication) getActivity().getApplication()).inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.about_layout, container, false);
        getActivity().setTitle("About");

        String versionName = "";

        try {
            versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView versionNameTextView = (TextView) rootView.findViewById(R.id.versionName);
        versionNameTextView.setText(versionName);

        long lastUpdated = 0;
        for (Spot s : spotManager.getAllSpots(0)) {
            if (s.getUpdateTime() > lastUpdated) {
                lastUpdated = s.getUpdateTime();
            }
        }
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date updatedDate = new Date(lastUpdated);

        TextView updatedText = (TextView) rootView.findViewById(R.id.updatedText);
        updatedText.setText(df.format(updatedDate));

        return rootView;
    }
}
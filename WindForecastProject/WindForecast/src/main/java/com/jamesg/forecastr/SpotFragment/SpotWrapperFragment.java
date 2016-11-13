package com.jamesg.forecastr.SpotFragment;



import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.jamesg.forecastr.base.BaseFragment;
import com.jamesg.forecastr.R;
import com.jamesg.forecastr.base.BaseSpotFragment;
import com.jamesg.forecastr.data.Spot;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpotWrapperFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SpotWrapperFragment extends BaseFragment {

    private BaseSpotFragment currentSpotFragment;

    private GoogleMap googleMap;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SpotWrapperFragment.
     */
    public static SpotWrapperFragment newInstance() {
        SpotWrapperFragment fragment = new SpotWrapperFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public SpotWrapperFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {}
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.spots_frame, container, false);

        FavouritesFragment favourites = FavouritesFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.spots_frame, favourites);
        transaction.commit();

        currentSpotFragment = favourites;

        return view;
    }

    public void updateDateTab(int newDateTab){
        if(currentSpotFragment != null){
            currentSpotFragment.updateDateTab(newDateTab);
        }
    }

    public void loadSpot(String name, boolean animate, boolean search){
        SpotFragment spot = SpotFragment.newInstance(name, search);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if(animate) {
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        }
        transaction.replace(R.id.spots_frame, spot);
        transaction.commit();

        currentSpotFragment = spot;
    }

    public void closeSpot(boolean animate){
        FavouritesFragment favourites = FavouritesFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if(animate){
            transaction.setCustomAnimations(R.anim.close_enter, R.anim.close_exit);
        }
        transaction.replace(R.id.spots_frame, favourites);
        transaction.commit();

        currentSpotFragment = favourites;
    }

    public void removeSpot(Spot spot){
        try {
            ((FavouritesFragment) currentSpotFragment).removeSpot(spot);
        }catch(Exception e){
            //Do Nothing
        }
    }

    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener){
        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }
}

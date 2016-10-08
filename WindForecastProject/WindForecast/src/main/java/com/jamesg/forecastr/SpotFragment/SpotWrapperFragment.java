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
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.spots_frame, favourites);
        transaction.commit();

        addMap();

        currentSpotFragment = favourites;

        return view;
    }

    public void addMap() {

        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

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
            fragmentManager.beginTransaction()
                    .add(R.id.map, mapFragment, "mapFragment")
                    .addToBackStack(null)
                    .commit();
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){

            @Override
            public void run() {
                googleMap = ((SupportMapFragment)
                        fragmentManager.findFragmentByTag("mapFragment")).getMap();
                if (googleMap != null) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    float cameraZoom = 2;
                    LatLng spotLatLng = new LatLng(51.4487547, -2.5740142);

//                        try {
//                            spotLatLng = new LatLng(Double.parseDouble(spot.getLatitude()), Double.parseDouble(spot.getLongitude()));
//                            googleMap.clear();
//                            Marker pin = googleMap.addMarker(new MarkerOptions()
//                                    .position(spotLatLng)
//                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
//                            cameraZoom = 11;
//                        }catch(Exception e){
//                            //DO NOTHING
//                        }

                    googleMap.getUiSettings().setCompassEnabled(false);
                    googleMap.getUiSettings().setZoomControlsEnabled(false);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    googleMap.getUiSettings().setAllGesturesEnabled(false);
                    googleMap.getUiSettings().setMapToolbarEnabled(false);

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(spotLatLng, cameraZoom));

                    centreMap();
                }
            }
        },200);
    }

    public void centreMap(){
        // Acquire a reference to the system Location Manager
        final LocationManager locationManager = (LocationManager)  getActivity().getSystemService(Context.LOCATION_SERVICE);

        Log.d("LOCATIONSHIT", String.valueOf(Build.VERSION.SDK_INT));
        Log.d("LOCATIONSHIT", String.valueOf(ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED));

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  }, 1);
        }
        try {

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
                // Do something with the recent location fix
                //  otherwise wait for the update below
                googleMap = ((SupportMapFragment)
                        getActivity().getSupportFragmentManager().findFragmentByTag("mapFragment")).getMap();
                if (googleMap != null){
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10));
                }
            }
            else {
                // Define a listener that responds to location updates
                LocationListener locationListener = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        Log.d("LOCATIONSHIT", "YEAH");
                        // Called when a new location is found by the network location provider.
                        googleMap = ((SupportMapFragment)
                                getActivity().getSupportFragmentManager().findFragmentByTag("mapFragment")).getMap();
                        if (googleMap != null){
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10));
                        }
                        locationManager.removeUpdates(this);
                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {}

                    public void onProviderEnabled(String provider) {}

                    public void onProviderDisabled(String provider) {}
                };

                // Register the listener with the Location Manager to receive location updates
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        } catch (Exception ex)  {
            //Error getting location
        }
    }

    public void updateDateTab(int newDateTab){
        if(currentSpotFragment != null){
            currentSpotFragment.updateDateTab(newDateTab);
        }
    }

    public void loadSpot(String name, boolean animate, boolean search){
        SpotFragment spot = SpotFragment.newInstance(name, search);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        if(animate) {
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        }
        transaction.replace(R.id.spots_frame, spot);
        transaction.commit();

        currentSpotFragment = spot;
    }

    public void closeSpot(boolean animate){
        FavouritesFragment favourites = FavouritesFragment.newInstance();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
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

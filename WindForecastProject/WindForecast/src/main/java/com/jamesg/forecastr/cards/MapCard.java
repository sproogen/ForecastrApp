package com.jamesg.forecastr.cards;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jamesg.forecastr.R;
import com.jamesg.forecastr.base.CardBase;
import com.jamesg.forecastr.data.Spot;

/**
 * Created by James on 17/10/2014.
 */
public class MapCard extends CardBase {

    private GoogleMap googleMap;
    private int mapSpot = -1;
    private int mapType = GoogleMap.MAP_TYPE_NORMAL;
    private FragmentManager fragmentManager;

    public MapCard(Context context, Spot spot, int dateTab, FragmentManager fragmentManager){
        this.context = context;
        this.spot = spot;
        this.dateTab = dateTab;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public String getTitle() {
        return "Map";
    }

    @Override
    public String getTag() {
        return "map";
    }

    public View getView(LayoutInflater inflater){

        View view = inflater.inflate(R.layout.card_layout_map, null);

        SupportMapFragment mapFragment = ((SupportMapFragment)
                fragmentManager.findFragmentByTag("mapFragment"));

        if(mapFragment==null){
            mapFragment = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(R.id.map, mapFragment, "mapFragment")
                    .addToBackStack(null)
                    .commit();
        }else{
            removeMap();
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
                if(googleMap != null) {
                    if(mapSpot == -1){
                        mapSpot = spot.getId();
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                        float cameraZoom = 4;
                        LatLng spotLatLng = new LatLng(51.4487547, -2.5740142);

                        try {
                            spotLatLng = new LatLng(Double.parseDouble(spot.getLatitude()), Double.parseDouble(spot.getLongitude()));
                            googleMap.clear();
                            Marker pin = googleMap.addMarker(new MarkerOptions()
                                    .position(spotLatLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
                            cameraZoom = 11;
                        }catch(Exception e){
                            //DO NOTHING
                        }

                        googleMap.getUiSettings().setCompassEnabled(false);
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        googleMap.getUiSettings().setAllGesturesEnabled(false);
                        googleMap.getUiSettings().setMapToolbarEnabled(true);

                        googleMap.setMapType(mapType);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(spotLatLng, cameraZoom));
                    }
                }else {
                    handler.postDelayed(this, 500);
                }
            }
        },500);

        return view;
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

    public void updateView(int newDateTab) {
        super.updateView(newDateTab);
    }
}

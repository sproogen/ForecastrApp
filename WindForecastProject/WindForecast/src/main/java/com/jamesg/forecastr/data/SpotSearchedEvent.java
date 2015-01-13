package com.jamesg.forecastr.data;

/**
 * Created by James on 13/01/2015.
 */
public class SpotSearchedEvent {
    private Spot spot;
    private String name;

    public SpotSearchedEvent(Spot spot){
        this.spot = spot;
        this.name = spot.getName();
    }

    public String getName(){
        return name;
    }

    public Spot getSpot(){
        return spot;
    }
}

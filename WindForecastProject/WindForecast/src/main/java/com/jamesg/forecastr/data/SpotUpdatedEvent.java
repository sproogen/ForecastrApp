package com.jamesg.forecastr.data;

/**
 * Created by James on 13/01/2015.
 */
public class SpotUpdatedEvent {
    private String name;

    public SpotUpdatedEvent(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}

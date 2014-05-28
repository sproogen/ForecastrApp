package com.jamesg.windforecast;

import com.jamesg.windforecast.data.Spot;

/**
 * Created by James on 17/12/13.
 */
public class CustomCard {

    private String type;
    private String title;
    private String subtitle;
    private boolean isHeader;
    private boolean isClickable = true;
    private Object mTag;

    private Spot spot;

    protected CustomCard() {
    }

    protected CustomCard(String title, String subtitle, boolean isHeader) {
        this.title = title;
        this.subtitle = subtitle;
        this.isHeader = isHeader;
    }

    public CustomCard(String title) {
        this.title = title;
    }

    public CustomCard(String title, String type) {
        this.title = title;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public boolean isClickable() {
        return isClickable;
    }

    public CustomCard setClickable(boolean clickable) {
        isClickable = clickable;
        return this;
    }

    public Object getTag() {
        return mTag;
    }

    /**
     * Sets a tag of any type that can be used to keep track of cards.
     */
    public CustomCard setTag(Object tag) {
        mTag = tag;
        return this;
    }

    public void setSpot(Spot spot) {
        this.spot = spot;
    }

    public Spot getSpot() {
        return this.spot;
    }
}

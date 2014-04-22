package com.jamesg.windforecast;

import android.content.Context;

import com.afollestad.cardsui.Card;

/**
 * Created by James on 17/12/13.
 */
public class CustomCard extends Card {

    private String type;

    protected CustomCard() {
    }

    protected CustomCard(String title, String subtitle, boolean isHeader) {
        super(title,subtitle,isHeader);
    }

    public CustomCard(String title) {
        super(title);
    }

    public CustomCard(String title, String type) {
        super(title);
        this.type = type;
    }

    public CustomCard(Context context, int titleRes) {
        this(context.getString(titleRes));
    }

    public CustomCard(Context context, String title, int contentRes) {
        this(title, context.getString(contentRes));
    }

    public CustomCard(Context context, int titleRes, String content) {
        this(context.getString(titleRes), content);
    }

    public CustomCard(Context context, int titleRes, int contentRes) {
        this(context.getString(titleRes), context.getString(contentRes));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

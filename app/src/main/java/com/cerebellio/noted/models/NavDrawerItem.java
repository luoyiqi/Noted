package com.cerebellio.noted.models;

/**
 * Created by Sam on 11/02/2016.
 */
public class NavDrawerItem {

    private String mTitle;
    private int mIconId;
    private NavDrawerItemType mType;

    public enum NavDrawerItemType {
        PINBOARD,
        ARCHIVE,
        TRASH,
        SETTINGS
    }

    public NavDrawerItem(String title, int iconId, NavDrawerItemType type) {
        mTitle = title;
        mIconId = iconId;
        mType = type;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getIconId() {
        return mIconId;
    }

    public NavDrawerItemType getType() {
        return mType;
    }

    public void setType(NavDrawerItemType type) {
        mType = type;
    }
}


package com.cerebellio.noted.models;

import com.cerebellio.noted.utils.TextFunctions;

/**
 * Represents an item in the navigation drawer
 */
public class NavDrawerItem {

    private static final String LOG_TAG = TextFunctions.makeLogTag(NavDrawerItem.class);

    private int mIconId;
    private boolean mIsDividerNeeded;

    private String mTitle;
    private NavDrawerItemType mType;

    public enum NavDrawerItemType {
        PINBOARD,
        ARCHIVE,
        TRASH,
        WORDCLOUD,
        SETTINGS;

        public static boolean isSelectable(NavDrawerItemType type) {
            return type == PINBOARD || type == ARCHIVE || type == TRASH;
        }
    }

    public NavDrawerItem(String title, int iconId, boolean isDividerNeeded, NavDrawerItemType type) {
        mTitle = title;
        mIconId = iconId;
        mIsDividerNeeded = isDividerNeeded;
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

    public boolean isDividerNeeded() {
        return mIsDividerNeeded;
    }

    public NavDrawerItemType getType() {
        return mType;
    }

    public void setType(NavDrawerItemType type) {
        mType = type;
    }
}


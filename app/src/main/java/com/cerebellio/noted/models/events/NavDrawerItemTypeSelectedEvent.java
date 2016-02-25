package com.cerebellio.noted.models.events;

import com.cerebellio.noted.models.NavDrawerItem;
import com.cerebellio.noted.utils.TextFunctions;

/**
 * Allows a {@link com.cerebellio.noted.models.NavDrawerItem.NavDrawerItemType} to be
 * passed through event bus
 */
public class NavDrawerItemTypeSelectedEvent {

    private static final String LOG_TAG = TextFunctions.makeLogTag(NavDrawerItemTypeSelectedEvent.class);

    private NavDrawerItem.NavDrawerItemType mType;

    public NavDrawerItemTypeSelectedEvent(NavDrawerItem.NavDrawerItemType type) {
        mType = type;
    }

    public NavDrawerItem.NavDrawerItemType getType() {
        return mType;
    }
}

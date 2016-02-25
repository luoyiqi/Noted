package com.cerebellio.noted.models.events;

import com.cerebellio.noted.utils.TextFunctions;

/**
 * Allows tag creation event to be sent through event bus
 */
public class TagEvent {

    private static final String LOG_TAG = TextFunctions.makeLogTag(TagEvent.class);

    private Type mType;
    private String mTag;

    public enum Type {
        ADD,
        EDIT
    }

    public TagEvent(Type type, String tag) {
        mType = type;
        mTag = tag;
    }

    public TagEvent(Type type) {
        mType = type;
    }

    public Type getType() {
        return mType;
    }

    public String getTag() {
        return mTag;
    }
}

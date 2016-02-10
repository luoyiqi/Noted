package com.cerebellio.noted.models;

/**
 * Created by Sam on 10/02/2016.
 */
public abstract class Item {

    protected String mTitle;

    protected long mId;
    protected long mLastModifiedDate;
    protected int mColour = 0xFF4CAF50;
    protected boolean mIsTrashed;
    protected boolean mIsUrgent;


    public String getTitle() {
        return mTitle;
    }

    public abstract boolean isEmpty();

    public void setTitle(String title) {
        mTitle = title;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getLastModifiedDate() {
        return mLastModifiedDate;
    }

    public void setLastModifiedDate(long lastModifiedDate) {
        mLastModifiedDate = lastModifiedDate;
    }

    public int getColour() {
        return mColour;
    }

    public void setColour(int colour) {
        mColour = colour;
    }

    public boolean isTrashed() {
        return mIsTrashed;
    }

    public void setIsTrashed(boolean isTrashed) {
        mIsTrashed = isTrashed;
    }

    public boolean isUrgent() {
        return mIsUrgent;
    }

    public void setIsUrgent(boolean isUrgent) {
        mIsUrgent = isUrgent;
    }
}

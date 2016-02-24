package com.cerebellio.noted.models;

import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.UtilityFunctions;

/**
 * Created by Sam on 10/02/2016.
 */
public abstract class Item {

    protected String mTitle;

    /**
     * Comma seperated list of tags
     */
    protected String mTagString;

    protected long mId;
    protected int mColour = UtilityFunctions.getRandomIntegerFromArray(Constants.COLOURS);
    protected long mCreatedDate;
    protected long mLastModifiedDate;
    protected boolean mIsImportant;

    protected Status mStatus = Status.NONE;

    public enum Type {
        NOTE,
        CHECKLIST,
        CHECKLIST_ITEM,
        SKETCH
    }

    public enum Status {
        NONE,
        TRASHED,
        ARCHIVED,
        DELETED
    }

    public void appendToTagString(String newTag) {
        if (!mTagString.equals("")) {
            mTagString += ",";
        }
        mTagString += newTag;
    }

    public abstract Type getItemType();

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

    public long getCreatedDate() {
        return mCreatedDate;
    }

    public void setCreatedDate(long createdDate) {
        mCreatedDate = createdDate;
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

    public boolean isImportant() {
        return mIsImportant;
    }

    public void setIsImportant(boolean isImportant) {
        mIsImportant = isImportant;
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        mStatus = status;
    }

    public String getTagString() {
        return mTagString;
    }

    public void setTagString(String tagString) {
        mTagString = tagString;
    }
}

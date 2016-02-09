package com.cerebellio.noted.models;

/**
 * Created by Sam on 08/02/2016.
 */
public class Note {

    private long mId;
    private long mLastModifiedDate;
    private String mTitle;
    private String mContent;

    public Note() {
    }

    public boolean isEmpty() {
        return mTitle.equals("") && mContent.equals("");
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

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }
}

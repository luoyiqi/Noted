package com.cerebellio.noted.models;

import com.cerebellio.noted.utils.TextFunctions;

/**
 * Represents a text note
 */
public class Note extends Item {

    private static final String LOG_TAG = TextFunctions.makeLogTag(Note.class);

    private String mContent;

    public Note() {
    }

    @Override
    public boolean isEmpty() {
        //Empty if only contains spaces or nothing
        return mContent.trim().equals("");
    }

    @Override
    public Type getItemType() {
        return Type.NOTE;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }


}

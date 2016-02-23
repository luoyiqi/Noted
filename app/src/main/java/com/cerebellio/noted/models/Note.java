package com.cerebellio.noted.models;

/**
 * Created by Sam on 08/02/2016.
 */
public class Note extends Item {

    private String mContent;


    public Note() {
    }

    @Override
    public boolean isEmpty() {
        return mTitle.equals("") && mContent.equals("");
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }


}

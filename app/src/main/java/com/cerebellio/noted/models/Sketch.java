package com.cerebellio.noted.models;

import com.cerebellio.noted.utils.TextFunctions;

/**
 * Represents a sketch a user can draw
 */
public class Sketch extends Item {

    private static final String LOG_TAG = TextFunctions.makeLogTag(Sketch.class);

    private String mImagePath;

    public Sketch() {}

    @Override
    public boolean isEmpty() {
        //Empty if has not been saved to file
        return mImagePath.equals("");
    }

    @Override
    public Type getItemType() {
        return Type.SKETCH;
    }

    @Override
    public String getText() {
        return getFormattedTagString();
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }
}

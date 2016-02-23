package com.cerebellio.noted.models;

/**
 * Represents a sketch a user can draw
 */
public class Sketch extends Item {

    private String mImagePath;

    public Sketch() {}

    @Override
    public boolean isEmpty() {
        return mImagePath.equals("");
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }
}

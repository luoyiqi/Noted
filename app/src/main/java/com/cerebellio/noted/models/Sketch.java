package com.cerebellio.noted.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Sam on 11/02/2016.
 */
public class Sketch extends Item {

    private byte[] mBitmapAsByteArray;

    public Sketch() {}

    @Override
    public boolean isEmpty() {
        Bitmap currentBitmap = getBitmap();

        return currentBitmap == null || currentBitmap.sameAs(Bitmap.createBitmap(currentBitmap.getWidth(),
                currentBitmap.getHeight(),
                currentBitmap.getConfig()));
    }

    public byte[] getBitmapAsByteArray() {
        return mBitmapAsByteArray;
    }

    public void setBitmapAsByteArray(byte[] bitmapAsByteArray) {
        mBitmapAsByteArray = bitmapAsByteArray;
    }

    public Bitmap getBitmap() {
        return BitmapFactory.decodeByteArray(mBitmapAsByteArray, 0, mBitmapAsByteArray.length);
    }
}

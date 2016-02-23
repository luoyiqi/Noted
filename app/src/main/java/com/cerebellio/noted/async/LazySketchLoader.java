package com.cerebellio.noted.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.cerebellio.noted.models.Sketch;
import com.cerebellio.noted.utils.UtilityFunctions;

/**
 * Loads a {@link Sketch} bitmap off the UI thread and inserts into the ImageView once loaded
 */
public class LazySketchLoader extends AsyncTask<Object, Void, Bitmap> {

    private Context mContext;
    private ImageView mTarget;
    private Sketch mSketch;

    public LazySketchLoader(Context context, ImageView targetImage, Sketch sketch) {
        mContext = context;
        mTarget = targetImage;
        mSketch = sketch;
        mTarget.setImageBitmap(null);
    }

    @Override
    protected Bitmap doInBackground(Object... objects) {
        return UtilityFunctions.getBitmapFromFile(mSketch.getImagePath());
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        final int FADE_IN_TIME = 500;

        if (bitmap != null) {

            //fade in our image over time as it is loaded
            final TransitionDrawable td =
                    new TransitionDrawable(new Drawable[] {
                            new ColorDrawable(Color.TRANSPARENT),
                            new BitmapDrawable(mContext.getResources(),
                                    bitmap)
                    });

            mTarget.setImageDrawable(td);
            td.startTransition(FADE_IN_TIME);
        }
    }
}

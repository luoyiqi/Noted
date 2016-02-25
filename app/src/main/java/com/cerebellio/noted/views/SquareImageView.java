package com.cerebellio.noted.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.cerebellio.noted.utils.TextFunctions;

/**
 * Ensures the View is only as tall as it is wide
 */
public class SquareImageView extends ImageView {

    private static final String LOG_TAG = TextFunctions.makeLogTag(SquareImageView.class);

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //Set height and width to the same value
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}

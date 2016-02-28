package com.cerebellio.noted.views;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.cerebellio.noted.R;
import com.cerebellio.noted.utils.TextFunctions;
import com.cerebellio.noted.utils.UtilityFunctions;

/**
 * Filters icon colour depending on current theme
 */
public class FilteredIconView extends ImageView {

    private static final String LOG_TAG = TextFunctions.makeLogTag(FilteredIconView.class);

    private int mDefaultColour;
    private int mCurrentColour = mDefaultColour;
    private final boolean mIsDark;

    public FilteredIconView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDefaultColour = ContextCompat.getColor(context,
                UtilityFunctions.getResIdFromAttribute(R.attr.colorFilter, context));

        //Is this considered a dark theme?
        mIsDark = getResources().getBoolean(
                UtilityFunctions.getResIdFromAttribute(R.attr.is_dark_theme, context));

        setFilterToDefault();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);

        //Need to reapply filter when image resource changed
        setFilter(mCurrentColour);
    }

    /**
     * Set the colour of the filter applied to the View
     *
     * @param colour        new colour to apply
     */
    public void setFilter(int colour) {
        mCurrentColour = colour;

        if (getDrawable() != null) {
            getDrawable().setColorFilter(colour, PorterDuff.Mode.SRC_ATOP);

            if (colour == mDefaultColour) {

                //not selected

                //Material design guidelines,
                //Dark theme: icon opacity should be 100%
                //Light theme: icon opacity should be 54%
                getDrawable().setAlpha(mIsDark ? 255 : 138);
            } else {

                //selected
                getDrawable().setAlpha(255);
            }
        }
    }

    /**
     * Return colour of filter to default state
     */
    public void setFilterToDefault() {
        setFilter(mDefaultColour);
    }
}

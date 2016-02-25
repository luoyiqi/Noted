package com.cerebellio.noted.utils;

import android.graphics.Color;

/**
 * Commonly used colour functions
 */
public abstract class ColourFunctions {

    private static final String LOG_TAG = TextFunctions.makeLogTag(ColourFunctions.class);

    /**
     * Adjusts the alpha value of the given colour
     * @param colour        colour to adjust
     * @param alpha         new alpha value 0-255
     * @return              adjusted colour
     */
    public static int adjustAlpha(int colour, int alpha) {

        final int MIN_ALPHA = 0;
        final int MAX_ALPHA = 255;

        //If alpha outside allowed range, just return given colour with no adjustment
        if (alpha < MIN_ALPHA || alpha > MAX_ALPHA) {
            return colour;
        }

        return Color.argb(alpha,
                Color.red(colour),
                Color.green(colour),
                Color.blue(colour));
    }


}

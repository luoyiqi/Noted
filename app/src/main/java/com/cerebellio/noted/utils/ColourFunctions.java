package com.cerebellio.noted.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

/**
 * Commonly used colour functions
 */
public class ColourFunctions {

    private ColourFunctions() {}

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

    /**
     * Retrieve a colour attribute from resources
     *
     * @param context
     * @param attr          attribute ID
     * @return              colour
     */
    public static int getColourFromAttr(Context context, int attr) {
        return ContextCompat.getColor(context, UtilityFunctions.getResIdFromAttribute(attr, context));
    }

    /**
     *
     * @return              random Material colour
     */
    public static int getRandomMaterialColour() {
        return UtilityFunctions.getRandomIntegerFromArray(Constants.MATERIAL_COLOURS);
    }
}

package com.cerebellio.noted.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.cerebellio.noted.ApplicationNoted;
import com.cerebellio.noted.R;

/**
 * Commonly used colour functions
 */
public class ColourFunctions {

    private ColourFunctions() {}

    private static final String LOG_TAG = TextFunctions.makeLogTag(ColourFunctions.class);

    public static final int MATERIAL_ALPHA_54_PER_CENT = 138;

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
     * Get a truly random colour
     *
     * @return          colour
     */
    public static int getRandomColour() {
        return Color.argb(255,
                ApplicationNoted.random.nextInt(256),
                ApplicationNoted.random.nextInt(256),
                ApplicationNoted.random.nextInt(256));
    }

    /**
     * Get a random colour mixed with a given colour.
     *
     * @param mixer         colour to mix in
     * @return              random colour + mixer
     */
    public static int getRandomColour(int mixer) {
        return Color.argb(255,
                (ApplicationNoted.random.nextInt(256) + Color.red(mixer)) / 2,
                (ApplicationNoted.random.nextInt(256) + Color.green(mixer)) / 2,
                (ApplicationNoted.random.nextInt(256) + Color.blue(mixer)) / 2);
    }

    /**
     * Generate random pastel colour
     *
     * @return          random pastel colour
     */
    public static int getRandomPastelColour() {
        //Mix truly random colour with a light grey as mixer
        return getRandomColour(0x00D3D3D3);
    }

    /**
     *
     * @return              random Material colour
     */
    public static int getRandomMaterialColour() {
        return UtilityFunctions.getRandomIntegerFromArray(Constants.MATERIAL_COLOURS);
    }

    /**
     *
     * @param context       calling Context
     * @return              accent colour for current theme
     */
    public static int getAccentColour(Context context) {
        return ContextCompat.getColor(
                context, UtilityFunctions.getResIdFromAttribute(R.attr.colorAccent, context));
    }

    /**
     *
     * @param context       calling Context
     * @return              tertiary text colour for current theme
     */
    public static int getTertiaryTextColour(Context context) {
        return ContextCompat.getColor(
                context, UtilityFunctions.getResIdFromAttribute(R.attr.textColorTertiary, context));
    }

    /**
     *
     * @param context       calling Context
     * @return              primary text colour for current theme
     */
    public static int getPrimaryTextColour(Context context) {
        return ContextCompat.getColor(
                context, UtilityFunctions.getResIdFromAttribute(R.attr.textColorPrimary, context));
    }

    /**
     *
     * @param context       calling Context
     * @return              background colour for current theme
     */
    public static int getBackgroundColour(Context context) {
        return ContextCompat.getColor(
                context, UtilityFunctions.getResIdFromAttribute(R.attr.windowBackground, context));
    }
}

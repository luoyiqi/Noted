package com.cerebellio.noted.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import com.cerebellio.noted.helpers.WordCloudBuilder;
import com.cerebellio.noted.models.WordCloud;

/**
 * Commonly used SharedPreference functions
 */
public class PreferenceFunctions {

    public static final String SETTINGS_DISPLAY_THEME = "settings_display_theme";
    public static final String SETTINGS_DISPLAY_THEME_LIGHT = "settings_display_theme_light";

    public static final String SETTINGS_DISPLAY_COLUMNS = "settings_display_columns";
    public static final String SETTINGS_DISPLAY_COLUMNS_1 = "settings_display_columns_1";
    public static final String SETTINGS_DISPLAY_COLUMNS_2 = "settings_display_columns_2";
    public static final String SETTINGS_DISPLAY_COLUMNS_3 = "settings_display_columns_3";
    public static final String SETTINGS_DISPLAY_COLUMNS_4 = "settings_display_columns_4";
    public static final String SETTINGS_DISPLAY_COLUMNS_5 = "settings_display_columns_5";

    public static final String SETTINGS_DISPLAY_DATE_FORMAT = "settings_display_date_format";
    public static final String SETTINGS_DISPLAY_DATE_FORMAT_PRETTY = "settings_display_date_format_pretty";
    public static final String SETTINGS_DISPLAY_DATE_FORMAT_LONG = "settings_display_date_format_long";

    public static final String SETTINGS_WORDCLOUD_COLOUR = "settings_wordcloud_colour";
    public static final String SETTINGS_WORDCLOUD_COLOUR_MATERIAL = "settings_wordcloud_colour_material";
    public static final String SETTINGS_WORDCLOUD_COLOUR_CURRENT = "settings_wordcloud_colour_current";
    public static final String SETTINGS_WORDCLOUD_COLOUR_MONOTONE = "settings_wordcloud_colour_monotone";

    public static final String SETTINGS_WORDCLOUD_SHAPE = "settings_wordcloud_shape";
    public static final String SETTINGS_WORDCLOUD_SHAPE_CIRCLE = "settings_wordcloud_shape_circle";
    public static final String SETTINGS_WORDCLOUD_SHAPE_HORIZONTAL_LINE = "settings_wordcloud_shape_horizontal_line";
    public static final String SETTINGS_WORDCLOUD_SHAPE_VERTICAL_LINE = "settings_wordcloud_shape_vertical_line";

    public static final String SETTINGS_WORDCLOUD_ANIMATION = "settings_wordcloud_animation";

    public static final String SETTINGS_WORDCLOUD_INCLUDE_COMMON_WORDS = "settings_wordcloud_include_common_words";

    public static final String SETTINGS_WORDCLOUD_DENSITY = "settings_wordcloud_density";
    public static final String SETTINGS_WORDCLOUD_DENSITY_DENSE = "settings_wordcloud_density_dense";
    public static final String SETTINGS_WORDCLOUD_DENSITY_NORMAL = "settings_wordcloud_density_normal";
    public static final String SETTINGS_WORDCLOUD_DENSITY_LOOSE = "settings_wordcloud_density_loose";

    public static final String SETTINGS_WORDCLOUD_NUMBER = "settings_wordcloud_number";
    public static final String SETTINGS_WORDCLOUD_NUMBER_10 = "settings_wordcloud_number_10";
    public static final String SETTINGS_WORDCLOUD_NUMBER_50 = "settings_wordcloud_number_50";
    public static final String SETTINGS_WORDCLOUD_NUMBER_100 = "settings_wordcloud_number_100";
    public static final String SETTINGS_WORDCLOUD_NUMBER_500 = "settings_wordcloud_number_500";
    public static final String SETTINGS_WORDCLOUD_NUMBER_1000 = "settings_wordcloud_number_1000";

    public static final String SETTINGS_FEEDBACK_VIBRATION = "settings_feedback_vibration";

    private PreferenceFunctions(){}

    /**
     * Gets the number of pinboard columns as set in SharedPreferences
     *
     * @param context       calling Context
     * @return              number of columns
     */
    public static int getPrefPinboardColumns(Context context) {
        switch (getPrefPinboardColumnsValue(context)) {
            case SETTINGS_DISPLAY_COLUMNS_1:
                return 1;
            case SETTINGS_DISPLAY_COLUMNS_2:
                return 2;
            case SETTINGS_DISPLAY_COLUMNS_3:
                return 3;
            default:
            case SETTINGS_DISPLAY_COLUMNS_4:
                return 4;
            case SETTINGS_DISPLAY_COLUMNS_5:
                return 5;
        }
    }

    /**
     * Get the value stored in the SharedPreferences
     *
     * @param context       calling Context
     * @return              value retrieved
     */
    public static String getPrefPinboardColumnsValue(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SETTINGS_DISPLAY_COLUMNS, SETTINGS_DISPLAY_COLUMNS_4);
    }

    /**
     * Get the theme stored in the SharedPreferences
     *
     * @param context       calling Context
     * @return              value retrieved
     */
    public static String getPrefThemeValue(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SETTINGS_DISPLAY_THEME, SETTINGS_DISPLAY_THEME_LIGHT);
    }

    /**
     * Gets the date format to use
     *
     * @param context       calling Context
     * @return              date format type
     */
    public static String getPrefDateFormatValue(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SETTINGS_DISPLAY_DATE_FORMAT, SETTINGS_DISPLAY_DATE_FORMAT_PRETTY);
    }

    /**
     *
     * Gets the word cloud colour to use
     *
     * @param context       calling Context
     * @return              colour value
     */
    public static String getPrefWordCloudColourValue(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SETTINGS_WORDCLOUD_COLOUR, SETTINGS_WORDCLOUD_COLOUR_MATERIAL);
    }

    /**
     * Gets the currently selected
     * {@link WordCloud.CloudShape}
     *
     * @param context
     * @return                  saved shape
     */
    public static WordCloud.CloudShape getPrefWordCloudShape(Context context) {
        switch (PreferenceFunctions.getPrefWordCloudShapeValue(context)) {
            default:
            case PreferenceFunctions.SETTINGS_WORDCLOUD_SHAPE_CIRCLE:
                return WordCloud.CloudShape.CIRCULAR;
            case PreferenceFunctions.SETTINGS_WORDCLOUD_SHAPE_HORIZONTAL_LINE:
                return WordCloud.CloudShape.HORIZONTAL;
            case PreferenceFunctions.SETTINGS_WORDCLOUD_SHAPE_VERTICAL_LINE:
                return WordCloud.CloudShape.VERTICAL;
        }
    }

    /**
     *
     * Gets the word cloud shape to use
     *
     * @param context       calling Context
     * @return              shape value
     */
    public static String getPrefWordCloudShapeValue(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SETTINGS_WORDCLOUD_SHAPE, SETTINGS_WORDCLOUD_SHAPE_CIRCLE);
    }

    /**
     * Gets the currently selected
     * {@link com.cerebellio.noted.helpers.WordCloudBuilder.CloudColouringSystem}
     *
     * @param context
     * @return                  saved system
     */
    public static WordCloudBuilder.CloudColouringSystem getPrefWordCloudColour(Context context) {
        switch (PreferenceFunctions.getPrefWordCloudColourValue(context)) {
            default:
            case PreferenceFunctions.SETTINGS_WORDCLOUD_COLOUR_MATERIAL:
                return WordCloudBuilder.CloudColouringSystem.MATERIAL;
            case PreferenceFunctions.SETTINGS_WORDCLOUD_COLOUR_CURRENT:
                return WordCloudBuilder.CloudColouringSystem.CUSTOM_PALETTE;
            case PreferenceFunctions.SETTINGS_WORDCLOUD_COLOUR_MONOTONE:
                return WordCloudBuilder.CloudColouringSystem.MONOTONE;
        }
    }

    /**
     *
     * Gets the word cloud density to use
     *
     * @param context       calling Context
     * @return              density value
     */
    public static String getPrefWordCloudDensityValue(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SETTINGS_WORDCLOUD_DENSITY, SETTINGS_WORDCLOUD_DENSITY_DENSE);
    }

    /**
     * Gets the currently selected
     * {@link com.cerebellio.noted.helpers.WordCloudBuilder.CloudDensity}
     *
     * @param context
     * @return                  saved density
     */
    public static WordCloudBuilder.CloudDensity getPrefWordCloudDensity(Context context) {
        switch (PreferenceFunctions.getPrefWordCloudDensityValue(context)) {
            default:
            case SETTINGS_WORDCLOUD_DENSITY_DENSE:
                return WordCloudBuilder.CloudDensity.DENSE;
            case SETTINGS_WORDCLOUD_DENSITY_NORMAL:
                return WordCloudBuilder.CloudDensity.NORMAL;
            case SETTINGS_WORDCLOUD_DENSITY_LOOSE:
                return WordCloudBuilder.CloudDensity.LOOSE;
        }
    }

    /**
     *
     * Gets the word cloud number to use
     *
     * @param context       calling Context
     * @return              density value
     */
    public static String getPrefWordCloudNumberValue(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SETTINGS_WORDCLOUD_NUMBER, SETTINGS_WORDCLOUD_NUMBER_100);
    }

    /**
     * Gets the currently selected word cloud number
     *
     * @param context
     * @return                  saved number
     */
    public static int getPrefWordCloudNumber(Context context) {
        switch (PreferenceFunctions.getPrefWordCloudNumberValue(context)) {
            case SETTINGS_WORDCLOUD_NUMBER_10:
                return 10;
            case SETTINGS_WORDCLOUD_NUMBER_50:
                return 50;
            default:
            case SETTINGS_WORDCLOUD_NUMBER_100:
                return 100;
            case SETTINGS_WORDCLOUD_NUMBER_500:
                return 500;
            case SETTINGS_WORDCLOUD_NUMBER_1000:
                return 1000;
        }
    }

    /**
     * Get the value stored in the SharedPreferences
     *
     * @param context       calling Context
     * @return              value retrieved
     */
    public static boolean getPrefWordCloudAnimation(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTINGS_WORDCLOUD_ANIMATION, false);
    }

    /**
     * Get the value stored in the SharedPreferences
     *
     * @param context       calling Context
     * @return              value retrieved
     */
    public static boolean getPrefWordCloudIncludeCommonWords(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTINGS_WORDCLOUD_INCLUDE_COMMON_WORDS, false);
    }

    /**
     * Get the value stored in the SharedPreferences
     *
     * @param context       calling Context
     * @return              value retrieved
     */
    public static boolean getPrefVibration(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTINGS_FEEDBACK_VIBRATION, false);
    }

}

package com.cerebellio.noted.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Commonly used SharedPreference functions
 */
public abstract class PreferenceFunctions {

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

    public static final String SETTINGS_FEEDBACK_VIBRATION = "settings_feedback_vibration";

    /**
     * Gets the number of pinboard columns as set in SharedPreferences
     *
     * @param context       calling Context
     * @return              number of columns
     */
    public static int getPrefNumPinboardColumns(Context context) {
        String columns =
                PreferenceManager.getDefaultSharedPreferences(context)
                        .getString(SETTINGS_DISPLAY_COLUMNS,
                                SETTINGS_DISPLAY_COLUMNS_4);
        switch (columns) {
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
    public static String getPrefPinboardColumns(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SETTINGS_DISPLAY_COLUMNS, SETTINGS_DISPLAY_COLUMNS_4);
    }

    /**
     * Get the theme stored in the SharedPreferences
     *
     * @param context       calling Context
     * @return              value retrieved
     */
    public static String getPrefTheme(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SETTINGS_DISPLAY_THEME, SETTINGS_DISPLAY_THEME_LIGHT);
    }

    /**
     * Gets the date format to use
     *
     * @param context       calling Context
     * @return              date format type
     */
    public static String getPrefDateFormat(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SETTINGS_DISPLAY_DATE_FORMAT, SETTINGS_DISPLAY_DATE_FORMAT_PRETTY);
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

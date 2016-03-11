package com.cerebellio.noted.helpers;

import android.content.Context;
import android.preference.PreferenceManager;

import com.cerebellio.noted.R;
import com.cerebellio.noted.models.WordCloud;

/**
 * Commonly used SharedPreference functions
 */
public class PreferenceHelper {

    public static final String SETTINGS_DISPLAY_THEME = "settings_display_theme";
    public static final String SETTINGS_DISPLAY_THEME_LIGHT = "settings_display_theme_light";
    public static final String SETTINGS_DISPLAY_THEME_DARK = "settings_display_theme_dark";
    public static final String SETTINGS_DISPLAY_THEME_CLASSIC = "settings_display_theme_classic";
    public static final String SETTINGS_DISPLAY_THEME_CHROME = "settings_display_theme_chrome";
    public static final String SETTINGS_DISPLAY_THEME_NATURE = "settings_display_theme_nature";

    public static final String SETTINGS_DISPLAY_COLUMNS = "settings_display_columns";
    public static final String SETTINGS_DISPLAY_COLUMNS_1 = "settings_display_columns_1";
    public static final String SETTINGS_DISPLAY_COLUMNS_2 = "settings_display_columns_2";
    public static final String SETTINGS_DISPLAY_COLUMNS_3 = "settings_display_columns_3";
    public static final String SETTINGS_DISPLAY_COLUMNS_4 = "settings_display_columns_4";
    public static final String SETTINGS_DISPLAY_COLUMNS_5 = "settings_display_columns_5";

    public static final String SETTINGS_DISPLAY_TYPE_COUNTS = "settings_display_type_counts";

    public static final String SETTINGS_DISPLAY_DATE_FORMAT = "settings_display_date_format";
    public static final String SETTINGS_DISPLAY_DATE_FORMAT_PRETTY = "settings_display_date_format_pretty";
    public static final String SETTINGS_DISPLAY_DATE_FORMAT_LONG = "settings_display_date_format_long";

    public static final String SETTINGS_DISPLAY_TRUNCATE_ITEM = "settings_display_truncate_item";
    public static final String SETTINGS_DISPLAY_TRUNCATE_ITEM_NONE = "settings_display_truncate_item_none";
    public static final String SETTINGS_DISPLAY_TRUNCATE_ITEM_SHORT = "settings_display_truncate_item_short";
    public static final String SETTINGS_DISPLAY_TRUNCATE_ITEM_MEDIUM = "settings_display_truncate_item_medium";
    public static final String SETTINGS_DISPLAY_TRUNCATE_ITEM_LONG = "settings_display_truncate_item_long";

    public static final String SETTINGS_WORDCLOUD_COLOUR = "settings_wordcloud_colour";
    public static final String SETTINGS_WORDCLOUD_COLOUR_MATERIAL = "settings_wordcloud_colour_material";
    public static final String SETTINGS_WORDCLOUD_COLOUR_CURRENT = "settings_wordcloud_colour_current";
    public static final String SETTINGS_WORDCLOUD_COLOUR_MONOTONE = "settings_wordcloud_colour_monotone";
    public static final String SETTINGS_WORDCLOUD_COLOUR_RANDOM = "settings_wordcloud_colour_random";
    public static final String SETTINGS_WORDCLOUD_COLOUR_PASTEL = "settings_wordcloud_colour_pastel";

    public static final String SETTINGS_WORDCLOUD_SHAPE = "settings_wordcloud_shape";
    public static final String SETTINGS_WORDCLOUD_SHAPE_CIRCLE = "settings_wordcloud_shape_circle";
    public static final String SETTINGS_WORDCLOUD_SHAPE_HORIZONTAL_LINE = "settings_wordcloud_shape_horizontal_line";
    public static final String SETTINGS_WORDCLOUD_SHAPE_VERTICAL_LINE = "settings_wordcloud_shape_vertical_line";
    public static final String SETTINGS_WORDCLOUD_SHAPE_CROSS = "settings_wordcloud_shape_cross";
    public static final String SETTINGS_WORDCLOUD_SHAPE_DIAMOND = "settings_wordcloud_shape_diamond";
    public static final String SETTINGS_WORDCLOUD_SHAPE_PICTURE_FRAME = "settings_wordcloud_shape_picture_frame";
    public static final String SETTINGS_WORDCLOUD_SHAPE_WAR_FORMATION = "settings_wordcloud_shape_war_formation";

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

    public static final String SETTINGS_BEHAVIOUR_NOTIFY_STATUS_CHANGE = "settings_behaviour_notify_status_change";
    public static final String SETTINGS_BEHAVIOUR_NOTIFICATION_VIBRATION = "settings_behaviour_notification_vibration";
    public static final String SETTINGS_BEHAVIOUR_NOTIFICATION_SOUND = "settings_behaviour_notification_sound";
    public static final String SETTINGS_BEHAVIOUR_CONFIRM_DELETE = "settings_behaviour_confirm_delete";
    public static final String SETTINGS_BEHAVIOUR_CHECKLIST_ITALICISE_CHECKED = "settings_behaviour_checklist_italicise_checked";
    public static final String SETTINGS_BEHAVIOUR_CHECKLIST_STRIKETHROUGH_CHECKED = "settings_behaviour_checklist_strikethrough_checked";
    public static final String SETTINGS_BEHAVIOUR_CHECKLIST_DELETE_CHECKED = "settings_behaviour_checklist_delete_checked";
    public static final String SETTINGS_BEHAVIOUR_CHECKLIST_SWIPE_TO_DELETE = "settings_behaviour_checklist_swipe_to_delete";

    public static final String SETTINGS_FEEDBACK_VIBRATION = "settings_feedback_vibration";

    private PreferenceHelper(){}

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

    public static int getPrefTheme(Context context) {
        switch (getPrefThemeValue(context)) {
            default:
            case SETTINGS_DISPLAY_THEME_LIGHT:
                return R.style.NotedTheme_Light;
            case SETTINGS_DISPLAY_THEME_DARK:
                return R.style.NotedTheme_Dark;
            case SETTINGS_DISPLAY_THEME_CLASSIC:
                return R.style.NotedTheme_Classic;
            case SETTINGS_DISPLAY_THEME_CHROME:
                return R.style.NotedTheme_Chrome;
            case SETTINGS_DISPLAY_THEME_NATURE:
                return R.style.NotedTheme_Nature;
        }
    }

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
            default:
            case SETTINGS_DISPLAY_COLUMNS_3:
                return 3;
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
                .getString(SETTINGS_DISPLAY_COLUMNS, SETTINGS_DISPLAY_COLUMNS_3);
    }

    /**
     * Get the value stored in the SharedPreferences
     *
     * @param context       calling Context
     * @return              value retrieved
     */
    public static boolean getPrefDisplayTypeCounts(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTINGS_DISPLAY_TYPE_COUNTS, false);
    }

    /**
     * Gets the item truncation length as set in SharedPreferences
     *
     * @param context       calling Context
     * @return              number of columns
     */
    public static int getPrefTruncateItem(Context context) {
        switch (getPrefTruncateItemValue(context)) {
            case SETTINGS_DISPLAY_TRUNCATE_ITEM_NONE:
                return Integer.MAX_VALUE;
            case SETTINGS_DISPLAY_TRUNCATE_ITEM_SHORT:
                return (int) context.getResources().getDimension(R.dimen.truncate_item_length_short);
            case SETTINGS_DISPLAY_TRUNCATE_ITEM_MEDIUM:
                return (int) context.getResources().getDimension(R.dimen.truncate_item_length_medium);
            default:
            case SETTINGS_DISPLAY_TRUNCATE_ITEM_LONG:
                return (int) context.getResources().getDimension(R.dimen.truncate_item_length_long);
        }
    }

    /**
     * Get the value stored in the SharedPreferences
     *
     * @param context       calling Context
     * @return              value retrieved
     */
    public static String getPrefTruncateItemValue(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SETTINGS_DISPLAY_TRUNCATE_ITEM, SETTINGS_DISPLAY_TRUNCATE_ITEM_NONE);
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
        switch (PreferenceHelper.getPrefWordCloudShapeValue(context)) {
            default:
            case PreferenceHelper.SETTINGS_WORDCLOUD_SHAPE_CIRCLE:
                return WordCloud.CloudShape.CIRCLE;
            case PreferenceHelper.SETTINGS_WORDCLOUD_SHAPE_HORIZONTAL_LINE:
                return WordCloud.CloudShape.HORIZONTAL;
            case PreferenceHelper.SETTINGS_WORDCLOUD_SHAPE_VERTICAL_LINE:
                return WordCloud.CloudShape.VERTICAL;
            case PreferenceHelper.SETTINGS_WORDCLOUD_SHAPE_CROSS:
                return WordCloud.CloudShape.CROSS;
            case PreferenceHelper.SETTINGS_WORDCLOUD_SHAPE_DIAMOND:
                return WordCloud.CloudShape.DIAMOND;
            case PreferenceHelper.SETTINGS_WORDCLOUD_SHAPE_PICTURE_FRAME:
                return WordCloud.CloudShape.PICTURE_FRAME;
            case PreferenceHelper.SETTINGS_WORDCLOUD_SHAPE_WAR_FORMATION:
                return WordCloud.CloudShape.WAR_FORMATION;
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
        switch (PreferenceHelper.getPrefWordCloudColourValue(context)) {
            default:
            case PreferenceHelper.SETTINGS_WORDCLOUD_COLOUR_MATERIAL:
                return WordCloudBuilder.CloudColouringSystem.MATERIAL;
            case PreferenceHelper.SETTINGS_WORDCLOUD_COLOUR_CURRENT:
                return WordCloudBuilder.CloudColouringSystem.CUSTOM_PALETTE;
            case PreferenceHelper.SETTINGS_WORDCLOUD_COLOUR_MONOTONE:
                return WordCloudBuilder.CloudColouringSystem.MONOTONE;
            case PreferenceHelper.SETTINGS_WORDCLOUD_COLOUR_PASTEL:
                return WordCloudBuilder.CloudColouringSystem.PASTEL;
            case PreferenceHelper.SETTINGS_WORDCLOUD_COLOUR_RANDOM:
                return WordCloudBuilder.CloudColouringSystem.RANDOM;
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
        switch (PreferenceHelper.getPrefWordCloudDensityValue(context)) {
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
                .getString(SETTINGS_WORDCLOUD_NUMBER, SETTINGS_WORDCLOUD_NUMBER_500);
    }

    /**
     * Gets the currently selected word cloud number
     *
     * @param context
     * @return                  saved number
     */
    public static int getPrefWordCloudNumber(Context context) {
        switch (PreferenceHelper.getPrefWordCloudNumberValue(context)) {
            case SETTINGS_WORDCLOUD_NUMBER_10:
                return 10;
            case SETTINGS_WORDCLOUD_NUMBER_50:
                return 50;
            case SETTINGS_WORDCLOUD_NUMBER_100:
                return 100;
            default:
            case SETTINGS_WORDCLOUD_NUMBER_500:
                return 500;
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
                .getBoolean(SETTINGS_WORDCLOUD_ANIMATION, true);
    }

    /**
     * Get the value stored in the SharedPreferences
     *
     * @param context       calling Context
     * @return              value retrieved
     */
    public static boolean getPrefWordCloudIncludeCommonWords(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTINGS_WORDCLOUD_INCLUDE_COMMON_WORDS, true);
    }

    /**
     * Get the value stored in the SharedPreferences
     *
     * @param context       calling Context
     * @return              value retrieved
     */
    public static boolean getPrefBehaviourNotifyStatusChange(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTINGS_BEHAVIOUR_NOTIFY_STATUS_CHANGE, true);
    }

    /**
     * Get the value stored in the SharedPreferences
     *
     * @param context       calling Context
     * @return              value retrieved
     */
    public static boolean getPrefBehaviourNotificationSound(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTINGS_BEHAVIOUR_NOTIFICATION_SOUND, true);
    }

    /**
     * Get the value stored in the SharedPreferences
     *
     * @param context       calling Context
     * @return              value retrieved
     */
    public static boolean getPrefBehaviourNotificationVibration(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTINGS_BEHAVIOUR_NOTIFICATION_VIBRATION, true);
    }

    /**
     * Get the value stored in the SharedPreferences
     *
     * @param context       calling Context
     * @return              value retrieved
     */
    public static boolean getPrefBehaviourConfirmDelete(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTINGS_BEHAVIOUR_CONFIRM_DELETE, true);
    }

    /**
     * Get the value stored in the SharedPreferences
     *
     * @param context       calling Context
     * @return              value retrieved
     */
    public static boolean getPrefBehaviourItaliciseChecked(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTINGS_BEHAVIOUR_CHECKLIST_ITALICISE_CHECKED, true);
    }

    /**
     * Get the value stored in the SharedPreferences
     *
     * @param context       calling Context
     * @return              value retrieved
     */
    public static boolean getPrefBehaviourStrikethroughChecked(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTINGS_BEHAVIOUR_CHECKLIST_STRIKETHROUGH_CHECKED, true);
    }

    /**
     * Get the value stored in the SharedPreferences
     *
     * @param context       calling Context
     * @return              value retrieved
     */
    public static boolean getPrefBehaviourDeleteChecked(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTINGS_BEHAVIOUR_CHECKLIST_DELETE_CHECKED, false);
    }

    /**
     * Get the value stored in the SharedPreferences
     *
     * @param context       calling Context
     * @return              value retrieved
     */
    public static boolean getPrefBehaviourSwipeToDelete(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SETTINGS_BEHAVIOUR_CHECKLIST_SWIPE_TO_DELETE, true);
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

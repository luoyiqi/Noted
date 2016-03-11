package com.cerebellio.noted.utils;

import android.content.Context;

import com.cerebellio.noted.helpers.PreferenceHelper;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Commonly used Date functions
 */
public class DateFunctions {

    private static final String LOG_TAG = TextFunctions.makeLogTag(DateFunctions.class);

    private DateFunctions(){}

    /**
     * Get the date and time for a given value
     *
     * @param prefix        String to prefix {@link PrettyTime}
     * @param time          millis since Epoch
     * @param context
     * @return              time as a String
     */
    public static String getTime(String prefix, long time, Context context) {
        if (PreferenceHelper.getPrefDateFormatValue(context).equals(PreferenceHelper.SETTINGS_DISPLAY_DATE_FORMAT_PRETTY)) {
            return getPrettyTime(prefix, time, context);
        } else if (PreferenceHelper.getPrefDateFormatValue(context).equals(PreferenceHelper.SETTINGS_DISPLAY_DATE_FORMAT_LONG)) {
            return getDateString(prefix, time, "EEE, d MMM yyyy HH:mm:ss");
        } else {
            return getDateString(prefix, time, "dd MMM HH:mm");
        }
    }

    /**
     * Creates a {@link PrettyTime} String
     * @param prefix        String to prefix {@link PrettyTime}
     * @param time          millis since Epoch
     * @param context
     * @return              {@link PrettyTime} String
     */
    private static String getPrettyTime(String prefix, long time, Context context) {
        return getPrettyTime(prefix, time, context.getResources().getConfiguration().locale);
    }

    /**
     * Creates a {@link PrettyTime} String
     * @param prefix        String to prefix {@link PrettyTime}
     * @param time          millis since Epoch
     * @param locale        Locale to set
     * @return              {@link PrettyTime} String
     */
    private static String getPrettyTime(String prefix, long time, Locale locale) {
        if (time == 0) {
            return "";
        }

        Date date = new Date(time);
        PrettyTime prettyTime = new PrettyTime();

        if (locale != null) {
            prettyTime.setLocale(locale);
        }

        return prefix + " " + prettyTime.format(date);
    }

    /**
     * Converts milliseconds to Date in format dd/MM/yy HH:mm:ss
     * @param prefix        String to prefix
     * @param millis        millis since Epoch
     * @param pattern       pattern to follow, i.e. dd MMM HH:mm
     * @return              formatted String
     */
    private static String getDateString(String prefix, long millis, String pattern) {
        return prefix
                + " "
                + (millis == 0 ? "": new SimpleDateFormat(pattern, Locale.getDefault())
                                        .format(new Date(millis)));
    }

}

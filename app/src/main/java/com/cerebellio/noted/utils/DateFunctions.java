package com.cerebellio.noted.utils;

import android.content.Context;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Commonly used Date functions
 */
public abstract class DateFunctions {

    private static final String LOG_TAG = TextFunctions.makeLogTag(DateFunctions.class);

    /**
     * Creates a {@link PrettyTime} String
     * @param prefix        String to prefix {@link PrettyTime}
     * @param time          millis since Epoch
     * @param context
     * @return              {@link PrettyTime} String
     */
    public static String getPrettyTime(String prefix, long time, Context context) {
        return getPrettyTime(prefix, time, context.getResources().getConfiguration().locale);
    }

    /**
     * Creates a {@link PrettyTime} String
     * @param prefix        String to prefix {@link PrettyTime}
     * @param time          millis since Epoch
     * @param locale        Locale to set
     * @return              {@link PrettyTime} String
     */
    public static String getPrettyTime(String prefix, long time, Locale locale) {
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
     * @param millis        millis since Epoch
     * @return              formatted String
     */
    public static String getDateString(long millis) {
        return millis == 0
                ? ""
                : new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())
                                        .format(new Date(millis));
    }

}

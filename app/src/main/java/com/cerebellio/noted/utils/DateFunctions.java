package com.cerebellio.noted.utils;

import android.content.Context;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.Locale;

/**
 * Created by Sam on 10/02/2016.
 */
public abstract class DateFunctions {

    public static String getPrettyTime(String prefix, long time, Context context) {
        return getPrettyTime(prefix, time, context.getResources().getConfiguration().locale);
    }

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

}

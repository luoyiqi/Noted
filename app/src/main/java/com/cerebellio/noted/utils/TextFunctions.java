package com.cerebellio.noted.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;

import java.util.ArrayList;
import java.util.List;

/**
 * Commonly used Text functions
 */
public abstract class TextFunctions {

    private static final String LOG_TAG = TextFunctions.makeLogTag(TextFunctions.class);

    /**
     * Make a log tag
     *
     * @param callingClass      class to base tag upon
     * @return                  created tag
     */
    public static String makeLogTag(Class callingClass) {
        return callingClass.getSimpleName();
    }

    /**
     * Converts a String to an equivalent {@link SpannableString}
     *
     * @param s         String to convert
     * @return          equivalent {@link SpannableString}
     */
    public static SpannableString convertStringToSpannable(String s) {
        return new SpannableString(s);
    }

    /**
     * Strike through given {@link SpannableString}
     *
     * @param s    {@link SpannableString} to strike through
     */
    public static void strikeThrough(SpannableString s) {
        s.setSpan(new StrikethroughSpan(), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    /**
     * Convert a separated String to a List
     *
     * @param string        raw String to separate
     * @param separator     separator character(s)
     * @return              created List
     */
    public static List<String> splitStringToList(String string, String separator) {

        List<String> strings = new ArrayList<>();

        for (String tag : string.split(separator)) {
            strings.add(tag.trim());
        }

        return strings;
    }

    /**
     * Convert a List to a character separated
     *
     * @param list              List to convert
     * @param separator         separator character(s)
     * @param isSpaceNeeded     is space needed after separator
     * @return                  formatted String
     */
    public static String listToSeparatedString(
            List<String> list, String separator, boolean isSpaceNeeded) {

        if (list.isEmpty()) {
            return "";
        }

        String tagString = "";

        for (String string : list) {

            //Don't add separator before first String
            if (!tagString.equals("")) {
                tagString += isSpaceNeeded ? separator + " " : separator;
            }

            tagString += string;
        }

        return tagString;
    }

}

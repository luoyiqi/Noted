package com.cerebellio.noted.utils;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Commonly used Text functions
 */
public class TextFunctions {

    private static final String LOG_TAG = TextFunctions.makeLogTag(TextFunctions.class);

    private TextFunctions(){}

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
     * @param string         String to convert
     * @return          equivalent {@link SpannableString}
     */
    public static SpannableString convertStringToSpannable(String string) {
        return new SpannableString(string);
    }

    /**
     * Strike through given {@link SpannableString}
     *
     * @param string    {@link SpannableString} to strike through
     */
    public static void strikeThrough(SpannableString string) {
        string.setSpan(new StrikethroughSpan(), 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    /**
     * Italicise given {@link SpannableString}
     *
     * @param string    {@link SpannableString} to italicise
     */
    public static void italicise(SpannableString string) {
        string.setSpan(new StyleSpan(Typeface.ITALIC), 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    /**
     * Convert a separated String to a List
     *
     * @param string        raw String to separate
     * @param separator     separator character(s)
     * @return              created List
     */
    public static List<String> splitStringToList(String string, String separator) {

        if (string.equals("")) {
            return new ArrayList<>();
        }

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

    /**
     * Creates a tag from a given string, i.e. no spaces, lowercase
     *
     * @param string        String to modify
     * @param maxLength     if string is greater than this length, it is truncated with an ellipse
     * @return              given String as tag
     */
    public static String truncateWithEllipse(String string, int maxLength) {
        return string.length() <= maxLength
                ? string
                : string.substring(0, maxLength - 1) + "\u2026";
    }

    /**
     * Creates a tag from a given string, i.e. no spaces, lowercase
     *
     * @param string        String to modify
     * @param maxLength     if string is greater than this length, it is truncated with an ellipse
     * @return              given String as tag
     */
    public static SpannableString truncateWithEllipse(SpannableString string, int maxLength) {
        return string.length() <= maxLength
                ? string
                : convertStringToSpannable(string.subSequence(0, maxLength - 1) + "\u2026");
    }

    /**
     * Creates a tag from a given string, i.e. no spaces, lowercase
     *
     * @param string        String to modify
     * @param maxLength     if string is greater than this length, it is truncated with an ellipse
     * @param makeLowerCase true if string should be converted to lower case
     * @return              given String as tag
     */
    public static String truncateWithEllipse(String string, int maxLength, boolean makeLowerCase) {
        return truncateWithEllipse(string, maxLength).toLowerCase(Locale.getDefault());
    }

    /**
     * Strip special characters from the given passage
     *
     * @param string            passage to operate on
     * @param isUpperCase       true if return should be upper case, false if to be left alone
     * @return                  String without special characters
     */
    public static String stripSpecialCharacters(String string, boolean isUpperCase) {
        return isUpperCase ? string.replaceAll("[^\\p{L} ]", "").toUpperCase()
                : string.replaceAll("[^\\p{L} ]", "");
    }

    /**
     * Replace tabs, returns and newlines with a space
     *
     * @param string            string to edit
     * @return                  edited string
     */
    public static String replaceNewlineReturnTabWithSpace(String string) {
        return string.replaceAll("[\\t\\n\\r]"," ");
    }

    /**
     * Splits a given String into words
     *
     * @param string            String to split
     * @return                  Array of words
     */
    public static String[] splitStringToWords(String string) {
        return string.split("\\s+");
    }

    /**
     * Check if given array contains given String
     *
     * @param array             Array to look in
     * @param toCheck           String to check for
     * @return                  true iff found
     */
    public static boolean arrayContains(String[] array, String toCheck) {
        Collator collator = Collator.getInstance(Locale.getDefault());

        //Ignore case but don't ignore accents etc.
        collator.setStrength(Collator.SECONDARY);
        for (String string : array) {
            if (collator.compare(string, toCheck) == 0) {
                return true;
            }
        }
        return false;
    }

}

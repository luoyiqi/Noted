package com.cerebellio.noted.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;

/**
 * Created by Sam on 10/02/2016.
 */
public abstract class TextFunctions {

    public static SpannableString convertStringToSpannable(String s) {
        return new SpannableString(s);
    }

    public static void strikeThrough(SpannableString s) {
        s.setSpan(new StrikethroughSpan(), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

}

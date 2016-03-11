package com.cerebellio.noted.utils;

import android.view.HapticFeedbackConstants;
import android.view.View;

import com.cerebellio.noted.helpers.PreferenceHelper;

/**
 * Commonly used feedback functions
 */
public class FeedbackFunctions {

    private static final String LOG_TAG = TextFunctions.makeLogTag(FeedbackFunctions.class);

    private FeedbackFunctions(){}

    /**
     * Checks whether vibration is enabled and vibrates the given View if so
     *
     * @param view          View to vibrate
     */
    public static void vibrate(View view) {
        if (view == null) {
            return;
        }

        if (PreferenceHelper.getPrefVibration(view.getContext())) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }
    }

}

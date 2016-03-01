package com.cerebellio.noted.utils;

import android.view.HapticFeedbackConstants;
import android.view.View;

/**
 * Commonly used feedback functions
 */
public abstract class FeedbackFunctions {

    /**
     * Checks whether vibration is enabled and vibrates the given View if so
     *
     * @param view          View to vibrate
     */
    public static void vibrate(View view) {
        if (view == null) {
            return;
        }

        if (PreferenceFunctions.getPrefVibration(view.getContext())) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }
    }

}

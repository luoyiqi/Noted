package com.cerebellio.noted;

import android.app.Application;
import android.preference.PreferenceManager;

import com.cerebellio.noted.utils.TextFunctions;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Application extension for this app
 */
public class ApplicationNoted extends Application {

    private static final String LOG_TAG = TextFunctions.makeLogTag(ApplicationNoted.class);

    public static Bus bus = new Bus(ThreadEnforcer.MAIN);

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.settings_display, false);
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.settings_wordcloud, false);
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.settings_feedback, false);
    }
}

package com.cerebellio.noted;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.cerebellio.noted.utils.Constants;

/**
 * Created by Sam on 09/02/2016.
 */
public class ActivityBase extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int themeId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getInt(Constants.SHARED_PREFS_THEME_ID, Constants.DEFAULT_THEME_ID);
        setTheme(themeId);

        super.onCreate(savedInstanceState);
    }
}

package com.cerebellio.noted;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.cerebellio.noted.utils.Constants;

/**
 * Created by Sam on 09/02/2016.
 */
public class ActivityBase extends AppCompatActivity {

    protected final int TRANSITION_VERTICAL = 0;
    protected final int TRANSITION_HORIZONTAL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int themeId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getInt(Constants.SHARED_PREFS_THEME_ID, Constants.DEFAULT_THEME_ID);
        setTheme(themeId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        super.onCreate(savedInstanceState);
    }

    protected void animateFragmentTransition(FragmentTransaction transaction, int direction) {
        if (direction == TRANSITION_HORIZONTAL) {
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}

package com.cerebellio.noted;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.cerebellio.noted.utils.PreferenceFunctions;
import com.cerebellio.noted.utils.TextFunctions;

/**
 * Base for Activities, contains commonly used operations
 */
public abstract class ActivityBase extends AppCompatActivity {

    private static final String LOG_TAG = TextFunctions.makeLogTag(ActivityBase.class);

    protected final int TRANSITION_HORIZONTAL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (PreferenceFunctions.getPrefTheme(getApplicationContext())
                .equals(PreferenceFunctions.SETTINGS_DISPLAY_THEME_LIGHT)) {
            setTheme(R.style.NotedTheme_Default);
        } else {
            setTheme(R.style.NotedTheme_Dark);
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }

        super.onCreate(savedInstanceState);
    }

    /**
     * Animates a fragment transition
     * @param transaction   {@link FragmentTransaction} to animate
     * @param direction     {@link #TRANSITION_HORIZONTAL}
     */
    protected void animateFragmentTransition(FragmentTransaction transaction, int direction) {
        if (direction == TRANSITION_HORIZONTAL) {
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    /**
     * Checks if {@link Fragment} is currently in foreground
     * @param fragment      {@link Fragment} to check
     * @return              true if in foreground
     */
    protected boolean isCurrentFragment(Fragment fragment) {
        return fragment != null && fragment.isVisible();
    }
}

package com.cerebellio.noted.helpers;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

/**
 * Created by Sam on 06/03/2016.
 */
public class AnimationsHelper {

    private AnimationsHelper(){}

    public static void showFloatingActionsMenu(FloatingActionsMenu floatingActionsMenu) {
        floatingActionsMenu
                .animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
    }

    public static void hideFloatingActionsMenu(FloatingActionsMenu floatingActionsMenu) {
        floatingActionsMenu
                .animate()
                .translationY(floatingActionsMenu.getHeight())
                .setInterpolator(new AccelerateInterpolator(2))
                .start();
    }

}

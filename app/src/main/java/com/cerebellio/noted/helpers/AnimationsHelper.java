package com.cerebellio.noted.helpers;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

/**
 * Helper class to perform animations
 */
public class AnimationsHelper {

    private static final int FAB_ANIMATION_MILLIS = 400;

    public enum AnimationDirection {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    private AnimationsHelper(){}

    /**
     * Animate in a {@link FloatingActionsMenu}
     *
     * @param floatingActionsMenu       {@link FloatingActionsMenu} to animate
     */
    public static void showFloatingActionsMenu(FloatingActionsMenu floatingActionsMenu) {
        translateFloatingActionsMenu(floatingActionsMenu,
                new DecelerateInterpolator(2), 0, AnimationDirection.UP);
    }

    /**
     * Animate out a {@link FloatingActionsMenu}
     *
     * @param floatingActionsMenu       {@link FloatingActionsMenu} to animate
     */
    public static void hideFloatingActionsMenu(FloatingActionsMenu floatingActionsMenu) {
        translateFloatingActionsMenu(floatingActionsMenu,
                new AccelerateInterpolator(2), floatingActionsMenu.getHeight(), AnimationDirection.DOWN);
    }

    /**
     * Animate a {@link FloatingActionsMenu} in a given direction and distance
     *
     * @param floatingActionsMenu       {@link FloatingActionsMenu} to animate
     */
    public static void translateFloatingActionsMenu(FloatingActionsMenu floatingActionsMenu,
                                                    Interpolator interpolator, int distance, AnimationDirection direction) {
        switch (direction) {
            case UP:
                floatingActionsMenu
                        .animate()
                        .setDuration(FAB_ANIMATION_MILLIS)
                        .translationY(-distance)
                        .setInterpolator(interpolator)
                        .start();
                break;
            case DOWN:
                floatingActionsMenu
                        .animate()
                        .setDuration(FAB_ANIMATION_MILLIS)
                        .translationY(distance)
                        .setInterpolator(interpolator)
                        .start();
                break;
            case LEFT:
                floatingActionsMenu
                        .animate()
                        .setDuration(FAB_ANIMATION_MILLIS)
                        .translationX(-distance)
                        .setInterpolator(interpolator)
                        .start();
                break;
            case RIGHT:
                floatingActionsMenu
                        .animate()
                        .setDuration(FAB_ANIMATION_MILLIS)
                        .translationX(distance)
                        .setInterpolator(interpolator)
                        .start();
                break;
        }
    }

    /**
     * Rotate a View 180 degrees about its centre
     *
     * @param view              View to animate
     * @param fromAngle         initial angle
     * @param duration          duration of animation
     * @param fillAfter         keep position after animation?
     */
    public static void halfRotate(View view, int fromAngle, int duration, boolean fillAfter) {
        rotate(view, fromAngle, fromAngle + 180, duration, fillAfter);
    }

    /**
     * Rotate a View 260 degrees about its centre
     *
     * @param view              View to animate
     * @param duration          duration of animation
     * @param fillAfter         keep position after animation?
     */
    public static void fullRotate(View view, int duration, boolean fillAfter) {
        rotate(view, 0, 360, duration, fillAfter);
    }


    /**
     * Rotate a View
     *
     * @param view              View to animate
     * @param fromAngle         initial angle
     * @param toAngle           final angle
     * @param duration          duration of animation
     * @param fillAfter         keep position after animation?
     */
    public static void rotate(View view, int fromAngle, int toAngle, int duration, boolean fillAfter) {
        RotateAnimation rotateAnimation = new RotateAnimation(fromAngle, toAngle,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(duration);
        rotateAnimation.setFillAfter(fillAfter);
        rotateAnimation.setInterpolator(new DecelerateInterpolator());
        view.startAnimation(rotateAnimation);
    }

}

package com.cerebellio.noted.utils;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;

import com.cerebellio.noted.R;
import com.cerebellio.noted.views.WrapContentGridLayoutManager;

import java.util.Random;

/**
 * Contains a number of functions used throughout the app
 */
public class UtilityFunctions {

    private static final String LOG_TAG = TextFunctions.makeLogTag(UtilityFunctions.class);

    private UtilityFunctions(){}

    /**
     * Sets up a given RecyclerView with a {@link LinearLayoutManager}
     *
     * @param context           calling Context
     * @param recyclerView      RecyclerView to set up
     * @param adapter           {@link android.support.v7.widget.RecyclerView.Adapter} to assign
     * @param orientation       {@link LinearLayoutManager#VERTICAL} or {@link LinearLayoutManager#HORIZONTAL}
     */
    public static void setUpLinearRecycler(Context context, RecyclerView recyclerView,
                                           RecyclerView.Adapter adapter, int orientation) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(orientation);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Sets up a given RecyclerView with a {@link WrapContentGridLayoutManager}
     *
     * @param context           calling Context
     * @param recyclerView      RecyclerView to set up
     * @param adapter           {@link android.support.v7.widget.RecyclerView.Adapter} to assign
     * @param columns           number of columns
     */
    public static void setUpWrapContentGridRecycler(
            Context context, RecyclerView recyclerView, RecyclerView.Adapter adapter, int columns) {
        WrapContentGridLayoutManager gridLayout = new WrapContentGridLayoutManager(context, columns);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayout);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Sets up a given RecyclerView with a {@link StaggeredGridLayoutManager}
     *
     * @param recyclerView      RecyclerView to set up
     * @param adapter           {@link android.support.v7.widget.RecyclerView.Adapter} to assign
     * @param columns           number of columns
     * @param orientation       {@link LinearLayoutManager#HORIZONTAL}, {@link LinearLayoutManager#VERTICAL}
     */
    public static void setUpStaggeredGridRecycler(
            RecyclerView recyclerView, RecyclerView.Adapter adapter, int columns, int orientation) {
        StaggeredGridLayoutManager staggeredLayout = new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL);
        staggeredLayout.setOrientation(orientation);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(staggeredLayout);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Gets the resource id of an attribute for the current theme
     *
     * @param attr              attribute to resolve
     * @param context           calling Context
     * @return                  resource id
     */
    public static int getResIdFromAttribute(final int attr, Context context) {
        if (attr == 0)
            return 0;
        final TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.resourceId;
    }

    /**
     * Grabs a random Integer from a given array
     *
     * @return randomly selected Integer
     */
    public static int getRandomIntegerFromArray(Integer[] fullList) {
        return fullList[new Random().nextInt(fullList.length)];
    }

    /**
     * Convert a given number of pixels to dp
     *
     * @param context
     * @param pixels        number to convert
     * @return              DiP equivalent
     */
    public static float convertPixelsToDp(Context context, int pixels) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, pixels, context.getResources().getDisplayMetrics());
    }


    /**
     * Checks whether the current theme set in the given context is considered dark
     *
     * @param context       context of theme to check
     * @return              true iff dark
     */
    public static boolean isConsideredDarkTheme(Context context) {
        return context.getResources().getBoolean(
                UtilityFunctions.getResIdFromAttribute(R.attr.is_dark_theme, context));
    }

}

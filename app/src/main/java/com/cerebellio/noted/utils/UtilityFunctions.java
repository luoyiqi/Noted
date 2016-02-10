package com.cerebellio.noted.utils;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sam on 09/02/2016.
 */
public abstract class UtilityFunctions {

    public static void setUpLinearRecycler(Context context, RecyclerView recyclerView,
                                           RecyclerView.Adapter adapter, int orientation) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(orientation);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public static void setUpGridRecycler(Context context, RecyclerView recyclerView, RecyclerView.Adapter adapter, int columns) {
        GridLayoutManager gridLayout = new GridLayoutManager(context, columns);
        gridLayout.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayout);
        recyclerView.setAdapter(adapter);
    }

    public static void setUpStaggeredGridRecycler(RecyclerView recyclerView, RecyclerView.Adapter adapter, int columns) {
        StaggeredGridLayoutManager staggeredLayout = new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL);
        staggeredLayout.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(staggeredLayout);
        recyclerView.setAdapter(adapter);
    }

    public static String getDateLastModifiedString(Long millisSinceUpdate) {
        return new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())
                .format(new Date(millisSinceUpdate));
    }

    public static int getResIdFromAttribute(final int attr, Context context) {
        if (attr == 0)
            return 0;
        final TypedValue typedvalueattr = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedvalueattr, true);
        return typedvalueattr.resourceId;
    }

}

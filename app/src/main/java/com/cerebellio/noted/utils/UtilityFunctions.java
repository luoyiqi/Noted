package com.cerebellio.noted.utils;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sam on 09/02/2016.
 */
public class UtilityFunctions {

    public static void setUpStaggeredGridRecycler(Context context, RecyclerView recyclerView, RecyclerView.Adapter adapter, int columns) {
        StaggeredGridLayoutManager staggeredLayout = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        staggeredLayout.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(staggeredLayout);
        recyclerView.setAdapter(adapter);
    }

    public static String getDateLastModifiedString(Long millisSinceUpdate) {
        return new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())
                .format(new Date(millisSinceUpdate));
    }

}

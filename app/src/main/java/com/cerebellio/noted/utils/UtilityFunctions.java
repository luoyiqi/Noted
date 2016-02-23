package com.cerebellio.noted.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.format.DateFormat;
import android.util.TypedValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Contains a number of functions used throughout the app
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


    public static int adjustAlpha(int colour, int alpha) {
        return Color.argb(alpha,
                Color.red(colour),
                Color.green(colour),
                Color.blue(colour));
    }


    public static String getDateString(long millisSinceUpdate) {
        return millisSinceUpdate == 0 ? "" : new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())
                .format(new Date(millisSinceUpdate));
    }

    public static int getResIdFromAttribute(final int attr, Context context) {
        if (attr == 0)
            return 0;
        final TypedValue typedvalueattr = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedvalueattr, true);
        return typedvalueattr.resourceId;
    }

    /**
     * Grabs a random Integer from a given array
     *
     * @return randomly selected Integer
     */
    public static Integer getRandomIntegerFromArray(Integer[] fullList) {
        return fullList[new Random().nextInt(fullList.length)];
    }

    public static Bitmap getBitmapFromFile(String path) {
        Bitmap bitmap = null;

        try {
            File f = new File(path);
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static String saveSketchToStorage(Bitmap sketch, Context context) throws IOException {
        ContextWrapper contextWrapper = new ContextWrapper(context.getApplicationContext());
        File directory = contextWrapper.getDir("sketches", Context.MODE_PRIVATE);
        String  fileName = DateFormat.format("MM-dd-yy HH-mm-ss", new Date().getTime()).toString();
        File path = new File(directory, fileName);

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(path);
            // Use the compress method on the BitMap object to write image to the OutputStream
            sketch.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                fos.close();
            }
        }

        return path.getAbsolutePath();
    }
}

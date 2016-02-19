package com.cerebellio.noted.utils;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Sam on 10/02/2016.
 */
public abstract class RecyclerScroll extends RecyclerView.OnScrollListener {

    private static final float MINIMUM = 25f;

    private int mScrollDistance = 0;
    private boolean mIsVisible = true;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (mIsVisible && mScrollDistance > MINIMUM) {
            hide();
            mScrollDistance = 0;
            mIsVisible = false;
        }
        else if (!mIsVisible && mScrollDistance < -MINIMUM) {
            show();
            mScrollDistance = 0;
            mIsVisible = true;
        }
        if ((mIsVisible && dy > 0) || (!mIsVisible && dy < 0)) {
            mScrollDistance += dy;
        }
    }

    public abstract void hide();
    public abstract void show();
}

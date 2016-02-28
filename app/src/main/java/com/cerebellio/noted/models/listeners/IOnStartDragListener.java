package com.cerebellio.noted.models.listeners;

import android.support.v7.widget.RecyclerView;

/**
 * Interface to facilitate dragging of a {@link RecyclerView.ViewHolder}
 */
public interface IOnStartDragListener {
    void onStartDrag(RecyclerView.ViewHolder viewHolder);
}

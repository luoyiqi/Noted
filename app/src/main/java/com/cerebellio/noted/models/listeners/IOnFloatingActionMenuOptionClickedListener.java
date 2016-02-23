package com.cerebellio.noted.models.listeners;

import com.cerebellio.noted.models.Item;

/**
 * Interface which notifies when an {@link Item} has been created
 * and passes the {@link com.cerebellio.noted.models.Item.Type}
 */
public interface IOnFloatingActionMenuOptionClickedListener {
    void OnFabCreateItemClick(Item.Type type);
}

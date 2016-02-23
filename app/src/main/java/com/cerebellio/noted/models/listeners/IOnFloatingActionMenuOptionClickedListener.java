package com.cerebellio.noted.models.listeners;

import com.cerebellio.noted.models.Item;

/**
 * Created by Sam on 09/02/2016.
 */
public interface IOnFloatingActionMenuOptionClickedListener {
    void OnFabCreateItemClick(Item.Type type);
}

package com.cerebellio.noted.models.listeners;

import com.cerebellio.noted.models.Item;

/**
 * Interface which allows notification when {@link Item}
 * needs to be edited
 */
public interface IOnItemFocusNeedsUpdatingListener {
    void onRemove(int position);
    void onUpdateColour(int position, int newColour);
}

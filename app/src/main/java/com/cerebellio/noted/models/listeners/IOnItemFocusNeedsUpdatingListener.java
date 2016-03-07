package com.cerebellio.noted.models.listeners;

import com.cerebellio.noted.models.Item;

/**
 * Interface which allows notification when {@link Item}
 * needs to be edited
 */
public interface IOnItemFocusNeedsUpdatingListener {
    void onRemove(Item.Status prevStatus, Item.Status newStatus, Item item, int position);
    void onUpdateColour(int position, int newColour);
}

package com.cerebellio.noted.models.listeners;

import com.cerebellio.noted.models.Item;

/**
 * Interface which notifies when an existing {@link Item}
 * has been selected to edit
 */
public interface IOnItemSelectedToEditListener {
    void onItemSelected(Item item);
}

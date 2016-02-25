package com.cerebellio.noted.models.events;

import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.utils.TextFunctions;

/**
 * Item and its position in a list to send through event bus
 */
public class ItemWithListPositionEvent {

    private static final String LOG_TAG = TextFunctions.makeLogTag(ItemWithListPositionEvent.class);

    private Item mItem;
    private int mPosition;

    public ItemWithListPositionEvent(Item item, int position) {

        mItem = item;
        mPosition = position;
    }

    public Item getItem() {
        return mItem;
    }

    public int getPosition() {
        return mPosition;
    }
}

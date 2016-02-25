package com.cerebellio.noted.models.events;

import com.cerebellio.noted.utils.TextFunctions;

/**
 * Colour to send through event bus
 */
public class ColourSelectedEvent {

    private static final String LOG_TAG = TextFunctions.makeLogTag(ColourSelectedEvent.class);

    private int mColour;

    public ColourSelectedEvent(int colour) {
        mColour = colour;
    }

    public int getColour() {
        return mColour;
    }
}

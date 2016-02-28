package com.cerebellio.noted.models.events;

/**
 * Allows title change event to be passed through event bus
 */
public class TitleChangedEvent {

    private String mTitle;

    public TitleChangedEvent(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }
}

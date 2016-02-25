package com.cerebellio.noted.models.listeners;

/**
 * Interface which allows tag to be operated on
 */
public interface IOnTagOperationListener {
    void onTagAdded(String tag);
    void onTagEdited(String originalTag, String newTag);
    void onTagDeleted(String tag);
}

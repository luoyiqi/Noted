package com.cerebellio.noted.models;

import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.TextFunctions;
import com.cerebellio.noted.utils.UtilityFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Represents an abstract item
 */
public abstract class Item {

    private static final String LOG_TAG = TextFunctions.makeLogTag(Item.class);

    public static final String TAG_STRING_SEPARATOR = ",";

    protected List<String> mTagList = new ArrayList<>();
    protected Status mStatus = Status.PINBOARD;
    protected Reminder mReminder;

    protected long mId;
    protected int mColour = UtilityFunctions.getRandomIntegerFromArray(Constants.MATERIAL_COLOURS);
    protected long mCreatedDate;
    protected long mEditedDate;
    protected boolean mIsImportant;
    protected boolean mIsLocked;

    public enum Type {
        NOTE,
        CHECKLIST,
        CHECKLIST_ITEM,
        SKETCH
    }

    public enum Status {
        PINBOARD,
        ARCHIVED,
        DELETED
    }

    /**
     *
     * @return {@link com.cerebellio.noted.models.Item.Type}
     */
    public abstract Type getItemType();

    /**
     * Check whether given {@link Item} is considered empty
     * @return              true if empty, false otherwise
     */
    public abstract boolean isEmpty();

    /**
     * Get all text related to this {@link Item} including tags
     *
     * @return              retrieved text
     */
    public abstract String getText();

    /**
     * Delete a tag
     * @param tag       tag to delete
     */
    public void deleteTag(String tag) {
        mTagList.remove(mTagList.indexOf(tag.trim()));
    }

    /**
     * Add a tag
     * @param newTag    tag to add
     */
    public void addTag(String newTag) {

        //Don't add empty string
        if (newTag.trim().equals("")) {
            return;
        }

        //Add tag if it doesn't already exist
        if (!isTagExisting(newTag.trim())) {
            mTagList.add(newTag.trim().toLowerCase(Locale.getDefault()));
        }
    }

    /**
     * Edit tag
     * @param originalTag       tag before editing
     * @param newTag            tag after editing
     */
    public void editTag(String originalTag, String newTag) {

        if (newTag.trim().equals("")) {
            deleteTag(originalTag.trim());
        } else {
            mTagList.set(mTagList.indexOf(originalTag.trim()), newTag.trim());
        }

    }

    /**
     * Checks whether {@link Item} is already tagged with this tag
     * @param tag           tag to check
     * @return              true if tag is already present
     */
    public boolean isTagExisting(String tag) {
        return mTagList.contains(tag.trim());
    }

    /**
     *
     * @return          true if {@link Item} is possible to delete
     */
    public boolean canBeDeleted() {
        return true;
    }

    /**
     *
     * @return          true if {@link Item} is possible to archive
     */
    public boolean canBeArchived() {
        return mStatus.equals(Status.PINBOARD);
    }

    /**
     *
     * @return          true if {@link Item} is possible to pinboarded
     */
    public boolean canBePinboarded() {
        return mStatus.equals(Status.ARCHIVED);
    }

    /**
     *
     * @return          true if no tags are set
     */
    public boolean areTagsEmpty() {
        return mTagList.isEmpty();
    }




    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getCreatedDate() {
        return mCreatedDate;
    }

    public void setCreatedDate(long createdDate) {
        mCreatedDate = createdDate;
    }

    public long getEditedDate() {
        return mEditedDate;
    }

    public void setEditedDate(long editedDate) {
        mEditedDate = editedDate;
    }

    public int getColour() {
        return mColour;
    }

    public void setColour(int colour) {
        mColour = colour;
    }

    public boolean isImportant() {
        return mIsImportant;
    }

    public void setIsImportant(boolean isImportant) {
        mIsImportant = isImportant;
    }

    public boolean isLocked() {
        return mIsLocked;
    }

    public void setIsLocked(boolean isLocked) {
        mIsLocked = isLocked;
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        mStatus = status;
    }

    public Reminder getReminder() {
        return mReminder;
    }

    public void setReminder(Reminder reminder) {
        mReminder = reminder;
    }

    public String getRawTagString() {
        return TextFunctions.listToSeparatedString(mTagList, TAG_STRING_SEPARATOR, "", false);
    }

    public String getFormattedTagString() {
        return getFormattedTagString("");
    }

    public String getFormattedTagString(String precedeWith) {
        return TextFunctions.listToSeparatedString(mTagList, TAG_STRING_SEPARATOR, precedeWith, true);
    }

    public void setTagString(String string) {
        mTagList.addAll(TextFunctions.splitStringToList(string, Item.TAG_STRING_SEPARATOR));
    }
}

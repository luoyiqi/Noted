package com.cerebellio.noted.models;

import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.TextFunctions;
import com.cerebellio.noted.utils.UtilityFunctions;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an abstract item
 */
public abstract class Item {

    private static final String LOG_TAG = TextFunctions.makeLogTag(Item.class);

    public static final String TAG_STRING_SEPARATOR = ",";

    protected List<String> mTagList = new ArrayList<>();
    protected Status mStatus = Status.PINBOARD;

    protected long mId;
    protected int mColour = UtilityFunctions.getRandomIntegerFromArray(Constants.COLOURS);
    protected long mCreatedDate;
    protected long mEditedDate;
    protected boolean mIsImportant;

    public enum Type {
        NOTE,
        CHECKLIST,
        CHECKLIST_ITEM,
        SKETCH
    }

    public enum Status {
        PINBOARD,
        TRASHED,
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
            mTagList.add(newTag.trim());
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
     * @return          true if {@link Item} is possible to trash
     */
    public boolean canBeTrashed() {
        return mStatus.equals(Status.PINBOARD) || mStatus.equals(Status.ARCHIVED);
    }

    /**
     *
     * @return          true if {@link Item} is possible to delete
     */
    public boolean canBeDeleted() {
        return mStatus.equals(Status.TRASHED);
    }

    /**
     *
     * @return          true if {@link Item} is possible to archive
     */
    public boolean canBeArchived() {
        return mStatus.equals(Status.PINBOARD) || mStatus.equals(Status.TRASHED);
    }

    /**
     *
     * @return          true if {@link Item} is possible to pinboarded
     */
    public boolean canBePinboarded() {
        return mStatus.equals(Status.ARCHIVED) || mStatus.equals(Status.TRASHED);
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

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        mStatus = status;
    }

    public String getRawTagString() {
        return TextFunctions.listToSeparatedString(mTagList, TAG_STRING_SEPARATOR, false);
    }

    public String getFormattedTagString() {
        return TextFunctions.listToSeparatedString(mTagList, TAG_STRING_SEPARATOR, true);
    }

    public void setTagString(String string) {
        mTagList.addAll(TextFunctions.splitStringToList(string, Item.TAG_STRING_SEPARATOR));
    }
}

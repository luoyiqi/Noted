package com.cerebellio.noted.models;

import com.cerebellio.noted.utils.TextFunctions;

/**
 * Represents a single item in a {@link CheckList}
 */
public class CheckListItem extends Item implements Cloneable {

    private static final String LOG_TAG = TextFunctions.makeLogTag(CheckListItem.class);

    private String mContent = "";

    private long mChecklistId;
    private boolean mIsCompleted = false;
    private int mPosition;

    public CheckListItem() {}

    public CheckListItem(long checklistId) {
        mChecklistId = checklistId;
    }

    @Override
    public boolean isEmpty() {
        //Empty if contains default text
        return mContent.equals("");
    }

    @Override
    public Type getItemType() {
        return Type.CHECKLIST_ITEM;
    }

    /**
     * Creates a deep copy of this object
     *
     * @return          the deep copy
     */
    public CheckListItem createDeepCopy() {

        CheckListItem newItem = new CheckListItem(mChecklistId);

        newItem.setContent(mContent);
        newItem.setIsCompleted(mIsCompleted);
        newItem.setPosition(mPosition);

        return newItem;
    }

    public long getChecklistId() {
        return mChecklistId;
    }

    public void setChecklistId(long checklistId) {
        mChecklistId = checklistId;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public boolean isCompleted() {
        return mIsCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        mIsCompleted = isCompleted;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }
}

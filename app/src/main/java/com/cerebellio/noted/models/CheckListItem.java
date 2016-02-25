package com.cerebellio.noted.models;

import com.cerebellio.noted.utils.TextFunctions;

/**
 * Represents a single item in a {@link CheckList}
 */
public class CheckListItem extends Item {

    private static final String LOG_TAG = TextFunctions.makeLogTag(CheckListItem.class);

    private String mContent = "";

    private long mChecklistId;
    private boolean mIsCompleted = false;
    private int mIndex;

    public CheckListItem() {}

    public CheckListItem(long checklistId) {
        mChecklistId = checklistId;
    }

    @Override
    public boolean isEmpty() {
        //Empty if not completed and only contains default text
        return !mIsCompleted && mContent.equals("");
    }

    @Override
    public Type getItemType() {
        return Type.CHECKLIST_ITEM;
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

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }
}

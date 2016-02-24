package com.cerebellio.noted.models;

/**
 * Created by Sam on 10/02/2016.
 */
public class CheckListItem extends Item {

    private String mContent = "";

    private long mChecklistId;
    private boolean mIsCompleted = false;

    public CheckListItem() {}

    public CheckListItem(long checklistId) {
        mChecklistId = checklistId;
    }

    @Override
    public boolean isEmpty() {
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
}

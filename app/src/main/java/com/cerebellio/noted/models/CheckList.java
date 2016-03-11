package com.cerebellio.noted.models;

import com.cerebellio.noted.utils.TextFunctions;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Checklist, which can be populated with completable items
 */
public class CheckList extends Item {

    private static final String LOG_TAG = TextFunctions.makeLogTag(CheckList.class);

    private List<CheckListItem> mItems = new ArrayList<>();

    public CheckList() {

    }

    @Override
    public boolean isEmpty() {
        //Empty if only contains default item
        return mItems.size() == 1 && mItems.get(0).getContent().trim().equals("");
    }

    @Override
    public Type getItemType() {
        return Type.CHECKLIST;
    }

    @Override
    public String getText() {
        String text = "";

        for (CheckListItem item : mItems) {
            text += item.getText() + " ";
        }

        return text + " " + getFormattedTagString();
    }

    /**
     * Add a {@link CheckListItem} to this {@link CheckList}
     *
     * @param item          {@link CheckListItem} to add
     */
    public void addItem(CheckListItem item) {
        mItems.add(item);
    }

    /**
     *
     * @return      true if new {@link CheckListItem} needs adding
     */
    public boolean isNewItemNeeded() {
        return !mItems.get(mItems.size() - 1).isEmpty();
    }

    /**
     * Assigns positions to each {@link CheckListItem}
     */
    public void assignPositions() {
        for (int i = 0; i < mItems.size(); i++) {
            mItems.get(i).setPosition(i);
        }
    }


    public List<CheckListItem> getItems() {
        return mItems;
    }

    public void setItems(List<CheckListItem> items) {
        mItems = items;
    }

}

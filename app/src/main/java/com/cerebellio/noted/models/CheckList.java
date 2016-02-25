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
        addItem();
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

    public void addItem() {
        mItems.add(new CheckListItem(mId));
    }

    public boolean isNewItemNeeded() {
        return !mItems.get(mItems.size() - 1).isEmpty();
    }

    public List<CheckListItem> getItems() {
        return mItems;
    }

    public void setItems(List<CheckListItem> items) {
        mItems = items;
    }

}

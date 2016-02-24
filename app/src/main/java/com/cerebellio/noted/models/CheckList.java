package com.cerebellio.noted.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam on 10/02/2016.
 */
public class CheckList extends Item {

    private List<CheckListItem> mItems = new ArrayList<>();

    public CheckList() {
        addItem();
    }

    @Override
    public boolean isEmpty() {
        return mItems.size() == 1 && mTitle.equals("");
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

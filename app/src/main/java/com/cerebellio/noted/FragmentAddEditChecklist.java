package com.cerebellio.noted;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.models.CheckList;
import com.cerebellio.noted.models.CheckListItem;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.adapters.ChecklistItemsAdapter;
import com.cerebellio.noted.models.events.TitleChangedEvent;
import com.cerebellio.noted.models.listeners.IOnColourSelectedListener;
import com.cerebellio.noted.models.listeners.IOnStartDragListener;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.helpers.PreferenceHelper;
import com.cerebellio.noted.utils.TextFunctions;
import com.cerebellio.noted.utils.UtilityFunctions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Allows user to add new {@link CheckList} or edit an existing one
 */
public class FragmentAddEditChecklist extends FragmentBase
        implements IOnColourSelectedListener, IOnStartDragListener{

    @InjectView(R.id.fragment_add_edit_checklist_items_recycler) RecyclerView mRecycler;

    private static final String LOG_TAG = TextFunctions.makeLogTag(FragmentAddEditChecklist.class);

    private CheckList mCheckList;
    private SqlDatabaseHelper mSqlDatabaseHelper;
    private ChecklistItemsAdapter mAdapter;
    private List<CheckListItem> mOriginalItems = new ArrayList<>();
    private ItemTouchHelper mItemTouchHelper;

    private boolean mIsInEditMode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_edit_checklist, container, false);
        ButterKnife.inject(this, rootView);

        mIsInEditMode = getArguments().getBoolean(Constants.BUNDLE_IS_IN_EDIT_MODE);

        initChecklist();

        //Scroll to end of list so user can conveniently enter new item
        mRecycler.smoothScrollToPosition(mAdapter.getItemCount());

        ApplicationNoted.bus.post(new TitleChangedEvent(
                mIsInEditMode ? getString(R.string.title_checklist_edit) : getString(R.string.title_checklist_new)));

        return rootView;
    }

    @Override
    public void onPause() {

        super.onPause();

        if (hasBeenEdited()) {
            mCheckList.setEditedDate(new Date().getTime());
        }

        if (mCheckList.isEmpty()) {
            mCheckList.setStatus(Item.Status.DELETED);
        }

        mSqlDatabaseHelper.addOrEditChecklist(mCheckList);

        mSqlDatabaseHelper.closeDB();
    }

    @Override
    public void onColourSelected(Integer colour) {
        mCheckList.setColour(colour);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    /**
     * Determines whether the checklist has been edited
     *
     * @return          true if edited, false otherwise
     */
    private boolean hasBeenEdited() {

        //If item has been added or removed from original list
        if (mOriginalItems.size() != mCheckList.getItems().size()) {
            return true;
        }

        for (int i = 0; i < mOriginalItems.size(); i++) {

            CheckListItem originalItem = mOriginalItems.get(i);
            CheckListItem newItem = mCheckList.getItems().get(i);

            if (!originalItem.getContent().equals(newItem.getContent())
                    || originalItem.isCompleted() != newItem.isCompleted()) {

                //Content of an item or its completion mark has been changed
                return true;
            }
        }

        //No changes have been made
        return false;
    }

    /**
     * Carries out initialisation of {@link #mCheckList}
     * If we're editing, retrieves it from database.
     * If we're adding, insert new {@link CheckList} into database
     */
    private void initChecklist() {

        mSqlDatabaseHelper = new SqlDatabaseHelper(getActivity());

        if (mIsInEditMode) {

            //Retrieve Checklist from database
            mCheckList = (CheckList) mSqlDatabaseHelper.getItemById(
                    getArguments().getLong(Constants.BUNDLE_ITEM_TO_EDIT_ID), Item.Type.CHECKLIST);
        } else {

            //Create new Checklist
            mCheckList = (CheckList) mSqlDatabaseHelper.getItemById(
                    mSqlDatabaseHelper.addBlankChecklist(), Item.Type.CHECKLIST);
        }

        //Deep copy the list so we can check if it has been edited later
        for (CheckListItem item : mCheckList.getItems()) {
            mOriginalItems.add(item.createDeepCopy());
        }

        mAdapter = new ChecklistItemsAdapter(mCheckList, getActivity(), this);
        UtilityFunctions.setUpLinearRecycler(getActivity(), mRecycler,
                mAdapter, LinearLayoutManager.VERTICAL);

        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                PreferenceHelper.getPrefBehaviourSwipeToDelete(getActivity()) ?
                        ItemTouchHelper.START | ItemTouchHelper.END : 0) {

            @Override
            public boolean isLongPressDragEnabled() {
                //Using custom view to perform dragging
                return false;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                mAdapter.swap(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mAdapter.remove(viewHolder.getAdapterPosition());
                mAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                mAdapter.notifyDataSetChanged();
            }
        });

        mItemTouchHelper.attachToRecyclerView(mRecycler);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {

                for (int i = positionStart; i < positionStart + itemCount; i++) {

                    //Item has been removed, notify database
                    mSqlDatabaseHelper.addOrEditItem(mAdapter.getItems().get(i));
                    mAdapter.getItems().remove(i);
                }

                super.onItemRangeRemoved(positionStart, itemCount);
            }
        });

    }


}

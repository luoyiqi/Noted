package com.cerebellio.noted;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.models.CheckList;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.adapters.ChecklistItemsAdapter;
import com.cerebellio.noted.models.events.ChecklistItemEditedEvent;
import com.cerebellio.noted.models.listeners.IOnColourSelectedListener;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.TextFunctions;
import com.cerebellio.noted.utils.UtilityFunctions;
import com.squareup.otto.Subscribe;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Allows user to add new {@link CheckList} or edit an existing one
 */
public class FragmentAddEditChecklist extends Fragment implements IOnColourSelectedListener {

    @InjectView(R.id.fragment_add_edit_checklist_items_recycler) RecyclerView mRecycler;

    private static final String LOG_TAG = TextFunctions.makeLogTag(FragmentAddEditChecklist.class);

    private CheckList mCheckList;
    private SqlDatabaseHelper mSqlDatabaseHelper;
    private ChecklistItemsAdapter mAdapter;

    private boolean mIsInEditMode;
    //If user makes a change, this flag is set to true
    private boolean mHasBeenEdited = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_edit_checklist, container, false);
        ButterKnife.inject(this, rootView);

        mIsInEditMode = getArguments().getBoolean(Constants.BUNDLE_IS_IN_EDIT_MODE);

        initChecklist();

        //Scroll to end of list so user can conveniently enter new item
        mRecycler.smoothScrollToPosition(mAdapter.getItemCount());

        return rootView;
    }

    @Override
    public void onPause() {

        super.onPause();

        if (mHasBeenEdited) {
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

    @Subscribe
    public void receiveEvent(ChecklistItemEditedEvent event) {
        mHasBeenEdited = true;
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

        mAdapter = new ChecklistItemsAdapter(mCheckList);
        UtilityFunctions.setUpLinearRecycler(getActivity(), mRecycler,
                mAdapter, LinearLayoutManager.VERTICAL);

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

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {

                mRecycler.scrollToPosition(positionStart);
                super.onItemRangeInserted(positionStart, itemCount);
            }
        });
    }


}

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
import com.cerebellio.noted.models.listeners.IOnColourSelectedListener;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.UtilityFunctions;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Sam on 09/02/2016.
 */
public class FragmentAddEditChecklist extends Fragment implements IOnColourSelectedListener {

    @InjectView(R.id.fragment_add_edit_checklist_title) MaterialEditText mEditTitle;
    @InjectView(R.id.fragment_add_edit_checklist_items_recycler) RecyclerView mRecycler;

    private CheckList mCheckList;
    private SqlDatabaseHelper mSqlDatabaseHelper;

    private boolean mIsInEditMode;
    private ChecklistItemsAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_edit_checklist, container, false);
        ButterKnife.inject(this, rootView);

        mIsInEditMode = getArguments().getBoolean(Constants.BUNDLE_IS_IN_EDIT_MODE);

        initChecklist();

        //scroll to end of list
        mRecycler.smoothScrollToPosition(mAdapter.getItemCount());

        FragmentCreationModifiedDates fragmentCreationModifiedDates = new FragmentCreationModifiedDates();
        Bundle bundleDates = new Bundle();
        bundleDates.putLong(Constants.BUNDLE_ITEM_ID_FOR_DATES_FRAGMENT, mCheckList.getId());
        bundleDates.putSerializable(Constants.BUNDLE_ITEM_TYPE_FOR_DATES_FRAGMENT, Item.Type.CHECKLIST);
        fragmentCreationModifiedDates.setArguments(bundleDates);

        FragmentColourSelection fragmentColourSelection = new FragmentColourSelection();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_CURRENT_COLOUR, mCheckList.getColour());
        fragmentColourSelection.setArguments(bundle);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_add_edit_checklist_colour_selection_frame, fragmentColourSelection)
                .replace(R.id.fragment_add_edit_checklist_dates_frame, fragmentCreationModifiedDates)
                .commit();

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        mCheckList.setTitle(mEditTitle.getText().toString());

        mCheckList.setLastModifiedDate(new Date().getTime());

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

    private void initChecklist() {
        mSqlDatabaseHelper = new SqlDatabaseHelper(getActivity());

        if (mIsInEditMode) {
            mCheckList = (CheckList) mSqlDatabaseHelper.getItemById(
                    getArguments().getLong(Constants.BUNDLE_ITEM_TO_EDIT_ID), Item.Type.CHECKLIST);
            mEditTitle.setText(mCheckList.getTitle());
        } else {
            mCheckList = (CheckList) mSqlDatabaseHelper.getItemById(
                    mSqlDatabaseHelper.addBlankChecklist(), Item.Type.CHECKLIST);
        }

        mSqlDatabaseHelper.closeDB();

        mAdapter = new ChecklistItemsAdapter(mCheckList);
        UtilityFunctions.setUpLinearRecycler(getActivity(), mRecycler,
                mAdapter, LinearLayoutManager.VERTICAL);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                SqlDatabaseHelper sqlDatabaseHelper = new SqlDatabaseHelper(getActivity());

                for (int i = positionStart; i < positionStart + itemCount; i++) {
                    sqlDatabaseHelper.addOrEditItem(mAdapter.getItems().get(i));
                    mAdapter.getItems().remove(i);
                }

                sqlDatabaseHelper.closeDB();

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

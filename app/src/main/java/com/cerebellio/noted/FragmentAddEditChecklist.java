package com.cerebellio.noted;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.models.CheckList;
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
    @InjectView(R.id.fragment_add_edit_checklist_items_recycler_frame) FrameLayout mRecyclerContainer;
    @InjectView(R.id.fragment_add_edit_checklist_items_colour_selection_container) FrameLayout mColourSelectionContainer;

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

        FragmentColourSelection fragmentColourSelection = new FragmentColourSelection();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_CURRENT_COLOUR, mCheckList.getColour());
        fragmentColourSelection.setArguments(bundle);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_add_edit_checklist_colour_selection_frame, fragmentColourSelection)
                .commit();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSqlDatabaseHelper = new SqlDatabaseHelper(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();

        mCheckList.setTitle(mEditTitle.getText().toString());
        mCheckList.setLastModifiedDate(new Date().getTime());

        if (mCheckList.isEmpty()) {
            mCheckList.setIsTrashed(true);
        }

        mSqlDatabaseHelper.updateChecklist(mCheckList);

        mSqlDatabaseHelper.closeDB();
    }

    @Override
    public void onColourSelected(Integer colour) {
        mCheckList.setColour(colour);
    }

    private void initChecklist() {
        mSqlDatabaseHelper = new SqlDatabaseHelper(getActivity());

        if (mIsInEditMode) {
            mCheckList = mSqlDatabaseHelper.getChecklist(
                    getArguments().getLong(Constants.BUNDLE_CHECKLIST_TO_EDIT_ID));
            mEditTitle.setText(mCheckList.getTitle());
        } else {
            mSqlDatabaseHelper.addBlankChecklist();
            mCheckList = new CheckList(mSqlDatabaseHelper.getLatestChecklistId());
        }

        mAdapter = new ChecklistItemsAdapter(mCheckList, getActivity());
        UtilityFunctions.setUpLinearRecycler(getActivity(), mRecycler,
                mAdapter, LinearLayoutManager.VERTICAL);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                SqlDatabaseHelper sqlDatabaseHelper = new SqlDatabaseHelper(getActivity());

                for (int i = positionStart; i < positionStart + itemCount; i++) {
                    sqlDatabaseHelper.deleteItem(mAdapter.getItems().get(i));
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

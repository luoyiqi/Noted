package com.cerebellio.noted;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.Note;
import com.cerebellio.noted.models.listeners.IOnColourSelectedListener;
import com.cerebellio.noted.utils.Constants;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Sam on 09/02/2016.
 */
public class FragmentAddEditNote extends Fragment implements IOnColourSelectedListener {

    @InjectView(R.id.fragment_add_edit_note_title) MaterialEditText mEditTitle;
    @InjectView(R.id.fragment_add_edit_note_content) MaterialEditText mEditContent;

    private Note mNote;
    private SqlDatabaseHelper mSqlDatabaseHelper;

    private boolean mIsInEditMode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_edit_note, container, false);
        ButterKnife.inject(this, rootView);

        mIsInEditMode = getArguments().getBoolean(Constants.BUNDLE_IS_IN_EDIT_MODE);

        initNote();

        FragmentCreationModifiedDates fragmentCreationModifiedDates = new FragmentCreationModifiedDates();
        Bundle bundleDates = new Bundle();
        bundleDates.putLong(Constants.BUNDLE_ITEM_ID_FOR_DATES_FRAGMENT, mNote.getId());
        bundleDates.putSerializable(Constants.BUNDLE_ITEM_TYPE_FOR_DATES_FRAGMENT, Item.Type.NOTE);
        fragmentCreationModifiedDates.setArguments(bundleDates);

        FragmentColourSelection fragmentColourSelection = new FragmentColourSelection();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_CURRENT_COLOUR, mNote.getColour());
        fragmentColourSelection.setArguments(bundle);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_add_edit_note_colour_selection_frame, fragmentColourSelection)
                .replace(R.id.fragment_add_edit_note_dates_frame, fragmentCreationModifiedDates)
                .commit();


        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        mNote.setTitle(mEditTitle.getText().toString());
        mNote.setContent(mEditContent.getText().toString());

        mNote.setLastModifiedDate(new Date().getTime());

        if (mNote.isEmpty()) {
            mNote.setStatus(Item.Status.DELETED);
        }

        mSqlDatabaseHelper.addOrEditNote(mNote);

        mSqlDatabaseHelper.closeDB();
    }

    @Override
    public void onColourSelected(Integer colour) {
        mNote.setColour(colour);
    }

    private void initNote() {
        mSqlDatabaseHelper = new SqlDatabaseHelper(getActivity());

        if (mIsInEditMode) {
            mNote = (Note) mSqlDatabaseHelper.getItemById(
                    getArguments().getLong(Constants.BUNDLE_ITEM_TO_EDIT_ID), Item.Type.NOTE);
            mEditTitle.setText(mNote.getTitle());
            mEditContent.setText(mNote.getContent());
        } else {
            mNote = (Note) mSqlDatabaseHelper.getItemById(
                    mSqlDatabaseHelper.addBlankNote(), Item.Type.NOTE);
        }
    }

}

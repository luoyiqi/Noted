package com.cerebellio.noted;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.models.CheckList;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.Note;
import com.cerebellio.noted.models.events.TitleChangedEvent;
import com.cerebellio.noted.models.listeners.IOnColourSelectedListener;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.TextFunctions;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Allows user to add new {@link CheckList} or edit an existing one
 */
public class FragmentAddEditNote extends FragmentBase
        implements IOnColourSelectedListener {

    @InjectView(R.id.fragment_add_edit_note_content) MaterialEditText mEditContent;

    private static final String LOG_TAG = TextFunctions.makeLogTag(FragmentAddEditNote.class);

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

        ApplicationNoted.bus.post(new TitleChangedEvent(
                mIsInEditMode ? getString(R.string.title_note_edit) : getString(R.string.title_note_new)));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationNoted.bus.register(this);
    }

    @Override
    public void onPause() {

        super.onPause();

        ApplicationNoted.bus.unregister(this);

        //Note has been edited
        if (!mNote.getContent().equals(mEditContent.getText().toString())) {
            mNote.setEditedDate(new Date().getTime());
        }

        mNote.setContent(mEditContent.getText().toString());

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

    /**
     * Carries out initialisation of {@link #mNote}
     * If we're editing, retrieves it from database.
     * If we're adding, insert new {@link Note} into database
     */
    private void initNote() {

        mSqlDatabaseHelper = new SqlDatabaseHelper(getActivity());

        if (mIsInEditMode) {

            //Retrieve Note from database
            mNote = (Note) mSqlDatabaseHelper.getItemById(
                    getArguments().getLong(Constants.BUNDLE_ITEM_TO_EDIT_ID), Item.Type.NOTE);
            mEditContent.setText(mNote.getContent());
        } else {

            mNote = (Note) mSqlDatabaseHelper.getItemById(
                    mSqlDatabaseHelper.addBlankNote(), Item.Type.NOTE);
        }
    }

}

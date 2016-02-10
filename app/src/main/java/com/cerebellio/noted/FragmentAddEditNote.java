package com.cerebellio.noted;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.models.Note;
import com.cerebellio.noted.models.listeners.IOnColourSelectedListener;
import com.cerebellio.noted.utils.Constants;
import com.google.gson.Gson;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_edit_note, container, false);
        ButterKnife.inject(this, rootView);

        mIsInEditMode = getArguments().getBoolean(Constants.BUNDLE_IS_IN_EDIT_MODE);
        
        initNote();

        FragmentColourSelection fragmentColourSelection = new FragmentColourSelection();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_CURRENT_COLOUR, mNote.getColour());
        fragmentColourSelection.setArguments(bundle);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_add_edit_note_colour_selection_frame, fragmentColourSelection)
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

        mNote.setTitle(mEditTitle.getText().toString());
        mNote.setContent(mEditContent.getText().toString());
        mNote.setLastModifiedDate(new Date().getTime());

        if (mNote.isEmpty()) {
            mNote.setIsTrashed(true);
        }

        mSqlDatabaseHelper.addOrEditNote(mNote);

        mSqlDatabaseHelper.closeDB();
    }

    @Override
    public void onColourSelected(Integer colour) {
        mNote.setColour(colour);
    }

    private void initNote() {
        if (mIsInEditMode) {
            mNote = new Gson().fromJson(getArguments().getString(
                    Constants.BUNDLE_NOTE_TO_EDIT_JSON), Note.class);
            mEditTitle.setText(mNote.getTitle());
            mEditContent.setText(mNote.getContent());
        } else {
            mNote = new Note();
        }
    }

}

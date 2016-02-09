package com.cerebellio.noted;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cerebellio.noted.models.Note;
import com.cerebellio.noted.database.SqlDatabaseHelper;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Sam on 09/02/2016.
 */
public class FragmentAddNote extends Fragment {

    @InjectView(R.id.fragment_add_note_title) TextView title;
    @InjectView(R.id.fragment_add_note_content) TextView content;

    private Note mNote;
    private SqlDatabaseHelper mSqlDatabaseHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNote = new Note();
        mNote.setLastModifiedDate(new Date().getTime());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_note, container, false);
        ButterKnife.inject(this, rootView);

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

        mNote.setTitle(title.getText().toString());
        mNote.setContent(content.getText().toString());

        mSqlDatabaseHelper.addNote(mNote);

        mSqlDatabaseHelper.closeDB();
    }
}

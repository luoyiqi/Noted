package com.cerebellio.noted.models.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cerebellio.noted.R;
import com.cerebellio.noted.models.Note;
import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.utils.UtilityFunctions;

import java.util.List;

/**
 * Created by Sam on 09/02/2016.
 */
public class ShowNotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Note> mNotes;
    private Context mContext;

    public ShowNotesAdapter(Context context) {
        SqlDatabaseHelper sqlDatabaseHelper = new SqlDatabaseHelper(context);

        mNotes = sqlDatabaseHelper.getNotes("");

        mContext = context;

        sqlDatabaseHelper.closeDB();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Note note = mNotes.get(position);

        ((ShowNotesAdapterViewHolder) holder).mTextTitle.setText(note.getTitle());
        ((ShowNotesAdapterViewHolder) holder).mTextContent.setText(note.getContent());
        ((ShowNotesAdapterViewHolder) holder).mTextLastModified.setText(
                  mContext.getString(R.string.date_last_modified)
                + " "
                + UtilityFunctions.getDateLastModifiedString(note.getLastModifiedDate()));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ShowNotesAdapterViewHolder(LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.recycler_item_show_notes, parent, false));
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    protected class ShowNotesAdapterViewHolder extends RecyclerView.ViewHolder {

        protected TextView mTextTitle;
        protected TextView mTextContent;
        protected TextView mTextLastModified;

        public ShowNotesAdapterViewHolder(View v) {
            super(v);

            mTextTitle = (TextView) v.findViewById(R.id.recycler_item_show_notes_title);
            mTextContent = (TextView) v.findViewById(R.id.recycler_item_show_notes_content);
            mTextLastModified = (TextView) v.findViewById(R.id.recycler_item_show_notes_last_modified_date);
        }

    }

}

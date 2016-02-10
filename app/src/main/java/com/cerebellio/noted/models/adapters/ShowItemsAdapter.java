package com.cerebellio.noted.models.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cerebellio.noted.ApplicationNoted;
import com.cerebellio.noted.R;
import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.models.CheckList;
import com.cerebellio.noted.models.CheckListItem;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.Note;
import com.cerebellio.noted.utils.TextFunctions;
import com.cerebellio.noted.utils.UtilityFunctions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam on 09/02/2016.
 */
public class ShowItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NOTE = 0;
    private static final int TYPE_CHECKLIST = 1;

    private List<Item> mItems = new ArrayList<>();
    private Context mContext;
    private boolean mIsEnabled = true;

    public ShowItemsAdapter(Context context) {
        SqlDatabaseHelper sqlDatabaseHelper = new SqlDatabaseHelper(context);

        List<Item> allItems = new ArrayList<>();
        allItems.addAll(sqlDatabaseHelper.getAllNotes());
        allItems.addAll(sqlDatabaseHelper.getAllChecklists());

        for (Item item : allItems) {
            if (!item.isEmpty()) {
                mItems.add(item);
            }
        }

        mContext = context;

        sqlDatabaseHelper.closeDB();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            default:
            case TYPE_NOTE:
                Note note = (Note) mItems.get(position);

                ((ShowNotesAdapterViewHolder) holder).mLinearLayoutFrame.setBackgroundColor(note.getColour());
                ((ShowNotesAdapterViewHolder) holder).mTextTitle.setText(note.getTitle());
                ((ShowNotesAdapterViewHolder) holder).mTextContent.setText(note.getContent());
                ((ShowNotesAdapterViewHolder) holder).mTextLastModified.setText(
                        mContext.getString(R.string.date_last_modified)
                                + " "
                                + UtilityFunctions.getDateLastModifiedString(note.getLastModifiedDate()));
                break;
            case TYPE_CHECKLIST:
                CheckList checkList = (CheckList) mItems.get(position);
                ShowChecklistsAdapterViewHolder checklistHolder = (ShowChecklistsAdapterViewHolder) holder;

                checklistHolder.mLinearLayoutFrame.setBackgroundColor(checkList.getColour());
                checklistHolder.mTextTitle.setText(checkList.getTitle());

                for (int i = 0; i < checkList.getItems().size(); i++) {
                    CheckListItem item = checkList.getItems().get(i);

                    //don't draw empty checklist items,
                    //avoid gap at bottom caused by final entry having to be empty
                    if (item.isEmpty()) {
                        continue;
                    }

                    if (i != 0) {
                        checklistHolder.mTextContent.append("\n");
                    }

                    SpannableString content = TextFunctions.convertStringToSpannable(
                            Integer.toString(i + 1)
                                    + ". " + item.getContent());

                    if (item.isCompleted()) {
                        TextFunctions.strikeThrough(content);
                    }

                    checklistHolder.mTextContent.append(content);
                }

                checklistHolder.mTextLastModified.setText(
                        mContext.getString(R.string.date_last_modified)
                                + " "
                                + UtilityFunctions.getDateLastModifiedString(checkList.getLastModifiedDate()));
                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        switch (viewType) {
            default:
            case TYPE_NOTE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_item_show_notes, parent, false);
                return new ShowNotesAdapterViewHolder(itemView);
            case TYPE_CHECKLIST:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_item_show_checklist, parent, false);
                return new ShowChecklistsAdapterViewHolder(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position) instanceof Note ? TYPE_NOTE : TYPE_CHECKLIST;
    }

    public void setEnabled(boolean isEnabled) {
        mIsEnabled = isEnabled;
    }

    public List<Item> getItems() {
        return mItems;
    }

    private class ShowNotesAdapterViewHolder extends RecyclerView.ViewHolder {

        protected CardView mCardView;
        protected LinearLayout mLinearLayoutFrame;
        protected TextView mTextTitle;
        protected TextView mTextContent;
        protected TextView mTextLastModified;

        public ShowNotesAdapterViewHolder(View v) {
            super(v);

            mCardView = (CardView) v.findViewById(R.id.recycler_item_show_notes_card);
            mLinearLayoutFrame = (LinearLayout) v.findViewById(R.id.recycler_item_show_notes_frame);
            mTextTitle = (TextView) v.findViewById(R.id.recycler_item_show_notes_title);
            mTextContent = (TextView) v.findViewById(R.id.recycler_item_show_notes_content);
            mTextLastModified = (TextView) v.findViewById(R.id.recycler_item_show_notes_last_modified_date);

            mCardView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mIsEnabled) {
                                ApplicationNoted.bus.post(mItems.get(getAdapterPosition()));
                            }
                        }
                    });

            mCardView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    MenuItem delete = contextMenu.add("Delete");
                    delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            notifyItemRemoved(getAdapterPosition());
                            return false;
                        }
                    });
                }
            });
        }
    }

    private class ShowChecklistsAdapterViewHolder extends RecyclerView.ViewHolder {

        protected CardView mCardView;
        protected LinearLayout mLinearLayoutFrame;
        protected TextView mTextTitle;
        protected TextView mTextContent;
        protected TextView mTextLastModified;


        public ShowChecklistsAdapterViewHolder(View v) {
            super(v);

            mCardView = (CardView) v.findViewById(R.id.recycler_item_show_checklist_card);
            mLinearLayoutFrame = (LinearLayout) v.findViewById(R.id.recycler_item_show_checklist_frame);
            mTextTitle = (TextView) v.findViewById(R.id.recycler_item_show_checklist_title);
            mTextContent = (TextView) v.findViewById(R.id.recycler_item_show_checklist_items);
            mTextLastModified = (TextView) v.findViewById(R.id.recycler_item_show_checklist_last_modified_date);

            mCardView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mIsEnabled) {
                                ApplicationNoted.bus.post(mItems.get(getAdapterPosition()));
                            }
                        }
                    });

            mCardView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    MenuItem delete = contextMenu.add("Delete");
                    delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            notifyItemRemoved(getAdapterPosition());
                            return false;
                        }
                    });
                }
            });
        }
    }
}

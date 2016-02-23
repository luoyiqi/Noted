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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cerebellio.noted.ApplicationNoted;
import com.cerebellio.noted.R;
import com.cerebellio.noted.async.LazySketchLoader;
import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.models.CheckList;
import com.cerebellio.noted.models.CheckListItem;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.NavDrawerItem;
import com.cerebellio.noted.models.Note;
import com.cerebellio.noted.models.Sketch;
import com.cerebellio.noted.utils.TextFunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Sam on 09/02/2016.
 */
public class ShowItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NOTE = 0;
    private static final int TYPE_CHECKLIST = 1;
    private static final int TYPE_SKETCH = 2;

    private List<Item> mItems = new ArrayList<>();
    private Context mContext;
    private boolean mIsEnabled = true;

    public enum FilterType {
        NONE,
        NOTE,
        CHECKLIST,
        SKETCH
    }

    public ShowItemsAdapter(Context context, FilterType filterType) {
        mContext = context;
        mItems = getFilteredItems(filterType, NavDrawerItem.NavDrawerItemType.PINBOARD);
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

                if (note.getTitle().equals("")) {
                    ((ShowNotesAdapterViewHolder) holder).mTextTitle.setVisibility(View.GONE);
                }

                ((ShowNotesAdapterViewHolder) holder).mCardView.setOnCreateContextMenuListener(new ItemContextMenu(position));

                break;
            case TYPE_CHECKLIST:
                CheckList checkList = (CheckList) mItems.get(position);
                ShowChecklistsAdapterViewHolder checklistHolder = (ShowChecklistsAdapterViewHolder) holder;

                checklistHolder.mLinearLayoutFrame.setBackgroundColor(checkList.getColour());
                checklistHolder.mTextTitle.setText(checkList.getTitle());
                checklistHolder.mTextContent.setText("");

                if (checkList.getTitle().equals("")) {
                    checklistHolder.mTextTitle.setVisibility(View.GONE);
                }

                for (int i = 0; i < checkList.getItems().size(); i++) {
                    CheckListItem item = checkList.getItems().get(i);

                    //don't draw empty checklist items,
                    //avoid gap at bottom caused by final entry having to be empty
                    if (item.isEmpty()) {
                        continue;
                    }

                    if (i != 0) {
                        checklistHolder.mTextContent.append("\n\n");
                    }

                    SpannableString content = TextFunctions.convertStringToSpannable(item.getContent());

                    if (item.isCompleted()) {
                        TextFunctions.strikeThrough(content);
                    }

                    checklistHolder.mTextContent.append(content);
                }

                checklistHolder.mCardView.setOnCreateContextMenuListener(new ItemContextMenu(position));

                break;
            case TYPE_SKETCH:
                Sketch sketch = (Sketch) mItems.get(position);
                ImageView sketchView = ((ShowSketchesAdapterViewHolder) holder).mImageSketch;
                new LazySketchLoader(mContext, sketchView, sketch).execute();

                ((ShowSketchesAdapterViewHolder) holder).mCardView.setOnCreateContextMenuListener(new ItemContextMenu(position));
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
                        .inflate(R.layout.recycler_item_show_note, parent, false);
                return new ShowNotesAdapterViewHolder(itemView);
            case TYPE_CHECKLIST:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_item_show_checklist, parent, false);
                return new ShowChecklistsAdapterViewHolder(itemView);
            case TYPE_SKETCH:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_item_show_sketch, parent, false);
                return new ShowSketchesAdapterViewHolder(itemView);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position) instanceof Note) {
            return TYPE_NOTE;
        } else if (mItems.get(position) instanceof CheckList) {
            return TYPE_CHECKLIST;
        } else {
            return TYPE_SKETCH;
        }
    }

    private List<Item> getFilteredItems(FilterType filterType, NavDrawerItem.NavDrawerItemType type) {
        SqlDatabaseHelper sqlDatabaseHelper = new SqlDatabaseHelper(mContext);

        List<Item> allItems = new ArrayList<>();

        switch (filterType) {
            default:
            case NONE:
                allItems.addAll(sqlDatabaseHelper.getAllItems(type));
                break;
            case NOTE:
                allItems.addAll(sqlDatabaseHelper.getAllNotes(type));
                break;
            case CHECKLIST:
                allItems.addAll(sqlDatabaseHelper.getAllChecklists(type));
                break;
            case SKETCH:
                allItems.addAll(sqlDatabaseHelper.getAllSketches(type));
                break;
        }

        //Sort Items by LastModifiedDate
        Collections.sort(allItems, new Comparator<Item>() {
            @Override
            public int compare(Item item, Item item2) {
                return (int) (item2.getLastModifiedDate() - item.getLastModifiedDate());
            }
        });

        for (Item item : allItems) {
            if (!item.isEmpty()) {
                mItems.add(item);
            }
        }

        sqlDatabaseHelper.closeDB();

        return allItems;
    }

    public void swapFilter(FilterType filterType, NavDrawerItem.NavDrawerItemType type) {
        mItems.clear();
        mItems = getFilteredItems(filterType, type);
        notifyDataSetChanged();
    }

    public void setEnabled(boolean isEnabled) {
        mIsEnabled = isEnabled;
    }

    public void setItemType(NavDrawerItem.NavDrawerItemType type) {
        mItems.clear();
        mItems = getFilteredItems(FilterType.NONE, type);
        notifyDataSetChanged();
    }

    public List<Item> getItems() {
        return mItems;
    }

    private class ShowNotesAdapterViewHolder extends RecyclerView.ViewHolder {

        protected CardView mCardView;
        protected LinearLayout mLinearLayoutFrame;
        protected TextView mTextTitle;
        protected TextView mTextContent;

        public ShowNotesAdapterViewHolder(View v) {
            super(v);

            mCardView = (CardView) v.findViewById(R.id.recycler_item_show_notes_card);
            mLinearLayoutFrame = (LinearLayout) v.findViewById(R.id.recycler_item_show_notes_frame);
            mTextTitle = (TextView) v.findViewById(R.id.recycler_item_show_notes_title);
            mTextContent = (TextView) v.findViewById(R.id.recycler_item_show_notes_content);

            mCardView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mIsEnabled) {
                                ApplicationNoted.bus.post(mItems.get(getAdapterPosition()));
                            }
                        }
                    });
        }
    }

    private class ShowChecklistsAdapterViewHolder extends RecyclerView.ViewHolder {

        protected CardView mCardView;
        protected LinearLayout mLinearLayoutFrame;
        protected TextView mTextTitle;
        protected TextView mTextContent;


        public ShowChecklistsAdapterViewHolder(View v) {
            super(v);

            mCardView = (CardView) v.findViewById(R.id.recycler_item_show_checklist_card);
            mLinearLayoutFrame = (LinearLayout) v.findViewById(R.id.recycler_item_show_checklist_frame);
            mTextTitle = (TextView) v.findViewById(R.id.recycler_item_show_checklist_title);
            mTextContent = (TextView) v.findViewById(R.id.recycler_item_show_checklist_items);

            mCardView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mIsEnabled) {
                                ApplicationNoted.bus.post(mItems.get(getAdapterPosition()));
                            }
                        }
                    });
        }
    }

    private class ShowSketchesAdapterViewHolder extends RecyclerView.ViewHolder {

        protected CardView mCardView;
        protected ImageView mImageSketch;

        public ShowSketchesAdapterViewHolder(View v) {
            super(v);

            mCardView = (CardView) v.findViewById(R.id.recycler_item_show_sketch_card);
            mImageSketch = (ImageView) v.findViewById(R.id.recycler_item_show_sketch_sketch);

            mCardView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mIsEnabled) {
                                ApplicationNoted.bus.post(mItems.get(getAdapterPosition()));
                            }
                        }
                    });
        }
    }

    private class ItemContextMenu implements View.OnCreateContextMenuListener {

        private int mPosition;

        public ItemContextMenu(int position) {
            mPosition = position;
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            final int ID_ARCHIVE = 0;
            final int ID_UNARCHIVE = 1;
            final int ID_TRASH = 2;
            final int ID_UNTRASH = 3;
            final int ID_DELETE = 4;

            MenuItem archive = contextMenu.add(0, ID_ARCHIVE, 0, mContext.getString(R.string.context_menu_archive));
            MenuItem unarchive = contextMenu.add(0, ID_UNARCHIVE, 0, mContext.getString(R.string.context_menu_unarchive));
            MenuItem trash = contextMenu.add(0, ID_TRASH, 0, mContext.getString(R.string.context_menu_trash));
            MenuItem untrash = contextMenu.add(0, ID_UNTRASH, 0, mContext.getString(R.string.context_menu_remove_trash));
            MenuItem delete = contextMenu.add(0, ID_DELETE, 0, mContext.getString(R.string.context_menu_delete));

            switch (mItems.get(mPosition).getStatus()) {
                default:
                case NONE:
                    contextMenu.removeItem(ID_UNARCHIVE);
                    contextMenu.removeItem(ID_UNTRASH);
                    contextMenu.removeItem(ID_DELETE);
                    break;
                case ARCHIVED:
                    contextMenu.removeItem(ID_ARCHIVE);
                    contextMenu.removeItem(ID_UNTRASH);
                    contextMenu.removeItem(ID_DELETE);
                    break;
                case TRASHED:
                    contextMenu.removeItem(ID_UNARCHIVE);
                    contextMenu.removeItem(ID_TRASH);
                    break;
                case DELETED:
                    break;
            }

            archive.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    mItems.get(mPosition).setStatus(Item.Status.ARCHIVED);
                    notifyItemRemoved(mPosition);
                    return false;
                }
            });

            unarchive.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    mItems.get(mPosition).setStatus(Item.Status.NONE);
                    notifyItemRemoved(mPosition);
                    return false;
                }
            });

            trash.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    mItems.get(mPosition).setStatus(Item.Status.TRASHED);
                    notifyItemRemoved(mPosition);
                    return false;
                }
            });

            untrash.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    mItems.get(mPosition).setStatus(Item.Status.NONE);
                    notifyItemRemoved(mPosition);
                    return false;
                }
            });

            delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    mItems.get(mPosition).setStatus(Item.Status.DELETED);
                    notifyItemRemoved(mPosition);
                    return false;
                }
            });
        }
    }
}

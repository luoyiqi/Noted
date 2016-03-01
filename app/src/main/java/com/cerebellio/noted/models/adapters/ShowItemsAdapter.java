package com.cerebellio.noted.models.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cerebellio.noted.ApplicationNoted;
import com.cerebellio.noted.R;
import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.models.CheckList;
import com.cerebellio.noted.models.CheckListItem;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.NavDrawerItem;
import com.cerebellio.noted.models.Note;
import com.cerebellio.noted.models.Sketch;
import com.cerebellio.noted.models.events.ItemWithListPositionEvent;
import com.cerebellio.noted.utils.FeedbackFunctions;
import com.cerebellio.noted.utils.TextFunctions;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Adapter to display {@link Item}
 */
public class ShowItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = TextFunctions.makeLogTag(ShowItemsAdapter.class);

    private static final int TYPE_NOTE = 0;
    private static final int TYPE_CHECKLIST = 1;
    private static final int TYPE_SKETCH = 2;

    private List<Item> mItems = new ArrayList<>();
    private Context mContext;

    private FilterType mFilterType = FilterType.NONE;
    private SortType mSortType = SortType.EDITED_DESC;
    private NavDrawerItem.NavDrawerItemType mNavType = NavDrawerItem.NavDrawerItemType.PINBOARD;

    /**
     * Is item clickable?
     */
    private boolean mIsEnabled = true;

    public enum FilterType {
        NONE,
        NOTE,
        CHECKLIST,
        SKETCH,
        IMPORTANT
    }

    public enum SortType {
        EDITED_ASC,
        EDITED_DESC,
        CREATED_ASC,
        CREATED_DESC,
        TYPE_N_C_S,
        TYPE_S_C_N
    }

    public ShowItemsAdapter(Context context) {
        mContext = context;
        refresh();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            default:
            case TYPE_NOTE:
                Note note = (Note) mItems.get(position);
                ShowNotesAdapterViewHolder notesHolder = ((ShowNotesAdapterViewHolder) holder);

                notesHolder.mTextContent.setBackgroundColor(note.getColour());
                notesHolder.mTextContent.setText(note.getContent());
                break;
            case TYPE_CHECKLIST:
                CheckList checkList = (CheckList) mItems.get(position);
                ShowChecklistsAdapterViewHolder checklistHolder =
                        (ShowChecklistsAdapterViewHolder) holder;

                checklistHolder.mTextContent.setBackgroundColor(checkList.getColour());
                checklistHolder.mTextContent.setText("");

                /**
                 * Loop through every item
                 * Can't itself be shown in RecyclerView because it would be nested,
                 * so we display each {@link CheckListItem} on a separate line in a TextView
                 */
                for (int i = 0; i < checkList.getItems().size(); i++) {
                    CheckListItem item = checkList.getItems().get(i);

                    //Don't draw empty checklist items
                    if (item.isEmpty()) {
                        continue;
                    }

                    //Not first item, so append newline characters
                    if (i != 0) {
                        checklistHolder.mTextContent.append("\n\n");
                    }

                    //Need content to be Spannable so we can strike through it if it is completed
                    SpannableString content = TextFunctions.convertStringToSpannable(item.getContent());

                    if (item.isCompleted()) {
                        TextFunctions.strikeThrough(content);
                    }

                    checklistHolder.mTextContent.append(content);
                }

                break;
            case TYPE_SKETCH:
                Sketch sketch = (Sketch) mItems.get(position);
                ImageView sketchView = ((ShowSketchesAdapterViewHolder) holder).mImageSketch;
                Picasso.with(mContext)
                        .load(new File(sketch.getImagePath()))
                        .fit()
                        .into(sketchView);
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

        //Check type of item so we know which layout to choose
        if (mItems.get(position) instanceof Note) {
            return TYPE_NOTE;
        } else if (mItems.get(position) instanceof CheckList) {
            return TYPE_CHECKLIST;
        } else {
            return TYPE_SKETCH;
        }
    }

    /**
     * Swaps the visible {@link Item} visible depending on
     * {@link #mFilterType}, {@link #mNavType} and {@link #mSortType}
     */
    public void refresh() {
        mItems.clear();

        List<Item> allItems = filterItems();
        sortItems(allItems);

        //Avoid showing blank Notes etc.
        for (Item item : allItems) {
            if (!item.isEmpty()) {
                mItems.add(item);
            }
        }

        notifyDataSetChanged();
    }

    private List<Item> filterItems() {
        SqlDatabaseHelper sqlDatabaseHelper = new SqlDatabaseHelper(mContext);
        List<Item> allItems = new ArrayList<>();

        switch (mFilterType) {
            default:
            case NONE:
                allItems.addAll(sqlDatabaseHelper.getAllItems(mNavType));
                break;
            case NOTE:
                allItems.addAll(sqlDatabaseHelper.getAllNotes(mNavType));
                break;
            case CHECKLIST:
                allItems.addAll(sqlDatabaseHelper.getAllChecklists(mNavType));
                break;
            case SKETCH:
                allItems.addAll(sqlDatabaseHelper.getAllSketches(mNavType));
                break;
            case IMPORTANT:
                allItems.addAll(sqlDatabaseHelper.getImportantItems(mNavType));
        }

        sqlDatabaseHelper.closeDB();
        return allItems;
    }

    private void sortItems(List<Item> allItems) {

        //Items come from database sorted Notes-Checklists-Sketches
        //So if we're sorting by type we either do nothing or reverse the list
        if (mSortType.equals(SortType.TYPE_N_C_S)) {
            return;
        } else if (mSortType.equals(SortType.TYPE_S_C_N)) {
            Collections.reverse(allItems);
            return;
        }

        //Sorting items by some values so we need to use a comparator
        Collections.sort(allItems, new Comparator<Item>() {
            @Override
            public int compare(Item item, Item item2) {
                switch (mSortType) {
                    default:
                    case EDITED_DESC:
                        return (int) (item2.getEditedDate() - item.getEditedDate());
                    case EDITED_ASC:
                        return (int) (item.getEditedDate() - item2.getEditedDate());
                    case CREATED_DESC:
                        return (int) (item2.getCreatedDate() - item.getCreatedDate());
                    case CREATED_ASC:
                        return (int) (item.getCreatedDate() - item2.getCreatedDate());
                }
            }
        });
    }

    /**
     * Allows disabling of items so they cannot be selected
     *
     * @param isEnabled     true if items should not be clickable, false otherwise
     */
    public void setEnabled(boolean isEnabled) {
        mIsEnabled = isEnabled;
    }

    /**
     * Replaced Items with given List
     *
     * @param items         new List
     */
    public void replaceItems(List<Item> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * Remove an item with the given position
     *
     * @param position      position at which to remove item
     */
    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Change the colour of an item
     *
     * @param position          position to change
     * @param newColour         new colour to apply
     */
    public void updateItemColour(int position, int newColour) {
        mItems.get(position).setColour(newColour);
        notifyItemChanged(position);
    }

    public List<Item> getItems() {
        return mItems;
    }

    public NavDrawerItem.NavDrawerItemType getNavType() {
        return mNavType;
    }

    public SortType getSortType() {
        return mSortType;
    }

    public FilterType getFilterType() {
        return mFilterType;
    }

    public void setFilterType(FilterType filterType) {
        mFilterType = filterType;
        refresh();
    }

    public void setSortType(SortType sortType) {
        mSortType = sortType;
        refresh();
    }

    public void setNavType(NavDrawerItem.NavDrawerItemType navType) {
        mNavType = navType;

        //Clear filters and sorts when making type transfer
        mFilterType = FilterType.NONE;
        mSortType = SortType.EDITED_DESC;
        refresh();
    }

    private class ShowNotesAdapterViewHolder extends RecyclerView.ViewHolder {

        protected TextView mTextContent;

        public ShowNotesAdapterViewHolder(final View v) {
            super(v);

            mTextContent = (TextView) v.findViewById(R.id.recycler_item_show_notes_content);

            mTextContent.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mIsEnabled) {
                                ApplicationNoted.bus.post(
                                        new ItemWithListPositionEvent(mItems.get(getAdapterPosition()),
                                                getAdapterPosition()));
                                FeedbackFunctions.vibrate(v);
                            }
                        }
                    });
        }
    }

    private class ShowChecklistsAdapterViewHolder extends RecyclerView.ViewHolder {

        protected TextView mTextContent;


        public ShowChecklistsAdapterViewHolder(final View v) {
            super(v);

            mTextContent = (TextView) v.findViewById(R.id.recycler_item_show_checklist_items);

            mTextContent.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mIsEnabled) {
                                ApplicationNoted.bus.post(
                                        new ItemWithListPositionEvent(mItems.get(getAdapterPosition()),
                                                getAdapterPosition()));
                                FeedbackFunctions.vibrate(v);
                            }
                        }
                    });
        }
    }

    private class ShowSketchesAdapterViewHolder extends RecyclerView.ViewHolder {

        protected ImageView mImageSketch;

        public ShowSketchesAdapterViewHolder(final View v) {
            super(v);

            mImageSketch = (ImageView) v.findViewById(R.id.recycler_item_show_sketch_sketch);

            mImageSketch.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mIsEnabled) {
                                ApplicationNoted.bus.post(
                                        new ItemWithListPositionEvent(mItems.get(getAdapterPosition()),
                                                getAdapterPosition()));
                                FeedbackFunctions.vibrate(v);
                            }
                        }
                    });
        }
    }
}

package com.cerebellio.noted.models.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
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
import com.cerebellio.noted.models.events.ItemWithListPositionEvent;
import com.cerebellio.noted.utils.TextFunctions;

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

    /**
     * Is item clickable?
     */
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
                ShowNotesAdapterViewHolder notesHolder = ((ShowNotesAdapterViewHolder) holder);

                notesHolder.mLinearLayoutFrame.setBackgroundColor(note.getColour());
                notesHolder.mTextContent.setText(note.getContent());
                break;
            case TYPE_CHECKLIST:
                CheckList checkList = (CheckList) mItems.get(position);
                ShowChecklistsAdapterViewHolder checklistHolder =
                        (ShowChecklistsAdapterViewHolder) holder;

                checklistHolder.mLinearLayoutFrame.setBackgroundColor(checkList.getColour());
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
                new LazySketchLoader(mContext, sketchView, sketch).execute();

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
     * Gets a list of {@link Item} from the database
     * with a filter applied
     *
     * @param filterType        i.e. only {@link Note} or only {@link CheckList}
     * @param type              i.e. {@link com.cerebellio.noted.models.NavDrawerItem.NavDrawerItemType#PINBOARD}
     * @return                  filtered List
     */
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

        //Sort Items by Edited Date
        Collections.sort(allItems, new Comparator<Item>() {
            @Override
            public int compare(Item item, Item item2) {
                return (int) (item2.getEditedDate() - item.getEditedDate());
            }
        });

        //Avoid showing blank Notes etc.
        for (Item item : allItems) {
            if (!item.isEmpty()) {
                mItems.add(item);
            }
        }

        sqlDatabaseHelper.closeDB();

        return allItems;
    }

    /**
     * Publicly accessible method to change
     * {@link com.cerebellio.noted.models.adapters.ShowItemsAdapter.FilterType} applied
     *
     * @param filterType        new filter to apply
     * @param type              i.e. {@link com.cerebellio.noted.models.NavDrawerItem.NavDrawerItemType#PINBOARD}
     */
    public void swapFilter(FilterType filterType, NavDrawerItem.NavDrawerItemType type) {
        mItems.clear();
        mItems = getFilteredItems(filterType, type);
        notifyDataSetChanged();
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
     * Change the current {@link com.cerebellio.noted.models.NavDrawerItem.NavDrawerItemType}
     *
     * @param type      new type
     */
    public void setItemType(NavDrawerItem.NavDrawerItemType type) {
        mItems.clear();
        mItems = getFilteredItems(FilterType.NONE, type);
        notifyDataSetChanged();
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

    private class ShowNotesAdapterViewHolder extends RecyclerView.ViewHolder {

        protected CardView mCardView;
        protected LinearLayout mLinearLayoutFrame;
        protected TextView mTextContent;

        public ShowNotesAdapterViewHolder(View v) {

            super(v);

            mCardView = (CardView) v.findViewById(R.id.recycler_item_show_notes_card);
            mLinearLayoutFrame = (LinearLayout) v.findViewById(R.id.recycler_item_show_notes_frame);
            mTextContent = (TextView) v.findViewById(R.id.recycler_item_show_notes_content);

            mCardView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mIsEnabled) {
                                ApplicationNoted.bus.post(
                                        new ItemWithListPositionEvent(mItems.get(getAdapterPosition()),
                                                getAdapterPosition()));
                            }
                        }
                    });
        }
    }

    private class ShowChecklistsAdapterViewHolder extends RecyclerView.ViewHolder {

        protected CardView mCardView;
        protected LinearLayout mLinearLayoutFrame;
        protected TextView mTextContent;


        public ShowChecklistsAdapterViewHolder(View v) {

            super(v);

            mCardView = (CardView) v.findViewById(R.id.recycler_item_show_checklist_card);
            mLinearLayoutFrame = (LinearLayout) v.findViewById(R.id.recycler_item_show_checklist_frame);
            mTextContent = (TextView) v.findViewById(R.id.recycler_item_show_checklist_items);

            mCardView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mIsEnabled) {
                                ApplicationNoted.bus.post(
                                        new ItemWithListPositionEvent(mItems.get(getAdapterPosition()),
                                                getAdapterPosition()));
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
                                ApplicationNoted.bus.post(
                                        new ItemWithListPositionEvent(mItems.get(getAdapterPosition()),
                                                getAdapterPosition()));
                            }
                        }
                    });
        }
    }
}

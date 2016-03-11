package com.cerebellio.noted.models.adapters;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.cerebellio.noted.R;
import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.models.CheckList;
import com.cerebellio.noted.models.CheckListItem;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.listeners.IOnStartDragListener;
import com.cerebellio.noted.helpers.PreferenceHelper;
import com.cerebellio.noted.utils.TextFunctions;
import com.cerebellio.noted.views.FilteredIconView;

import java.util.Collections;
import java.util.List;

/**
 * Adapter to display {@link CheckListItem}
 */
public class ChecklistItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = TextFunctions.makeLogTag(ChecklistItemsAdapter.class);

    private final IOnStartDragListener mIOnStartDragListener;

    private CheckList mCheckList;
    private List<CheckListItem> mItems;
    private Context mContext;

    public ChecklistItemsAdapter(
            CheckList checkList, Context context, IOnStartDragListener iOnStartDragListener) {
        mCheckList = checkList;
        mItems = checkList.getItems();
        mContext = context;
        mIOnStartDragListener = iOnStartDragListener;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        CheckListItem item = mItems.get(position);
        final ChecklistItemsAdapterViewHolder viewHolder = ((ChecklistItemsAdapterViewHolder) holder);

        viewHolder.mEditContent.setText(item.getContent());
        viewHolder.mCheckCompleted.setChecked(item.isCompleted());

        //Don't want to be able to drag or mark as completed an empty item
        viewHolder.mDrag.setVisibility(item.isEmpty() ? View.INVISIBLE : View.VISIBLE);
        viewHolder.mCheckCompleted.setVisibility(item.isEmpty() ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_checklist_items, parent, false);

        return new ChecklistItemsAdapterViewHolder(itemView);

    }

    public List<CheckListItem> getItems() {
        return mItems;
    }

    public void swap(int sourcePosition, int targetPosition)  {
        //Don't want to be able to swap final item
        if (sourcePosition == mItems.size() - 1 || targetPosition == mItems.size() - 1) {
            return;
        }

        Collections.swap(mItems, sourcePosition, targetPosition);

        notifyItemMoved(sourcePosition, targetPosition);
    }

    public void remove(int position) {
        //Don't want to delete the final item because user couldn't enter new item
        if (position == mItems.size() - 1) {
            return;
        }

        mItems.get(position).setStatus(Item.Status.DELETED);
        notifyItemRemoved(position);
    }

    public class ChecklistItemsAdapterViewHolder extends RecyclerView.ViewHolder {

        protected LinearLayout mLinearLayoutFrame;
        protected FilteredIconView mDrag;
        protected EditText mEditContent;
        protected CheckBox mCheckCompleted;

        public ChecklistItemsAdapterViewHolder(View v) {

            super(v);

            mLinearLayoutFrame = (LinearLayout) v.findViewById(R.id.recycler_item_checklist_items_frame);
            mDrag = (FilteredIconView) v.findViewById(R.id.recycler_item_checklist_items_drag);
            mEditContent = (EditText) v.findViewById(R.id.recycler_item_checklist_items_content);
            mCheckCompleted = (CheckBox) v.findViewById(R.id.recycler_item_checklist_items_completed);

            mCheckCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    mItems.get(getAdapterPosition()).setIsCompleted(isChecked);

                    if (isChecked) {
                        if (PreferenceHelper.getPrefBehaviourDeleteChecked(mContext)) {
                            remove(getAdapterPosition());
                        }
                    }
                }
            });

            mEditContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    mItems.get(getAdapterPosition()).setContent(mEditContent.getText().toString());

                    //Remove item if it becomes empty and is not last in list
                    //posting from Handler to circumvent binding issues
                    Handler handler = new Handler();
                    final Runnable r = new Runnable() {
                        public void run() {

                            if (mItems.get(getAdapterPosition()).isEmpty()) {

                                //If current item is has been made empty
                                //and it's not the final item, delete the
                                //item so we don't have two '+ New Item' items
                                if (getAdapterPosition() != mItems.size() - 1) {
                                    mItems.get(getAdapterPosition()).setStatus(Item.Status.DELETED);
                                    notifyItemRemoved(getAdapterPosition());
                                }

                            }

                            if (mCheckList.isNewItemNeeded()) {
                                SqlDatabaseHelper databaseHelper = new SqlDatabaseHelper(mContext);

                                mCheckList.addItem((CheckListItem) databaseHelper.getItemById(
                                        databaseHelper.addBlankChecklistItem(mCheckList.getId()), Item.Type.CHECKLIST_ITEM));

                                notifyItemInserted(getAdapterPosition() + 1);

                                databaseHelper.closeDB();
                            }
                        }
                    };
                    handler.post(r);

                    //Can't mark as completed or remove if current item is empty
                    mDrag.setVisibility(
                            mItems.get(getAdapterPosition()).isEmpty() ? View.INVISIBLE : View.VISIBLE);
                    mCheckCompleted.setVisibility(
                            mItems.get(getAdapterPosition()).isEmpty() ? View.INVISIBLE : View.VISIBLE);
                }
            });


            mDrag.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                        mIOnStartDragListener.onStartDrag(ChecklistItemsAdapterViewHolder.this);
                    }
                    return false;
                }
            });


        }
    }

}

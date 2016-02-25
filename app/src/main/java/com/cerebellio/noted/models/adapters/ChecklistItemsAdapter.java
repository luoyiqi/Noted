package com.cerebellio.noted.models.adapters;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cerebellio.noted.ApplicationNoted;
import com.cerebellio.noted.R;
import com.cerebellio.noted.models.CheckList;
import com.cerebellio.noted.models.CheckListItem;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.events.ChecklistItemEditedEvent;
import com.cerebellio.noted.utils.TextFunctions;

import java.util.List;

/**
 * Adapter to display {@link CheckListItem}
 */
public class ChecklistItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = TextFunctions.makeLogTag(ChecklistItemsAdapter.class);

    private CheckList mCheckList;
    private List<CheckListItem> mItems;

    public ChecklistItemsAdapter(CheckList checkList) {

        mCheckList = checkList;
        mItems = checkList.getItems();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        CheckListItem item = mItems.get(position);

        ((ChecklistItemsAdapterViewHolder) holder).mEditContent.setText(item.getContent());
        ((ChecklistItemsAdapterViewHolder) holder).mCheckCompleted.setChecked(item.isCompleted());

        //Don't want to be able to remove or mark as completed an empty item
        ((ChecklistItemsAdapterViewHolder) holder).mTextRemove.setVisibility(
                item.isEmpty() ? View.INVISIBLE : View.VISIBLE);
        ((ChecklistItemsAdapterViewHolder) holder).mCheckCompleted.setVisibility(
                item.isEmpty() ? View.INVISIBLE : View.VISIBLE);
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

    public class ChecklistItemsAdapterViewHolder extends RecyclerView.ViewHolder {

        protected LinearLayout mLinearLayoutFrame;
        protected CheckBox mCheckCompleted;
        protected EditText mEditContent;
        protected TextView mTextRemove;

        public ChecklistItemsAdapterViewHolder(View v) {

            super(v);

            mLinearLayoutFrame = (LinearLayout) v.findViewById(R.id.recycler_item_checklist_items_frame);
            mCheckCompleted = (CheckBox) v.findViewById(R.id.recycler_item_checklist_items_completed);
            mEditContent = (EditText) v.findViewById(R.id.recycler_item_checklist_items_content);
            mTextRemove = (TextView) v.findViewById(R.id.recycler_item_checklist_items_remove);

            mCheckCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    mItems.get(getAdapterPosition()).setIsCompleted(isChecked);
                }
            });

            mTextRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItems.get(getAdapterPosition()).setStatus(Item.Status.DELETED);
                    notifyItemRemoved(getAdapterPosition());
                }
            });

            mEditContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    ApplicationNoted.bus.post(new ChecklistItemEditedEvent());
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    mItems.get(getAdapterPosition()).setContent(mEditContent.getText().toString());

                    //Remove item if it becomes empty and is not last in list
                    //posting from Handler to circumvent binding issues
                    Handler handler = new Handler();
                    final Runnable r = new Runnable() {
                        public void run() {
                            if (mItems.get(getAdapterPosition()).isEmpty() && getAdapterPosition() != getItemCount() - 1) {
                                notifyItemRemoved(getAdapterPosition());
                            }

                            if (mCheckList.isNewItemNeeded()) {
                                mCheckList.addItem();
                                notifyItemInserted(getAdapterPosition() + 1);
                            }
                        }
                    };
                    handler.post(r);

                    //Can't mark as completed or remove if current item is empty
                    mTextRemove.setVisibility(
                            mItems.get(getAdapterPosition()).isEmpty() ? View.INVISIBLE : View.VISIBLE);
                    mCheckCompleted.setVisibility(
                            mItems.get(getAdapterPosition()).isEmpty() ? View.INVISIBLE : View.VISIBLE);


                }
            });

        }
    }

}

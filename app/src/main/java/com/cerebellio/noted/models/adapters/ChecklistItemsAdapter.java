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

import com.cerebellio.noted.R;
import com.cerebellio.noted.models.Checklist;
import com.cerebellio.noted.models.ChecklistItem;
import com.cerebellio.noted.models.Item;

import java.util.List;

/**
 * Created by Sam on 21/01/2016.
 */
public class ChecklistItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Checklist mChecklist;
    private List<ChecklistItem> mItems;

    public ChecklistItemsAdapter(Checklist checklist) {
        mChecklist = checklist;
        mItems = checklist.getItems();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChecklistItem item = mItems.get(position);

        ((ChecklistItemsAdapterViewHolder) holder).mEditContent.setText(item.getContent());
        ((ChecklistItemsAdapterViewHolder) holder).mCheckCompleted.setChecked(item.isCompleted());

        //don't want to be able to remove empty item
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

    public List<ChecklistItem> getItems() {
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

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    mItems.get(getAdapterPosition()).setContent(mEditContent.getText().toString());

                    //remove item if it becomes empty and is not last in list
                    //posting from Handler to circumvent binding issues
                    Handler handler = new Handler();
                    final Runnable r = new Runnable() {
                        public void run() {
                            if (mItems.get(getAdapterPosition()).isEmpty() && getAdapterPosition() != getItemCount() - 1) {
                                notifyItemRemoved(getAdapterPosition());
                            }

                            if (mChecklist.isNewItemNeeded()) {
                                mChecklist.addItem();
                                notifyItemInserted(getAdapterPosition() + 1);
                            }
                        }
                    };
                    handler.post(r);

                    mTextRemove.setVisibility(
                            mItems.get(getAdapterPosition()).isEmpty() ? View.INVISIBLE : View.VISIBLE);
                    mCheckCompleted.setVisibility(
                            mItems.get(getAdapterPosition()).isEmpty() ? View.INVISIBLE : View.VISIBLE);


                }
            });

        }
    }

}

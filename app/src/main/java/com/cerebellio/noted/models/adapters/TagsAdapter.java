package com.cerebellio.noted.models.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cerebellio.noted.ApplicationNoted;
import com.cerebellio.noted.R;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.events.TagEvent;
import com.cerebellio.noted.utils.TextFunctions;
import com.cerebellio.noted.utils.UtilityFunctions;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to display tags
 */
public class TagsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = TextFunctions.makeLogTag(TagsAdapter.class);

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_ADD = 1;
    private static final int MAX_TAG_LENGTH = 8;

    private Context mContext;
    private List<String> mTags = new ArrayList<>();

    public TagsAdapter(Context context, String tagString) {
        mContext = context;
        mTags.addAll(TextFunctions.splitStringToList(tagString, Item.TAG_STRING_SEPARATOR));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        TagsAdapterViewHolder viewHolder = ((TagsAdapterViewHolder) holder);

        if (getItemViewType(position) == TYPE_ADD) {
            viewHolder.mTextTag.setText(mContext.getString(R.string.tag_new));
            ((GradientDrawable) viewHolder.mTextTag.getBackground()).setColor(
                    ContextCompat.getColor(mContext,
                            UtilityFunctions.getResIdFromAttribute(R.attr.colorAccent, mContext)));
        } else {
            String tag = mTags.get(position);
            viewHolder.mTextTag.setText(TextFunctions.createTagString(tag, MAX_TAG_LENGTH));

            ((GradientDrawable) viewHolder.mTextTag.getBackground()).setColor(
                    ContextCompat.getColor(mContext,
                            UtilityFunctions.getResIdFromAttribute(R.attr.colorTag, mContext)));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TagsAdapterViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_tags, parent, false), viewType);
    }

    @Override
    public int getItemViewType(int position) {
        //Final position should be 'Add new tag' button
        return position == mTags.size() ? TYPE_ADD : TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mTags.size() + 1;
    }

    /**
     * Change the list of tags displayed
     * @param tagString     new separated String of tags
     */
    public void setTagsList(String tagString) {
        mTags.clear();
        mTags.addAll(TextFunctions.splitStringToList(tagString, Item.TAG_STRING_SEPARATOR));
    }

    private class TagsAdapterViewHolder extends RecyclerView.ViewHolder {

        protected TextView mTextTag;

        public TagsAdapterViewHolder(View v, int viewType) {

            super(v);

            mTextTag = (TextView) v.findViewById(R.id.recycler_item_tag_value);

            if (viewType == TYPE_ADD) {
                //TagEvent to event bus for adding
                mTextTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ApplicationNoted.bus.post(new TagEvent(TagEvent.Type.ADD));
                    }
                });
            } else {
                //TagEvent to event bus for edit, along with new tag value
                mTextTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ApplicationNoted.bus.post(
                                new TagEvent(TagEvent.Type.EDIT, mTags.get(getAdapterPosition())));
                    }
                });
            }
        }

    }

}

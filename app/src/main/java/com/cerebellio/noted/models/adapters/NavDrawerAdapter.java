package com.cerebellio.noted.models.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cerebellio.noted.ApplicationNoted;
import com.cerebellio.noted.R;
import com.cerebellio.noted.models.NavDrawerItem;
import com.cerebellio.noted.utils.UtilityFunctions;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Sam on 11/02/2016.
 */
public class NavDrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<NavDrawerItem> mItems;
    private NavDrawerItem.NavDrawerItemType mCurrentlyHighlighted = NavDrawerItem.NavDrawerItemType.PINBOARD;
    private Context mContext;

    private static final NavDrawerItem.NavDrawerItemType[] HIGHLIGHTABLE_TYPES = {
            NavDrawerItem.NavDrawerItemType.PINBOARD,
            NavDrawerItem.NavDrawerItemType.ARCHIVE,
            NavDrawerItem.NavDrawerItemType.TRASH
    };

    public NavDrawerAdapter(List<NavDrawerItem> items, Context context) {
        mItems = items;
        mContext = context;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:

                break;
            case TYPE_ITEM:
                NavDrawerItem item = mItems.get(position - 1);

                ((NavDrawerAdapterViewHolder) holder).mTextTitle.setText(item.getTitle());
                ((NavDrawerAdapterViewHolder) holder).mImageIcon.setImageResource(item.getIconId());

                if (item.getType().equals(mCurrentlyHighlighted)) {
                    ((NavDrawerAdapterViewHolder) holder).mTextTitle.setTextColor(
                            ContextCompat.getColor(mContext, UtilityFunctions.getResIdFromAttribute(R.attr.colorAccent, mContext)));
                } else {
                    ((NavDrawerAdapterViewHolder) holder).mTextTitle.setTextColor(
                            ContextCompat.getColor(mContext, UtilityFunctions.getResIdFromAttribute(R.attr.textColorTertiary, mContext)));
                }
                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new NavDrawerAdapterViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.header_nav_drawer, parent, false), TYPE_HEADER);
            default:
            case TYPE_ITEM:
                return new NavDrawerAdapterViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.recycler_item_nav_drawer, parent, false), TYPE_ITEM);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    private class NavDrawerAdapterViewHolder extends RecyclerView.ViewHolder {

        protected LinearLayout mContainer;
        protected ImageView mImageIcon;
        protected TextView mTextTitle;

        public NavDrawerAdapterViewHolder(View v, int viewType) {
            super(v);

            if (viewType == TYPE_HEADER) {

            } else {
                mContainer = (LinearLayout) v.findViewById(R.id.recycler_item_nav_drawer_container);
                mImageIcon = (ImageView) v.findViewById(R.id.recycler_item_nav_drawer_icon);
                mTextTitle = (TextView) v.findViewById(R.id.recycler_item_nav_drawer_title);

                mContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //offset for header
                        NavDrawerItem.NavDrawerItemType type = mItems.get(getAdapterPosition() - 1).getType();
                        ApplicationNoted.bus.post(type);

                        //check if type selected is highlight-able
                        //i.e. it should be highlighted as current view in nav drawer
                        if (Arrays.asList(HIGHLIGHTABLE_TYPES).contains(type)) {
                            mCurrentlyHighlighted = type;
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        }
    }
}

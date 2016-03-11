package com.cerebellio.noted.models.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cerebellio.noted.ApplicationNoted;
import com.cerebellio.noted.R;
import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.helpers.PreferenceHelper;
import com.cerebellio.noted.models.NavDrawerItem;
import com.cerebellio.noted.models.events.NavDrawerItemTypeSelectedEvent;
import com.cerebellio.noted.utils.TextFunctions;
import com.cerebellio.noted.utils.UtilityFunctions;
import com.cerebellio.noted.views.FilteredIconView;

import java.util.Arrays;
import java.util.List;

/**
 * Adapter to display {@link NavDrawerItem}
 */
public class NavDrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = TextFunctions.makeLogTag(NavDrawerAdapter.class);

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<NavDrawerItem> mItems;
    private NavDrawerItem.NavDrawerItemType mCurrentlyHighlighted = NavDrawerItem.NavDrawerItemType.PINBOARD;
    private Context mContext;

    //Items marked with these types can be selected
    private static final NavDrawerItem.NavDrawerItemType[] HIGHLIGHTABLE_TYPES = {
            NavDrawerItem.NavDrawerItemType.PINBOARD,
            NavDrawerItem.NavDrawerItemType.ARCHIVE,
    };

    public NavDrawerAdapter(List<NavDrawerItem> items, Context context) {

        mItems = items;
        mContext = context;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                //Static layout for header so no code needed
                break;
            case TYPE_ITEM:
                //Account for header
                NavDrawerItem item = mItems.get(position - 1);

                ((NavDrawerAdapterViewHolder) holder).mTextTitle.setText(item.getTitle());

                //Display counts next to nav drawer types?
                //i.e. Pinboard (5)
                if (PreferenceHelper.getPrefDisplayTypeCounts(mContext)) {
                    int count = 0;

                    if (NavDrawerItem.NavDrawerItemType.isSelectable(item.getType())) {
                        SqlDatabaseHelper sqlDatabaseHelper = new SqlDatabaseHelper(mContext);
                        count += sqlDatabaseHelper.getTypeCount(item.getType());
                        sqlDatabaseHelper.closeDB();
                    }

                    String countText = count == 0 ? "" : (" (" + Integer.toString(count) + ")");
                    ((NavDrawerAdapterViewHolder) holder).mTextTitle.append(countText);
                }

                ((NavDrawerAdapterViewHolder) holder).mImageIcon.setImageResource(item.getIconId());
                ((NavDrawerAdapterViewHolder) holder).mDivider.setVisibility(item.isDividerNeeded() ? View.VISIBLE : View.GONE);

                if (item.getType().equals(mCurrentlyHighlighted)) {

                    //This item needs to be highlighted
                    ((NavDrawerAdapterViewHolder) holder).mTextTitle.setTextColor(
                            ContextCompat.getColor(mContext, UtilityFunctions.getResIdFromAttribute(R.attr.colorPrimary, mContext)));
                    ((NavDrawerAdapterViewHolder) holder).mImageIcon.setFilter(
                            ContextCompat.getColor(mContext,
                                    UtilityFunctions.getResIdFromAttribute(R.attr.colorPrimary, mContext)));
                } else {

                    //This item doesn't need to be highlighted
                    ((NavDrawerAdapterViewHolder) holder).mTextTitle.setTextColor(
                            ContextCompat.getColor(mContext, UtilityFunctions.getResIdFromAttribute(R.attr.textColorTertiary, mContext)));
                    ((NavDrawerAdapterViewHolder) holder).mImageIcon.setFilterToDefault();
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
        //First item is the header
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    private class NavDrawerAdapterViewHolder extends RecyclerView.ViewHolder {

        protected LinearLayout mContainer;
        protected View mDivider;
        protected FilteredIconView mImageIcon;
        protected TextView mTextTitle;

        public NavDrawerAdapterViewHolder(View v, int viewType) {

            super(v);

            if (viewType == TYPE_ITEM) {

                mContainer = (LinearLayout) v.findViewById(R.id.recycler_item_nav_drawer_container);
                mDivider = v.findViewById(R.id.recycler_item_nav_drawer_divider);
                mImageIcon = (FilteredIconView) v.findViewById(R.id.recycler_item_nav_drawer_icon);
                mTextTitle = (TextView) v.findViewById(R.id.recycler_item_nav_drawer_title);

                mContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Offset for header
                        NavDrawerItem.NavDrawerItemType type = mItems.get(getAdapterPosition() - 1).getType();

                        //Post type to event bus
                        ApplicationNoted.bus.post(new NavDrawerItemTypeSelectedEvent(type));

                        //Check if type selected is highlight-able
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

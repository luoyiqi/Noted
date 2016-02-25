package com.cerebellio.noted;

import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.events.ItemWithListPositionEvent;
import com.cerebellio.noted.models.NavDrawerItem;
import com.cerebellio.noted.models.adapters.ShowItemsAdapter;
import com.cerebellio.noted.models.listeners.IOnFloatingActionMenuOptionClickedListener;
import com.cerebellio.noted.models.listeners.IOnItemFocusNeedsUpdatingListener;
import com.cerebellio.noted.models.listeners.IOnItemSelectedToEditListener;
import com.cerebellio.noted.utils.ColourFunctions;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.FabShowHideRecyclerScroll;
import com.cerebellio.noted.utils.UtilityFunctions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Sam on 09/02/2016.
 */
public class FragmentShowItems extends Fragment implements IOnItemSelectedToEditListener, IOnItemFocusNeedsUpdatingListener {

    @InjectView(R.id.fragment_show_items_recycler) RecyclerView mItemsRecycler;
    @InjectView(R.id.fragment_show_items_floating_action_menu) FloatingActionsMenu mFloatingActionsMenu;
    @InjectView(R.id.fragnent_show_items_floating_actions_menu_create_note) FloatingActionButton mCreateNote;
    @InjectView(R.id.fragnent_show_items_floating_actions_menu_create_checklist) FloatingActionButton mCreateChecklist;
    @InjectView(R.id.fragnent_show_items_floating_actions_menu_create_sketch) FloatingActionButton mCreateSketch;
    @InjectView(R.id.fragment_show_items_empty) TextView mTextEmpty;
    @InjectView(R.id.fragment_show_items_overlay) View mOverlay;

    private static final int NUM_COLUMNS_PORTRAIT = 2;
    private static final int NUM_COLUMNS_LANDSCAPE = 4;

    private IOnFloatingActionMenuOptionClickedListener mIOnFloatingActionMenuOptionClickedListener;
    private IOnItemSelectedToEditListener mIOnItemSelectedToEditListener;
    private ShowItemsAdapter mAdapter;
    private ShowItemsAdapter.FilterType mFilterType = ShowItemsAdapter.FilterType.NONE;
    private NavDrawerItem.NavDrawerItemType mCurrentType = NavDrawerItem.NavDrawerItemType.PINBOARD;

    private boolean mIsFabMenuVisible = true;
    private SqlDatabaseHelper mSqlDatabaseHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_show_items, container, false);
        ButterKnife.inject(this, rootView);

        //Get background colour for current theme and set the alpha opacity to almost full,
        //which gives the effect of covering the main RecyclerView when FAB pressed
        int backColourId = ContextCompat.getColor(getActivity(), UtilityFunctions.getResIdFromAttribute(R.attr.windowBackground, getActivity()));
        mOverlay.setBackgroundColor(ColourFunctions.adjustAlpha(backColourId, 245));

        mFloatingActionsMenu.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mFloatingActionsMenu.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fab_grow));
                mFloatingActionsMenu.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        mAdapter = new ShowItemsAdapter(getActivity(), mFilterType);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                for (int i = positionStart; i < positionStart + itemCount; i++) {
                    Item item = mAdapter.getItems().get(i);

                    mSqlDatabaseHelper.addOrEditItem(item);

                    mAdapter.getItems().remove(i);
                }

                // TODO: 11/02/2016 Discover why this is needed to refresh Recyclerview on last item removed
                if (mAdapter.getItems().size() == 0) {
                    mItemsRecycler.smoothScrollToPosition(0);
                }

                toggleEmptyText();

                super.onItemRangeRemoved(positionStart, itemCount);
            }
        });

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                toggleEmptyText();
                super.onItemRangeInserted(positionStart, itemCount);
            }
        });

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            UtilityFunctions.setUpStaggeredGridRecycler(mItemsRecycler, mAdapter, NUM_COLUMNS_LANDSCAPE);
        } else {
            UtilityFunctions.setUpStaggeredGridRecycler(mItemsRecycler, mAdapter, NUM_COLUMNS_PORTRAIT);
        }

        toggleEmptyText();

        mCreateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_shrink);
                mOverlay.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shrink_into_bottom_right));
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mIOnFloatingActionMenuOptionClickedListener.OnFabCreateItemClick(Item.Type.NOTE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mFloatingActionsMenu.startAnimation(animation);
            }
        });

        mCreateChecklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_shrink);
                mOverlay.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shrink_into_bottom_right));
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mIOnFloatingActionMenuOptionClickedListener.OnFabCreateItemClick(Item.Type.CHECKLIST);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mFloatingActionsMenu.startAnimation(animation);
            }
        });

        mCreateSketch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_shrink);
                mOverlay.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shrink_into_bottom_right));
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mIOnFloatingActionMenuOptionClickedListener.OnFabCreateItemClick(Item.Type.SKETCH);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mFloatingActionsMenu.startAnimation(animation);
            }
        });

        mFloatingActionsMenu.setOnFloatingActionsMenuUpdateListener(
                new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                    @Override
                    public void onMenuExpanded() {
                        mOverlay.setVisibility(View.VISIBLE);
                        mOverlay.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.grow_from_bottom_right));
                        mAdapter.setEnabled(false);
                        mItemsRecycler.setLayoutFrozen(true);
                    }

                    @Override
                    public void onMenuCollapsed() {
                        mOverlay.setVisibility(View.GONE);
                        mOverlay.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shrink_into_bottom_right));
                        mAdapter.setEnabled(true);
                        mItemsRecycler.setLayoutFrozen(false);
                    }
                });

        mItemsRecycler.addOnScrollListener(new FabShowHideRecyclerScroll() {
            @Override
            public void hide() {
                mFloatingActionsMenu
                        .animate()
                        .translationY(mFloatingActionsMenu.getHeight() + getResources().getDimension(R.dimen.size_medium))
                        .setInterpolator(new AccelerateInterpolator(2))
                        .start();
                mIsFabMenuVisible = false;
            }

            @Override
            public void show() {
                mFloatingActionsMenu
                        .animate()
                        .translationY(0)
                        .setInterpolator(new DecelerateInterpolator(2))
                        .start();
                mIsFabMenuVisible = false;
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_fragment_show_items, menu);

        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView =
                (android.support.v7.widget.SearchView) menu.findItem(R.id.menu_action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    setListToCurrentType();
                } else {
                    searchChanged(newText);
                    toggleEmptyText();
                    mItemsRecycler.smoothScrollToPosition(0);
                }

                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                setListToCurrentType();
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_action_item_filter_submenu_none) {
            mAdapter.swapFilter(ShowItemsAdapter.FilterType.NONE, mCurrentType);
        } else if (item.getItemId() == R.id.menu_action_item_filter_submenu_note) {
            mAdapter.swapFilter(ShowItemsAdapter.FilterType.NOTE, mCurrentType);
        }  else if (item.getItemId() == R.id.menu_action_item_filter_submenu_checklist) {
            mAdapter.swapFilter(ShowItemsAdapter.FilterType.CHECKLIST, mCurrentType);
        }  else if (item.getItemId() == R.id.menu_action_item_filter_submenu_sketch) {
            mAdapter.swapFilter(ShowItemsAdapter.FilterType.SKETCH, mCurrentType);
        }

        item.setChecked(true);

        toggleEmptyText();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationNoted.bus.register(this);
        mSqlDatabaseHelper = new SqlDatabaseHelper(getActivity());
        mFloatingActionsMenu.collapse();
        mFloatingActionsMenu.startAnimation(AnimationUtils.loadAnimation(
                getActivity(), R.anim.fab_grow));
    }

    @Override
    public void onPause() {
        super.onPause();
        ApplicationNoted.bus.unregister(this);
        mSqlDatabaseHelper.closeDB();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mIOnFloatingActionMenuOptionClickedListener = (IOnFloatingActionMenuOptionClickedListener) context;
            mIOnItemSelectedToEditListener = (IOnItemSelectedToEditListener) context;
        } catch (ClassCastException e) {
            Log.e("Listener Error", "Calling Activity does not implement listener");
        }
    }

    @Override
    public void onItemToEdit(Item item) {
        mIOnItemSelectedToEditListener.onItemToEdit(item);
    }

    @Override
    public void onRemove(int position) {
        mAdapter.removeItem(position);
    }

    @Override
    public void onUpdateColour(int position, int newColour) {
        mAdapter.updateItemColour(position, newColour);
    }

    @Subscribe
    public void onItemToFocus(ItemWithListPositionEvent itemWithListPositionEvent) {
        DialogItemFocus dialogItemFocus = new DialogItemFocus();
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.BUNDLE_ITEM_ID, itemWithListPositionEvent.getItem().getId());
        bundle.putInt(Constants.BUNDLE_ITEM_POSITION, itemWithListPositionEvent.getPosition());
        bundle.putSerializable(Constants.BUNDLE_ITEM_TYPE, itemWithListPositionEvent.getItem().getItemType());
        dialogItemFocus.setArguments(bundle);
        dialogItemFocus.show(getChildFragmentManager(), null);
    }

    /**
     * If the RecyclerView has anything to show,
     * hide message telling user it is empty and vice versa.
     */
    @UiThread
    private void toggleEmptyText() {
        if (!mIsFabMenuVisible) {
            mFloatingActionsMenu
                    .animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(2))
                    .start();
        }

        if (mAdapter.getItemCount() == 0) {
            mTextEmpty.setVisibility(View.VISIBLE);
            mItemsRecycler.setVisibility(View.GONE);
        } else {
            mTextEmpty.setVisibility(View.GONE);
            mItemsRecycler.setVisibility(View.VISIBLE);
        }
    }

    public void searchChanged(String searchQuery) {
        mAdapter.replaceItems(mSqlDatabaseHelper.searchItems(searchQuery, mCurrentType));
    }

    public void setItemType(NavDrawerItem.NavDrawerItemType type) {
        mCurrentType = type;
        setListToCurrentType();
    }


    private void setListToCurrentType() {
        mAdapter.setItemType(mCurrentType);
        toggleEmptyText();
        mItemsRecycler.smoothScrollToPosition(0);
    }
}

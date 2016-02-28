package com.cerebellio.noted;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.view.MenuItemCompat;
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
import com.cerebellio.noted.models.NavDrawerItem;
import com.cerebellio.noted.models.adapters.ShowItemsAdapter;
import com.cerebellio.noted.models.events.ItemWithListPositionEvent;
import com.cerebellio.noted.models.events.TitleChangedEvent;
import com.cerebellio.noted.models.listeners.IOnFloatingActionMenuOptionClickedListener;
import com.cerebellio.noted.models.listeners.IOnItemFocusNeedsUpdatingListener;
import com.cerebellio.noted.models.listeners.IOnItemSelectedToEditListener;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.FabShowHideRecyclerScroll;
import com.cerebellio.noted.utils.UtilityFunctions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Shows Items retrieved from the database
 */
public class FragmentShowItems extends FragmentBase
        implements IOnItemSelectedToEditListener, IOnItemFocusNeedsUpdatingListener {

    @InjectView(R.id.fragment_show_items_recycler) RecyclerView mItemsRecycler;
    @InjectView(R.id.fragment_show_items_floating_action_menu) FloatingActionsMenu mFloatingActionsMenu;
    @InjectView(R.id.fragnent_show_items_floating_actions_menu_create_note) FloatingActionButton mCreateNote;
    @InjectView(R.id.fragnent_show_items_floating_actions_menu_create_checklist) FloatingActionButton mCreateChecklist;
    @InjectView(R.id.fragnent_show_items_floating_actions_menu_create_sketch) FloatingActionButton mCreateSketch;
    @InjectView(R.id.fragment_show_items_empty) TextView mTextEmpty;
    @InjectView(R.id.fragment_show_items_overlay) View mOverlay;

    private static final int NUM_COLUMNS = 4;

    private IOnFloatingActionMenuOptionClickedListener mIOnFloatingActionMenuOptionClickedListener;
    private IOnItemSelectedToEditListener mIOnItemSelectedToEditListener;
    private ShowItemsAdapter mAdapter;
    private SqlDatabaseHelper mSqlDatabaseHelper;

    private boolean mIsFabMenuVisible = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_show_items, container, false);
        ButterKnife.inject(this, rootView);

        mFloatingActionsMenu.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mFloatingActionsMenu.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fab_grow));
                mFloatingActionsMenu.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        mAdapter = new ShowItemsAdapter(getActivity());
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

        UtilityFunctions.setUpStaggeredGridRecycler(mItemsRecycler, mAdapter, NUM_COLUMNS);

        toggleEmptyText();

        mCreateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_shrink);
                mOverlay.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shutter_up));
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
                mOverlay.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shutter_up));
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
                mOverlay.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shutter_up));
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
                        mOverlay.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shutter_down));
                        mAdapter.setEnabled(false);
                        mItemsRecycler.setLayoutFrozen(true);
                    }

                    @Override
                    public void onMenuCollapsed() {
                        mOverlay.setVisibility(View.GONE);
                        mOverlay.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shutter_up));
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

        setItemType(NavDrawerItem.NavDrawerItemType.PINBOARD);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_fragment_show_items, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_action_search);
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
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
                }

                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mAdapter.refresh();
                return false;
            }
        });

        //Ensure correct options are selected when menu refreshes
        switch (mAdapter.getSortType()) {
            case EDITED_ASC:
                menu.findItem(R.id.menu_action_item_sort_submenu_edited_asc).setChecked(true);
                break;
            case EDITED_DESC:
                menu.findItem(R.id.menu_action_item_sort_submenu_edited_desc).setChecked(true);
                break;
            case CREATED_ASC:
                menu.findItem(R.id.menu_action_item_sort_submenu_created_asc).setChecked(true);
                break;
            case CREATED_DESC:
                menu.findItem(R.id.menu_action_item_sort_submenu_created_desc).setChecked(true);
                break;
        }

        //Ensure correct options are selected when menu refreshes
        switch (mAdapter.getFilterType()) {
            case NONE:
                menu.findItem(R.id.menu_action_item_filter_submenu_none).setChecked(true);
                break;
            case NOTE:
                menu.findItem(R.id.menu_action_item_filter_submenu_note).setChecked(true);
                break;
            case CHECKLIST:
                menu.findItem(R.id.menu_action_item_filter_submenu_checklist).setChecked(true);
                break;
            case SKETCH:
                menu.findItem(R.id.menu_action_item_filter_submenu_sketch).setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_action_item_filter_submenu_none) {
            mAdapter.setFilterType(ShowItemsAdapter.FilterType.NONE);
        } else if (item.getItemId() == R.id.menu_action_item_filter_submenu_note) {
            mAdapter.setFilterType(ShowItemsAdapter.FilterType.NOTE);
        }  else if (item.getItemId() == R.id.menu_action_item_filter_submenu_checklist) {
            mAdapter.setFilterType(ShowItemsAdapter.FilterType.CHECKLIST);
        }  else if (item.getItemId() == R.id.menu_action_item_filter_submenu_sketch) {
            mAdapter.setFilterType(ShowItemsAdapter.FilterType.SKETCH);
        }  else if (item.getItemId() == R.id.menu_action_item_sort_submenu_edited_asc) {
            mAdapter.setSortType(ShowItemsAdapter.SortType.EDITED_ASC);
        }  else if (item.getItemId() == R.id.menu_action_item_sort_submenu_edited_desc) {
            mAdapter.setSortType(ShowItemsAdapter.SortType.EDITED_DESC);
        }  else if (item.getItemId() == R.id.menu_action_item_sort_submenu_created_asc) {
            mAdapter.setSortType(ShowItemsAdapter.SortType.CREATED_ASC);
        }   else if (item.getItemId() == R.id.menu_action_item_sort_submenu_created_desc) {
            mAdapter.setSortType(ShowItemsAdapter.SortType.CREATED_DESC);
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
        mAdapter.replaceItems(mSqlDatabaseHelper.searchItems(searchQuery, mAdapter.getNavType()));
    }

    private void setListToCurrentType() {
        mAdapter.refresh();
        toggleEmptyText();
    }

    public void setItemType(NavDrawerItem.NavDrawerItemType type) {
        mAdapter.setNavType(type);
        toggleEmptyText();

        switch (type) {
            default:
            case PINBOARD:
                ApplicationNoted.bus.post(new TitleChangedEvent(getString(R.string.title_nav_drawer_pinboard)));
                break;
            case ARCHIVE:
                ApplicationNoted.bus.post(new TitleChangedEvent(getString(R.string.title_nav_drawer_archive)));
                break;
            case TRASH:
                ApplicationNoted.bus.post(new TitleChangedEvent(getString(R.string.title_nav_drawer_trash)));
                break;
        }
    }

}

package com.cerebellio.noted;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.helpers.AnimationsHelper;
import com.cerebellio.noted.helpers.PreferenceHelper;
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
import com.cerebellio.noted.utils.FeedbackFunctions;
import com.cerebellio.noted.utils.TextFunctions;
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

    @InjectView(R.id.fragment_show_items_container)
    FrameLayout mContainer;
    @InjectView(R.id.fragment_show_items_recycler)
    RecyclerView mItemsRecycler;
    @InjectView(R.id.fragment_show_items_floating_action_menu)
    FloatingActionsMenu mFloatingActionsMenu;
    @InjectView(R.id.fragnent_show_items_floating_actions_menu_create_note)
    FloatingActionButton mCreateNote;
    @InjectView(R.id.fragnent_show_items_floating_actions_menu_create_checklist)
    FloatingActionButton mCreateChecklist;
    @InjectView(R.id.fragnent_show_items_floating_actions_menu_create_sketch)
    FloatingActionButton mCreateSketch;
    @InjectView(R.id.fragment_show_items_empty)
    LinearLayout mEmpty;
    @InjectView(R.id.fragment_show_items_overlay)
    View mOverlay;

    private static final String LOG_TAG = TextFunctions.makeLogTag(FragmentShowItems.class);

    private IOnFloatingActionMenuOptionClickedListener mIOnFloatingActionMenuOptionClickedListener;
    private IOnItemSelectedToEditListener mIOnItemSelectedToEditListener;
    private ShowItemsAdapter mAdapter;
    private SqlDatabaseHelper mSqlDatabaseHelper;
    private NavDrawerItem.NavDrawerItemType mType = NavDrawerItem.NavDrawerItemType.PINBOARD;

    private boolean mIsFabMenuVisible = true;
    private boolean mIsEditedDescending = true;
    private boolean mIsCreatedDescending = true;
    private boolean mIsReminderDateDescending = true;
    private boolean mIsSortNCS = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_show_items, container, false);
        ButterKnife.inject(this, rootView);

        mFloatingActionsMenu.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                AnimationsHelper.showFloatingActionsMenu(mFloatingActionsMenu);
                mFloatingActionsMenu.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        mAdapter = new ShowItemsAdapter(getActivity());
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
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

        mCreateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateItemClick(view, Item.Type.NOTE);
            }
        });

        mCreateChecklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateItemClick(view, Item.Type.CHECKLIST);
            }
        });

        mCreateSketch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateItemClick(view, Item.Type.SKETCH);
            }
        });

        mOverlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mFloatingActionsMenu.isExpanded()) {
                    FeedbackFunctions.vibrate(view);
                    mFloatingActionsMenu.collapse();
                    return true;
                }
                return false;
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
                        FeedbackFunctions.vibrate(mFloatingActionsMenu);
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
                AnimationsHelper.hideFloatingActionsMenu(mFloatingActionsMenu);
                mIsFabMenuVisible = false;
            }

            @Override
            public void show() {
                if (!mType.equals(NavDrawerItem.NavDrawerItemType.PINBOARD)) {
                    return;
                }

                AnimationsHelper.showFloatingActionsMenu(mFloatingActionsMenu);
                mIsFabMenuVisible = true;
            }
        });

        setItemType(NavDrawerItem.NavDrawerItemType.PINBOARD);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_fragment_show_items, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_action_search);
        final MenuItem filterItem = menu.findItem(R.id.menu_action_item_filter);
        final MenuItem sortItem = menu.findItem(R.id.menu_action_item_sort);

        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                filterItem.setVisible(false);
                sortItem.setVisible(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                filterItem.setVisible(true);
                sortItem.setVisible(true);
                mAdapter.refresh();
                return true;
            }
        });

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

        //Ensure correct options are selected when menu refreshes
        switch (mAdapter.getSortType()) {
            case EDITED_ASC:
            case EDITED_DESC:
                menu.findItem(R.id.menu_action_item_sort_submenu_edited).setChecked(true);
                break;
            case CREATED_ASC:
            case CREATED_DESC:
                menu.findItem(R.id.menu_action_item_sort_submenu_created).setChecked(true);
                break;
            case TYPE_N_C_S:
            case TYPE_S_C_N:
                menu.findItem(R.id.menu_action_item_sort_submenu_type).setChecked(true);
                break;
            case REMINDER_ASC:
            case REMINDER_DESC:
                menu.findItem(R.id.menu_action_item_sort_submenu_reminder).setChecked(true);
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
            case IMPORTANT:
                menu.findItem(R.id.menu_action_item_filter_submenu_important).setChecked(true);
                break;
            case LOCKED:
                menu.findItem(R.id.menu_action_item_filter_submenu_locked).setChecked(true);
                break;
            case REMINDER:
                menu.findItem(R.id.menu_action_item_filter_submenu_reminder).setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_action_item_filter_submenu_none) {
            mAdapter.setFilterType(ShowItemsAdapter.FilterType.NONE);
            setToolbarTitleByType();
        } else if (item.getItemId() == R.id.menu_action_item_filter_submenu_note) {
            mAdapter.setFilterType(ShowItemsAdapter.FilterType.NOTE);
            ApplicationNoted.bus.post(new TitleChangedEvent(getString(R.string.title_filter_note)));
        } else if (item.getItemId() == R.id.menu_action_item_filter_submenu_checklist) {
            mAdapter.setFilterType(ShowItemsAdapter.FilterType.CHECKLIST);
            ApplicationNoted.bus.post(new TitleChangedEvent(getString(R.string.title_filter_checklist)));
        } else if (item.getItemId() == R.id.menu_action_item_filter_submenu_sketch) {
            mAdapter.setFilterType(ShowItemsAdapter.FilterType.SKETCH);
            ApplicationNoted.bus.post(new TitleChangedEvent(getString(R.string.title_filter_sketch)));
        } else if (item.getItemId() == R.id.menu_action_item_filter_submenu_important) {
            mAdapter.setFilterType(ShowItemsAdapter.FilterType.IMPORTANT);
            ApplicationNoted.bus.post(new TitleChangedEvent(getString(R.string.title_filter_important)));
        }  else if (item.getItemId() == R.id.menu_action_item_filter_submenu_locked) {
            mAdapter.setFilterType(ShowItemsAdapter.FilterType.LOCKED);
            ApplicationNoted.bus.post(new TitleChangedEvent(getString(R.string.title_filter_important)));
        } else if (item.getItemId() == R.id.menu_action_item_filter_submenu_reminder) {
            mAdapter.setFilterType(ShowItemsAdapter.FilterType.REMINDER);
            ApplicationNoted.bus.post(new TitleChangedEvent(getString(R.string.title_filter_reminder)));
        } else if (item.getItemId() == R.id.menu_action_item_sort_submenu_edited) {
            mIsEditedDescending = !mIsEditedDescending;
            mAdapter.setSortType(mIsEditedDescending
                    ? ShowItemsAdapter.SortType.EDITED_DESC
                    : ShowItemsAdapter.SortType.EDITED_ASC);
        } else if (item.getItemId() == R.id.menu_action_item_sort_submenu_created) {
            mIsCreatedDescending = !mIsCreatedDescending;
            mAdapter.setSortType(mIsCreatedDescending
                    ? ShowItemsAdapter.SortType.CREATED_DESC
                    : ShowItemsAdapter.SortType.CREATED_ASC);
        } else if (item.getItemId() == R.id.menu_action_item_sort_submenu_type) {
            mIsSortNCS = !mIsSortNCS;
            mAdapter.setSortType(mIsSortNCS
                    ? ShowItemsAdapter.SortType.TYPE_N_C_S
                    : ShowItemsAdapter.SortType.TYPE_S_C_N);
        } else if (item.getItemId() == R.id.menu_action_item_sort_submenu_reminder) {
            mIsReminderDateDescending = !mIsReminderDateDescending;
            mAdapter.setSortType(mIsReminderDateDescending
                    ? ShowItemsAdapter.SortType.REMINDER_DESC
                    : ShowItemsAdapter.SortType.REMINDER_ASC);
        }

        item.setChecked(true);
        toggleEmptyText();

        if (mFloatingActionsMenu.isExpanded()) {
            mFloatingActionsMenu.collapse();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationNoted.bus.register(this);
        UtilityFunctions.setUpStaggeredGridRecycler(mItemsRecycler, mAdapter,
                PreferenceHelper.getPrefPinboardColumns(getActivity()), LinearLayoutManager.VERTICAL);
        toggleEmptyText();
        mSqlDatabaseHelper = new SqlDatabaseHelper(getActivity());
        mFloatingActionsMenu.collapse();
        if (mType.equals(NavDrawerItem.NavDrawerItemType.PINBOARD)) {
            AnimationsHelper.showFloatingActionsMenu(mFloatingActionsMenu);
        }
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
    public void onRemove(final Item.Status prevStatus, final Item.Status newStatus,
                         final Item item, final int position) {
        mAdapter.removeItem(position);
        mSqlDatabaseHelper.addOrEditItem(item);
        ((ActivityMain) getActivity()).onResume();

        //Don't show SnackBar if user has asked not to be notified on status changes
        if (!PreferenceHelper.getPrefBehaviourNotifyStatusChange(getActivity())) {
            return;
        }

        String message = "";

        switch (newStatus) {
            case PINBOARD:
                message = getString(R.string.snackbar_restored);
                break;
            case ARCHIVED:
                message = getString(R.string.snackbar_archived);
                break;
            case DELETED:
                message = getString(R.string.snackbar_deleted);
                break;
        }

        Snackbar snackbar = Snackbar.make(mContainer, message, Snackbar.LENGTH_LONG);
        customiseSnackbar(getActivity(), snackbar);

        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (!mIsFabMenuVisible) {
                    return;
                }
                AnimationsHelper.translateFloatingActionsMenu(mFloatingActionsMenu, new DecelerateInterpolator(2),
                        0, AnimationsHelper.AnimationDirection.DOWN);
            }

            @Override
            public void onShown(Snackbar snackbar) {
                super.onShown(snackbar);
                if (!mIsFabMenuVisible) {
                    return;
                }
                AnimationsHelper.translateFloatingActionsMenu(mFloatingActionsMenu, new AccelerateInterpolator(2),
                        snackbar.getView().getHeight(), AnimationsHelper.AnimationDirection.UP);
            }
        });

        if (!newStatus.equals(Item.Status.DELETED)) {
            snackbar.setAction(getString(R.string.snackbar_undo), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item.setStatus(prevStatus);
                    mAdapter.addItem(item, position);
                    mSqlDatabaseHelper.addOrEditItem(item);
                    mItemsRecycler.scrollToPosition(position);
                }
            });
        }

        snackbar.show();
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
        if (mAdapter.getItemCount() == 0) {
            mEmpty.setVisibility(View.VISIBLE);
            mItemsRecycler.setVisibility(View.GONE);
        } else {
            mEmpty.setVisibility(View.GONE);
            mItemsRecycler.setVisibility(View.VISIBLE);
        }
    }

    private void onCreateItemClick(View view, final Item.Type type) {
        mOverlay.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shutter_up));
        mIOnFloatingActionMenuOptionClickedListener.OnFabCreateItemClick(type);
        FeedbackFunctions.vibrate(view);
    }

    public void searchChanged(String searchQuery) {
        mAdapter.replaceItems(mSqlDatabaseHelper.searchItems(searchQuery, mAdapter.getNavType()));
    }

    private void setListToCurrentType() {
        mAdapter.refresh();
        toggleEmptyText();
    }

    public void setItemType(NavDrawerItem.NavDrawerItemType type) {
        mType = type;
        mAdapter.setNavType(type);
        toggleEmptyText();
        setToolbarTitleByType();

        //Don't want user to add items when we're not in main view
        if (type.equals(NavDrawerItem.NavDrawerItemType.PINBOARD)) {
            if (!mIsFabMenuVisible) {
                AnimationsHelper.showFloatingActionsMenu(mFloatingActionsMenu);
            }
            mIsFabMenuVisible = true;
        } else {
            if (mIsFabMenuVisible) {
                AnimationsHelper.hideFloatingActionsMenu(mFloatingActionsMenu);
            }
            mIsFabMenuVisible = false;
        }
    }

    /**
     * Sets the Toolbar title depending on the current view we are in
     */
    private void setToolbarTitleByType() {
        switch (mType) {
            default:
            case PINBOARD:
                ApplicationNoted.bus.post(new TitleChangedEvent(getString(R.string.title_nav_drawer_pinboard)));
                break;
            case ARCHIVE:
                ApplicationNoted.bus.post(new TitleChangedEvent(getString(R.string.title_nav_drawer_archive)));
                break;
        }
    }

}

package com.cerebellio.noted;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.NavDrawerItem;
import com.cerebellio.noted.models.adapters.NavDrawerAdapter;
import com.cerebellio.noted.models.events.NavDrawerItemTypeSelectedEvent;
import com.cerebellio.noted.models.events.TitleChangedEvent;
import com.cerebellio.noted.models.listeners.IOnFloatingActionMenuOptionClickedListener;
import com.cerebellio.noted.models.listeners.IOnItemSelectedToEditListener;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.TextFunctions;
import com.cerebellio.noted.utils.UtilityFunctions;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Main Activity, handles numerous child Fragments
 */
public class ActivityMain extends ActivityBase
        implements IOnFloatingActionMenuOptionClickedListener, IOnItemSelectedToEditListener {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.activity_main_nav_drawer)
    DrawerLayout mNavDrawer;
    @InjectView(R.id.activity_main_recycler_nav_drawer)
    RecyclerView mNavDrawerRecycler;

    private static final String LOG_TAG = TextFunctions.makeLogTag(ActivityMain.class);

    private static final String FRAGMENT_SHOW_ITEMS_TAG = "show_items_tag";
    private static final String FRAGMENT_ADD_EDIT_ITEM_TAG = "add_edit_item_tag";

    private FragmentManager mFragmentManager;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavDrawerItem.NavDrawerItemType mCurrentNavDrawerType = NavDrawerItem.NavDrawerItemType.PINBOARD;
    private NavDrawerAdapter mNavDrawerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        //Initialise FragmentManager and add BackStackChanged listener
        mFragmentManager = getSupportFragmentManager();
        //region OnBackStackChangedListener
        mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

                syncToolbarArrowState();

                //Hide keyboard if we are on main screen
                if (mFragmentManager.getBackStackEntryCount() == 0) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (getCurrentFocus() != null) {
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }

                FragmentShowItems fragmentShowItems =
                        (FragmentShowItems) mFragmentManager.findFragmentByTag(FRAGMENT_SHOW_ITEMS_TAG);

                //If we are showing items, pass navigation drawer value
                if (isCurrentFragment(fragmentShowItems)) {
                    fragmentShowItems.setItemType(mCurrentNavDrawerType);
                }

            }
        });
        //endregion

        //Setup Toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mNavDrawer, mToolbar, 0, 0) {
            @Override
            public void onDrawerOpened(View drawerView) {
                mNavDrawerAdapter.notifyDataSetChanged();
                super.onDrawerOpened(drawerView);
                supportInvalidateOptionsMenu();
                syncState();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu();
                syncState();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentManager.popBackStackImmediate();
            }
        });
        mNavDrawer.setDrawerListener(mDrawerToggle);

        initShowItemsFragment();
        initNavDrawer();
        handleIntent(getIntent());
        setToolbarTitle(mToolbar, getString(R.string.title_nav_drawer_pinboard));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationNoted.bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ApplicationNoted.bus.unregister(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void OnFabCreateItemClick(Item.Type type) {
        displayAddEditItemFragment(null, type);
    }

    @Override
    public void onItemToEdit(Item item) {
        displayAddEditItemFragment(item, item.getItemType());
    }

    /**
     * Display one of the Add/Edit fragments
     * @param item      {@link Item} to display, null if adding new
     * @param type      {@link com.cerebellio.noted.models.Item.Type} of item
     */
    private void displayAddEditItemFragment(Item item, Item.Type type) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        animateFragmentTransition(ft, TRANSITION_HORIZONTAL);
        Fragment fragment;
        Bundle bundle = new Bundle();

        //If item is null, we are adding a new one, else editing existing item
        if (item == null) {
            bundle.putBoolean(Constants.BUNDLE_IS_IN_EDIT_MODE, false);
        } else {
            bundle.putBoolean(Constants.BUNDLE_IS_IN_EDIT_MODE, true);
            bundle.putLong(Constants.BUNDLE_ITEM_TO_EDIT_ID, item.getId());
        }

        //Set fragment to applicable type
        switch (type) {
            default:
            case NOTE:
                fragment = new FragmentAddEditNote();
                break;
            case CHECKLIST:
                fragment = new FragmentAddEditChecklist();
                break;
            case SKETCH:
                fragment = new FragmentAddEditSketch();
                break;
        }

        fragment.setArguments(bundle);
        ft.replace(R.id.activity_main_fragment, fragment, FRAGMENT_ADD_EDIT_ITEM_TAG)
                .addToBackStack(FRAGMENT_SHOW_ITEMS_TAG).commit();
    }

    @Subscribe
    public void onNavDrawerItemSelected(final NavDrawerItemTypeSelectedEvent event) {
        switch (event.getType()) {
            case PINBOARD:
                setItemType(NavDrawerItem.NavDrawerItemType.PINBOARD);
                break;
            case ARCHIVE:
                setItemType(NavDrawerItem.NavDrawerItemType.ARCHIVE);
                break;
            case WORDCLOUD:
                startActivity(new Intent(this, ActivityWordCloud.class));
                mNavDrawer.closeDrawers();
                break;
            default:
            case SETTINGS:
                startActivity(new Intent(this, ActivitySettings.class));
                mNavDrawer.closeDrawers();
                break;
            case BURSTLE:
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.cerebellio.burstle")));
                break;
            case GRIDDITION:
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.cerebellio.griddition")));
                break;
        }
    }

    @Subscribe
    public void onTitleChanged(TitleChangedEvent event) {
        setToolbarTitle(mToolbar, event.getTitle());
    }

    /**
     * Parses given Intent
     * @param intent        Intent to check
     */
    public void handleIntent(Intent intent) {
        if (intent.getAction() == null) {
            if (intent.getExtras() != null) {
                if (intent.getBooleanExtra(Constants.INTENT_FROM_NOTIFICATION, false)) {
                    //Come from notification
                    try {
                        SqlDatabaseHelper sqlDatabaseHelper = new SqlDatabaseHelper(this);

                        Item item = sqlDatabaseHelper.getItemByReminderId(
                                intent.getLongExtra(Constants.INTENT_REMINDER_ID, -1));
                        displayAddEditItemFragment(item, item.getItemType());

                        sqlDatabaseHelper.closeDB();
                    } catch (NullPointerException e) {
                        Log.e(LOG_TAG, "Error retrieving item");
                    }
                }
            }
        } else if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            //Search has been performed
            Log.d(LOG_TAG, intent.getStringExtra(SearchManager.QUERY));

            FragmentShowItems fragmentShowItems =
                    (FragmentShowItems) mFragmentManager.findFragmentByTag(FRAGMENT_SHOW_ITEMS_TAG);

            if (isCurrentFragment(fragmentShowItems)) {
                fragmentShowItems.searchChanged(intent.getStringExtra(SearchManager.QUERY));
            }
        }
    }

    /**
     * Passes the selected {@link com.cerebellio.noted.models.NavDrawerItem.NavDrawerItemType}
     * from the navigation drawer to child fragments
     * @param type      {@link com.cerebellio.noted.models.NavDrawerItem.NavDrawerItemType} to pass
     */
    private void setItemType(NavDrawerItem.NavDrawerItemType type) {
        mCurrentNavDrawerType = type;

        FragmentShowItems fragmentShowItems =
                (FragmentShowItems) mFragmentManager.findFragmentByTag(FRAGMENT_SHOW_ITEMS_TAG);

        if (isCurrentFragment(fragmentShowItems)) {
            fragmentShowItems.setItemType(type);
        }

        mNavDrawerAdapter.notifyDataSetChanged();
    }

    /**
     * Adds a new {@link FragmentShowItems} to the {@link #mFragmentManager} if one doesn't exist
     */
    private void initShowItemsFragment() {

        FragmentTransaction ft;
        FragmentShowItems fragmentShowItems =
                (FragmentShowItems) mFragmentManager.findFragmentByTag(FRAGMENT_SHOW_ITEMS_TAG);

        if (!isCurrentFragment(fragmentShowItems)) {

            fragmentShowItems = new FragmentShowItems();
            fragmentShowItems.setHasOptionsMenu(true);

            ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.activity_main_fragment,
                    fragmentShowItems, FRAGMENT_SHOW_ITEMS_TAG).commit();
        }
    }

    /**
     * Initialises the navigation drawer RecyclerView
     */
    private void initNavDrawer() {

        List<NavDrawerItem> items = new ArrayList<>();

        items.add(new NavDrawerItem(getString(R.string.nav_drawer_pinboard), R.drawable.ic_pinboard, false, NavDrawerItem.NavDrawerItemType.PINBOARD));
        items.add(new NavDrawerItem(getString(R.string.nav_drawer_archive), R.drawable.ic_archive, false, NavDrawerItem.NavDrawerItemType.ARCHIVE));
        items.add(new NavDrawerItem(getString(R.string.nav_drawer_wordcloud), R.drawable.ic_wordcloud, true, NavDrawerItem.NavDrawerItemType.WORDCLOUD));
        items.add(new NavDrawerItem(getString(R.string.nav_drawer_settings), R.drawable.ic_settings, false, NavDrawerItem.NavDrawerItemType.SETTINGS));
        items.add(new NavDrawerItem(getString(R.string.nav_drawer_burstle), R.drawable.ic_game, true, NavDrawerItem.NavDrawerItemType.BURSTLE));
        items.add(new NavDrawerItem(getString(R.string.nav_drawer_griddition), R.drawable.ic_game, false, NavDrawerItem.NavDrawerItemType.GRIDDITION));

        mNavDrawerAdapter = new NavDrawerAdapter(items, this);
        UtilityFunctions.setUpLinearRecycler(this, mNavDrawerRecycler,
                mNavDrawerAdapter, LinearLayoutManager.VERTICAL);
    }

    /**
     * Toggles Toolbar arrow to appropriate home/back icon
     */
    private void syncToolbarArrowState() {

        if (mFragmentManager.getBackStackEntryCount() == 0) {

            //If home screen, show hamburger
            mNavDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        } else {

            //Show navigate back up arrow and lock nav drawer
            mNavDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
        }
    }

}

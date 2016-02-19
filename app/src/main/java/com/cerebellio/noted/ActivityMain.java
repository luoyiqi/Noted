package com.cerebellio.noted;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.cerebellio.noted.models.CheckList;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.NavDrawerItem;
import com.cerebellio.noted.models.Note;
import com.cerebellio.noted.models.adapters.NavDrawerAdapter;
import com.cerebellio.noted.models.listeners.IOnFloatingActionMenuOptionClickedListener;
import com.cerebellio.noted.models.listeners.IOnItemSelectedToEditListener;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.UtilityFunctions;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ActivityMain extends ActivityBase
        implements IOnFloatingActionMenuOptionClickedListener, IOnItemSelectedToEditListener {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.activity_main_nav_drawer)
    DrawerLayout mNavDrawer;
    @InjectView(R.id.activity_main_recycler_nav_drawer)
    RecyclerView mNavDrawerRecycler;

    private static final String FRAGMENT_SHOW_ITEMS_TAG = "show_items_tag";
    private static final String FRAGMENT_ADD_EDIT_ITEM_TAG = "add_edit_item_tag";

    private FragmentManager mFragmentManager;
    private ActionBarDrawerToggle mDrawerToggle;

    private NavDrawerItem.NavDrawerItemType mCurrentNavDrawerType = NavDrawerItem.NavDrawerItemType.PINBOARD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.addOnBackStackChangedListener(mOnBackStackChangedListener);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mNavDrawer, mToolbar, 0, 0) {
            @Override
            public void onDrawerOpened(View drawerView) {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_action_settings) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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
    public void OnFabCreateItemClick(Item.Type type) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        animateFragmentTransition(ft, TRANSITION_HORIZONTAL);
        Fragment fragment;
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.BUNDLE_IS_IN_EDIT_MODE, false);

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

    @Override
    public void onItemSelected(Item item) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        animateFragmentTransition(ft, TRANSITION_HORIZONTAL);
        Fragment fragment;
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.BUNDLE_IS_IN_EDIT_MODE, true);
        bundle.putLong(Constants.BUNDLE_ITEM_TO_EDIT_ID, item.getId());

        if (item instanceof Note) {
            fragment = new FragmentAddEditNote();
        } else if (item instanceof CheckList) {
            fragment = new FragmentAddEditChecklist();
        } else {
            fragment = new FragmentAddEditSketch();
        }

        fragment.setArguments(bundle);
        ft.replace(R.id.activity_main_fragment, fragment, FRAGMENT_ADD_EDIT_ITEM_TAG)
                .addToBackStack(FRAGMENT_SHOW_ITEMS_TAG).commit();
    }

    @Subscribe
    public void onNavDrawerItemSelected(NavDrawerItem.NavDrawerItemType type) {
        switch (type) {
            case PINBOARD:
                setItemType(NavDrawerItem.NavDrawerItemType.PINBOARD);
                break;
            case ARCHIVE:
                setItemType(NavDrawerItem.NavDrawerItemType.ARCHIVE);
                break;
            case TRASH:
                setItemType(NavDrawerItem.NavDrawerItemType.TRASH);
                break;
            default:
            case SETTINGS:
                if (PreferenceManager.getDefaultSharedPreferences(this)
                        .getInt(Constants.SHARED_PREFS_THEME_ID, Constants.DEFAULT_THEME_ID)
                        == Constants.DEFAULT_THEME_ID) {
                    PreferenceManager.getDefaultSharedPreferences(this)
                            .edit()
                            .putInt(Constants.SHARED_PREFS_THEME_ID, Constants.DARK_THEME_ID)
                            .apply();
                } else {
                    PreferenceManager.getDefaultSharedPreferences(this)
                            .edit()
                            .putInt(Constants.SHARED_PREFS_THEME_ID, Constants.DEFAULT_THEME_ID)
                            .apply();
                }
                recreate();
                break;
        }
    }

    private void setItemType(NavDrawerItem.NavDrawerItemType type) {
        mCurrentNavDrawerType = type;

        FragmentShowItems fragmentShowItems =
                (FragmentShowItems) mFragmentManager.findFragmentByTag(FRAGMENT_SHOW_ITEMS_TAG);

        if (fragmentShowItems != null && fragmentShowItems.isVisible()) {
            fragmentShowItems.setItemType(type);
        }

    }

    private void initShowItemsFragment() {
        FragmentTransaction ft;
        FragmentShowItems fragmentShowItems =
                (FragmentShowItems) mFragmentManager.findFragmentByTag(FRAGMENT_SHOW_ITEMS_TAG);
        if (fragmentShowItems == null) {
            fragmentShowItems = new FragmentShowItems();
            fragmentShowItems.setHasOptionsMenu(true);

            ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.activity_main_fragment,
                    fragmentShowItems, FRAGMENT_SHOW_ITEMS_TAG).commit();

        }
    }

    private void initNavDrawer() {
        List<NavDrawerItem> items = new ArrayList<>();

        items.add(new NavDrawerItem(getString(R.string.nav_drawer_pinboard), R.drawable.ic_pinboard, NavDrawerItem.NavDrawerItemType.PINBOARD));
        items.add(new NavDrawerItem(getString(R.string.nav_drawer_archive), R.drawable.ic_archive, NavDrawerItem.NavDrawerItemType.ARCHIVE));
        items.add(new NavDrawerItem(getString(R.string.nav_drawer_trash), R.drawable.ic_trash, NavDrawerItem.NavDrawerItemType.TRASH));
        items.add(new NavDrawerItem(getString(R.string.nav_drawer_settings), R.drawable.ic_settings, NavDrawerItem.NavDrawerItemType.SETTINGS));

        UtilityFunctions.setUpLinearRecycler(this, mNavDrawerRecycler,
                new NavDrawerAdapter(items, this), LinearLayoutManager.VERTICAL);
    }

    private void syncToolbarArrowState() {
        if (mFragmentManager.getBackStackEntryCount() == 0) {
            mNavDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        } else {
            mNavDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
        }
    }

    private FragmentManager.OnBackStackChangedListener mOnBackStackChangedListener =
            new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    syncToolbarArrowState();
                    if (mFragmentManager.getBackStackEntryCount() == 0) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (getCurrentFocus() != null) {
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }

                    FragmentShowItems fragmentShowItems =
                            (FragmentShowItems) mFragmentManager.findFragmentByTag(FRAGMENT_SHOW_ITEMS_TAG);
                    if (fragmentShowItems != null) {
                        fragmentShowItems.setItemType(mCurrentNavDrawerType);
                    }

                }
            };

}

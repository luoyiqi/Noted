package com.cerebellio.noted;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.cerebellio.noted.models.CheckList;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.Note;
import com.cerebellio.noted.models.listeners.IOnFloatingActionMenuOptionClickedListener;
import com.cerebellio.noted.models.listeners.IOnItemSelectedToEditListener;
import com.cerebellio.noted.utils.Constants;
import com.google.gson.Gson;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ActivityMain extends ActivityBase
        implements IOnFloatingActionMenuOptionClickedListener, IOnItemSelectedToEditListener {

    @InjectView(R.id.toolbar) Toolbar mToolbar;
    @InjectView(R.id.activity_main_navigation_drawer) DrawerLayout mNavigationDrawer;

    private static final String FRAGMENT_SHOW_NOTES_TAG = "show_notes_tag";
    private static final String FRAGMENT_ADD_EDIT_NOTE_TAG = "add_edit_note_tag";
    private static final String FRAGMENT_ADD_EDIT_CHECKLIST_TAG = "add_edit_checklist_tag";

    private FragmentManager mFragmentManager;
    private ActionBarDrawerToggle mDrawerToggle;

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
                }
            };

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

        mDrawerToggle = new ActionBarDrawerToggle(this, mNavigationDrawer, mToolbar, 0, 0) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu();
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

        mNavigationDrawer.setDrawerListener(mDrawerToggle);

        initShowItemsFragment();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
    public void OnFabNewNoteClick() {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        FragmentAddEditNote fragmentAddEditNote = new FragmentAddEditNote();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.BUNDLE_IS_IN_EDIT_MODE, false);
        fragmentAddEditNote.setArguments(bundle);
        ft.replace(R.id.activity_main_fragment, fragmentAddEditNote, FRAGMENT_ADD_EDIT_NOTE_TAG)
                .addToBackStack(FRAGMENT_SHOW_NOTES_TAG).commit();
    }

    @Override
    public void OnFabNewChecklistClick() {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        FragmentAddEditChecklist fragmentAddEditChecklist = new FragmentAddEditChecklist();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.BUNDLE_IS_IN_EDIT_MODE, false);
        fragmentAddEditChecklist.setArguments(bundle);
        ft.replace(R.id.activity_main_fragment, fragmentAddEditChecklist, FRAGMENT_ADD_EDIT_CHECKLIST_TAG)
                .addToBackStack(FRAGMENT_SHOW_NOTES_TAG).commit();
    }

    @Override
    public void onItemSelected(Item item) {
        if (item instanceof Note) {
            onNoteSelected((Note) item);
        } else if (item instanceof CheckList) {
            onChecklistSelected((CheckList) item);
        }
    }

    private void onNoteSelected(Note note) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        FragmentAddEditNote fragmentAddEditNote = new FragmentAddEditNote();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.BUNDLE_IS_IN_EDIT_MODE, true);
        bundle.putString(Constants.BUNDLE_NOTE_TO_EDIT_JSON, new Gson().toJson(note));
        fragmentAddEditNote.setArguments(bundle);
        ft.replace(R.id.activity_main_fragment, fragmentAddEditNote, FRAGMENT_ADD_EDIT_NOTE_TAG)
                .addToBackStack(FRAGMENT_SHOW_NOTES_TAG).commit();
    }

    private void onChecklistSelected(CheckList checkList) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        FragmentAddEditChecklist fragmentAddEditChecklist = new FragmentAddEditChecklist();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.BUNDLE_IS_IN_EDIT_MODE, true);
        bundle.putLong(Constants.BUNDLE_CHECKLIST_TO_EDIT_ID, checkList.getId());
        fragmentAddEditChecklist.setArguments(bundle);
        ft.replace(R.id.activity_main_fragment, fragmentAddEditChecklist, FRAGMENT_ADD_EDIT_CHECKLIST_TAG)
                .addToBackStack(FRAGMENT_SHOW_NOTES_TAG).commit();
    }

    private void initShowItemsFragment() {
        FragmentTransaction ft;
        FragmentShowItems fragmentShowItems =
                (FragmentShowItems) mFragmentManager.findFragmentById(R.id.activity_main_fragment);

        if (fragmentShowItems == null) {
            ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.activity_main_fragment,
                    new FragmentShowItems(), FRAGMENT_SHOW_NOTES_TAG).commit();
        }
    }

    private void syncToolbarArrowState() {
        if (mFragmentManager.getBackStackEntryCount() == 0) {
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        } else {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
        }
    }

}

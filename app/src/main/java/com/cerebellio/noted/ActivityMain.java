package com.cerebellio.noted;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.cerebellio.noted.models.listeners.IOnFabAddClickedListener;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ActivityMain extends ActivityBase implements IOnFabAddClickedListener {

    @InjectView(R.id.toolbar) Toolbar toolbar;

    private static final String FRAGMENT_SHOW_NOTES_TAG = "show_notes_tag";
    private static final String FRAGMENT_ADD_NOTE_TAG = "add_note_tag";

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mFragmentManager = getSupportFragmentManager();

        initShowNotesFragment();

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnFabAddClick() {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        FragmentAddNote fragmentAddNote = new FragmentAddNote();
        ft.replace(R.id.activity_main_fragment, fragmentAddNote, FRAGMENT_ADD_NOTE_TAG)
                .addToBackStack(FRAGMENT_SHOW_NOTES_TAG).commit();
    }

    private void initShowNotesFragment() {
        FragmentTransaction ft;
        FragmentShowNotes fragmentShowNotes =
                (FragmentShowNotes) mFragmentManager.findFragmentById(R.id.activity_main_fragment);

        if (fragmentShowNotes == null) {
            ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.activity_main_fragment,
                    new FragmentShowNotes(), FRAGMENT_SHOW_NOTES_TAG).commit();
        }
    }

}

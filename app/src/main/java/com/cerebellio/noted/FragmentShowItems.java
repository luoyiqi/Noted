package com.cerebellio.noted;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.adapters.ShowItemsAdapter;
import com.cerebellio.noted.models.listeners.IOnFloatingActionMenuOptionClickedListener;
import com.cerebellio.noted.models.listeners.IOnItemSelectedToEditListener;
import com.cerebellio.noted.utils.UtilityFunctions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Sam on 09/02/2016.
 */
public class FragmentShowItems extends Fragment {

    @InjectView(R.id.fragment_show_notes_recycler) RecyclerView mNotesRecycler;
    @InjectView(R.id.fragment_show_notes_empty) TextView mTextmpty;
    @InjectView(R.id.fragment_show_notes_floating_action_menu) FloatingActionsMenu mFloatingActionsMenu;
    @InjectView(R.id.fragnent_show_notes_floating_actions_menu_new_note) FloatingActionButton mNewNote;
    @InjectView(R.id.fragnent_show_notes_floating_actions_menu_new_checklist) FloatingActionButton mNewChecklist;

    private static final int NUM_COLUMNS = 2;

    private IOnFloatingActionMenuOptionClickedListener mIOnFloatingActionMenuOptionClickedListener;
    private IOnItemSelectedToEditListener mIOnItemSelectedToEditListener;
    private ShowItemsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_show_notes, container, false);
        ButterKnife.inject(this, rootView);

        mAdapter = new ShowItemsAdapter(getActivity());
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                SqlDatabaseHelper sqlDatabaseHelper = new SqlDatabaseHelper(getActivity());

                for (int i = positionStart; i < positionStart + itemCount; i++) {
                    sqlDatabaseHelper.deleteItem(mAdapter.getItems().get(i));
                    mAdapter.getItems().remove(i);
                }

                sqlDatabaseHelper.closeDB();

                toggleEmptyText();

                super.onItemRangeRemoved(positionStart, itemCount);
            }
        });
        UtilityFunctions.setUpStaggeredGridRecycler(mNotesRecycler, mAdapter, NUM_COLUMNS);

        toggleEmptyText();

        mNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIOnFloatingActionMenuOptionClickedListener.OnFabNewNoteClick();
            }
        });

        mNewChecklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIOnFloatingActionMenuOptionClickedListener.OnFabNewChecklistClick();
            }
        });

        mFloatingActionsMenu.setOnFloatingActionsMenuUpdateListener(
                new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                    @Override
                    public void onMenuExpanded() {
                        mNotesRecycler.startAnimation(
                                AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out));
                        mAdapter.setEnabled(false);
                        mNotesRecycler.setLayoutFrozen(true);
                    }

                    @Override
                    public void onMenuCollapsed() {
                        mNotesRecycler.startAnimation(
                                AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
                        mAdapter.setEnabled(true);
                        mNotesRecycler.setLayoutFrozen(false);
                    }
                });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ApplicationNoted.bus.register(this);
        mFloatingActionsMenu.collapse();
    }

    @Override
    public void onPause() {
        super.onPause();
        ApplicationNoted.bus.unregister(this);
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

    @Subscribe
    public void itemToEdit(Item item) {
        mIOnItemSelectedToEditListener.onItemSelected(item);
    }

    /**
     * If the RecyclerView has anything to show,
     * hide message telling user it is empty and vice versa.
     *
     * Uses @UiThread to force the RecyclerView to be redrawn instantly on final item delete
     */
    @UiThread
    private void toggleEmptyText() {
        if (mAdapter.getItemCount() == 0) {
            mTextmpty.setVisibility(View.VISIBLE);
            mNotesRecycler.setVisibility(View.GONE);
        } else {
            mTextmpty.setVisibility(View.GONE);
            mNotesRecycler.setVisibility(View.VISIBLE);
        }
    }

}

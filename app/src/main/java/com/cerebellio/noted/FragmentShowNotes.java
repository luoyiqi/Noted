package com.cerebellio.noted;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cerebellio.noted.models.adapters.ShowNotesAdapter;
import com.cerebellio.noted.models.listeners.IOnFabAddClickedListener;
import com.cerebellio.noted.utils.UtilityFunctions;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Sam on 09/02/2016.
 */
public class FragmentShowNotes extends Fragment {

    @InjectView(R.id.fragment_show_notes_recycler) RecyclerView mNotesRecycler;
    @InjectView(R.id.fragment_show_notes_add) FloatingActionButton mAddNote;

    private IOnFabAddClickedListener mIOnFabAddClickedListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_show_notes, container, false);
        ButterKnife.inject(this, rootView);

        ShowNotesAdapter adapter = new ShowNotesAdapter(getActivity());
        UtilityFunctions.setUpStaggeredGridRecycler(getActivity(), mNotesRecycler, adapter, 2);

        mAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIOnFabAddClickedListener.OnFabAddClick();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mIOnFabAddClickedListener = (IOnFabAddClickedListener) context;
        } catch (ClassCastException e) {
            Log.e("Listener Error", "Calling Activity does not implement IOnFabAddClickedListener");
        }
    }
}

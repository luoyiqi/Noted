package com.cerebellio.noted;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cerebellio.noted.models.adapters.ColourSelectionAdapter;
import com.cerebellio.noted.models.events.ColourSelectedEvent;
import com.cerebellio.noted.models.listeners.IOnColourSelectedListener;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.TextFunctions;
import com.cerebellio.noted.utils.UtilityFunctions;
import com.squareup.otto.Subscribe;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Allows user to select a colour from a palette
 */
public class FragmentColourSelection extends Fragment {

    @InjectView(R.id.fragment_colour_selection_recycler) RecyclerView mRecyclerView;

    private static final String LOG_TAG = TextFunctions.makeLogTag(FragmentColourSelection.class);

    private static final int NUM_COLUMNS = 7;

    private IOnColourSelectedListener mIOnColourSelectedListener;

    public FragmentColourSelection() {    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_colour_selection, container, false);
        ButterKnife.inject(this, rootView);

        ColourSelectionAdapter colourSelectionAdapter =
                new ColourSelectionAdapter(Arrays.asList(Constants.COLOURS));

        UtilityFunctions.setUpStaggeredGridRecycler(mRecyclerView,
                colourSelectionAdapter, NUM_COLUMNS, LinearLayoutManager.VERTICAL);

        return rootView;
    }

    @Override
    public void onResume() {

        super.onResume();

        ApplicationNoted.bus.register(this);
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
            mIOnColourSelectedListener = (IOnColourSelectedListener) getParentFragment();
        } catch (ClassCastException e) {
            Log.e("Listener error", "Parent fragment does not implement listeners");
        }
    }

    @Subscribe
    public void receiveColour(ColourSelectedEvent event) {
        mIOnColourSelectedListener.onColourSelected(event.getColour());
    }

}

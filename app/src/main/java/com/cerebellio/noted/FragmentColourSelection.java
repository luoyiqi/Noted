package com.cerebellio.noted;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cerebellio.noted.models.adapters.ColourSelectionAdapter;
import com.cerebellio.noted.models.listeners.IOnColourSelectedListener;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.UtilityFunctions;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Sam on 10/02/2016.
 */
public class FragmentColourSelection extends Fragment {

    @InjectView(R.id.fragment_colour_selection_frame) FrameLayout mContainer;
    @InjectView(R.id.fragment_colour_selection_bar) TextView mSelectionBar;
    @InjectView(R.id.fragment_colour_selection_recycler) RecyclerView mRecyclerView;

    private static final int NUM_COLUMNS = 8;

    private boolean mIsBarNeeded = true;

    private IOnColourSelectedListener mIOnColourSelectedListener;
    private ColourSelectionAdapter mColourSelectionAdapter;

    public FragmentColourSelection() {
        List<Integer> colours = new ArrayList<>();
        for (Integer colour : Constants.COLOURS) {
            colours.add(colour);
        }

        mColourSelectionAdapter = new ColourSelectionAdapter(colours);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_colour_selection, container, false);
        ButterKnife.inject(this, rootView);

        mIsBarNeeded = getArguments().getBoolean(Constants.BUNDLE_COLOUR_SELECTION_NEEDS_BAR, true);

        if (!mIsBarNeeded) {
            mSelectionBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        mSelectionBar.setBackgroundColor(getArguments().getInt(Constants.BUNDLE_CURRENT_COLOUR));

        UtilityFunctions.setUpGridRecycler(getActivity(), mRecyclerView,
                mColourSelectionAdapter, NUM_COLUMNS);

        mSelectionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectionBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        });

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
    public void receiveColour(Integer colour) {
        mIOnColourSelectedListener.onColourSelected(colour);
        if (mIsBarNeeded) {
            mSelectionBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
        mSelectionBar.setBackgroundColor(colour);
    }

}

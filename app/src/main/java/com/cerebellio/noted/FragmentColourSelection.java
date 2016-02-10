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

    private IOnColourSelectedListener mIOnColourSelectedListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_colour_selection, container, false);
        ButterKnife.inject(this, rootView);

        mSelectionBar.setBackgroundColor(getArguments().getInt(Constants.BUNDLE_CURRENT_COLOUR));

        List<Integer> colours = new ArrayList<>();

        colours.add(0xFFF44336);
        colours.add(0xFFD32F2F);

        colours.add(0xFFE91E63);
        colours.add(0xFFC2185B);

        colours.add(0xFF9C27B0);
        colours.add(0xFF7B1FA2);

        colours.add(0xFF3F51B5);
        colours.add(0xFF303F9F);

        colours.add(0xFF2196F3);
        colours.add(0xFF1976D2);

        colours.add(0xFF03A9F4);
        colours.add(0xFF0288D1);

        colours.add(0xFF00BCD4);
        colours.add(0xFF0097A7);

        colours.add(0xFF009688);
        colours.add(0xFF00796B);

        colours.add(0xFF8BC34A);
        colours.add(0xFF689F38);

        colours.add(0xFFCDDC39);
        colours.add(0xFFA4B42B);

        colours.add(0xFFCDDC39);
        colours.add(0xFFFBC02D);

        colours.add(0xFFFFC107);
        colours.add(0xFFFFA000);

        colours.add(0xFFFF9800);
        colours.add(0xFFF57C00);

        colours.add(0xFFFF5722);
        colours.add(0xFFE64A19);

        colours.add(0xFF795548);
        colours.add(0xFF5D4037);

        colours.add(0xFF607D8B);
        colours.add(0xFF455A64);

        UtilityFunctions.setUpGridRecycler(getActivity(), mRecyclerView,
                new ColourSelectionAdapter(colours), NUM_COLUMNS);

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
        mSelectionBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mSelectionBar.setBackgroundColor(colour);
    }

}

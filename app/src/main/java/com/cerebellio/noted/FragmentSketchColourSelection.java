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
 * Created by Sam on 13/02/2016.
 */
public class FragmentSketchColourSelection extends Fragment {

    @InjectView(R.id.fragment_colour_selection_bar)
    TextView mSelectionBar;
    @InjectView(R.id.fragment_colour_selection_recycler)
    RecyclerView mRecyclerView;

    private static final int NUM_COLUMNS = 8;

    private IOnColourSelectedListener mIOnColourSelectedListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_colour_selection, container, false);
        ButterKnife.inject(this, rootView);

        mSelectionBar.setVisibility(View.GONE);

        List<Integer> colours = new ArrayList<>();
        for (Integer colour : Constants.COLOURS) {
            colours.add(colour);
        }

        UtilityFunctions.setUpGridRecycler(getActivity(), mRecyclerView,
                new ColourSelectionAdapter(colours), NUM_COLUMNS);

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
    }
}

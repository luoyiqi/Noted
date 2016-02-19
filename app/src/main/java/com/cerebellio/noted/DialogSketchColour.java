package com.cerebellio.noted;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SeekBar;

import com.cerebellio.noted.models.listeners.IOnAlphaChangedListener;
import com.cerebellio.noted.models.listeners.IOnColourSelectedListener;
import com.cerebellio.noted.utils.Constants;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Sam on 12/02/2016.
 */
public class DialogSketchColour extends DialogFragment implements IOnColourSelectedListener {

    @InjectView(R.id.popup_stroke_colour_selection_alpha_seekbar) SeekBar mSeekbarAlpha;

    private IOnColourSelectedListener mIOnColourSelectedListener;
    private IOnAlphaChangedListener mIOnAlphaChangedListener;
    private FragmentColourSelection mFragmentColourSelection;

    public DialogSketchColour() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View rootView = getActivity().getLayoutInflater().inflate(R.layout.popup_colour_selection, null);
        ButterKnife.inject(this, rootView);

        mFragmentColourSelection = new FragmentColourSelection();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.BUNDLE_COLOUR_SELECTION_NEEDS_BAR, false);
        mFragmentColourSelection.setArguments(bundle);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.popup_stroke_colour_selection, mFragmentColourSelection)
                .commit();

        mSeekbarAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                mFragmentColourSelection.alphaChanged(progress);
                mIOnAlphaChangedListener.onAlphaChanged(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekbarAlpha.setProgress(getArguments().getInt(Constants.BUNDLE_CURRENT_COLOUR_ALPHA, 255));

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mIOnColourSelectedListener = (IOnColourSelectedListener) getParentFragment();
            mIOnAlphaChangedListener = (IOnAlphaChangedListener) getParentFragment();
        } catch (ClassCastException e) {
            Log.e("Listener failure", "Calling context must implement listeners");
        }
    }

    @Override
    public void onColourSelected(Integer colour) {
        mIOnColourSelectedListener.onColourSelected(colour);
        dismiss();
    }

}

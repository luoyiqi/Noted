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

import com.cerebellio.noted.models.listeners.IOnColourSelectedListener;
import com.cerebellio.noted.utils.Constants;

/**
 * Created by Sam on 12/02/2016.
 */
public class DialogSketchColour extends DialogFragment implements IOnColourSelectedListener {

    private IOnColourSelectedListener mIOnColourSelectedListener;
    private FragmentColourSelection mFragmentColourSelection;

    public DialogSketchColour() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_colour_selection, null);

        mFragmentColourSelection = new FragmentColourSelection();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.BUNDLE_COLOUR_SELECTION_NEEDS_BAR, false);
        mFragmentColourSelection.setArguments(bundle);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.dialog_stroke_colour_selection, mFragmentColourSelection)
                .commit();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mIOnColourSelectedListener = (IOnColourSelectedListener) getParentFragment();
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

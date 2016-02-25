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
import com.cerebellio.noted.utils.TextFunctions;

/**
 * Displays a palette of colours from which the user can select
 */
public class DialogSketchColour extends DialogFragment implements IOnColourSelectedListener {

    private static final String LOG_TAG = TextFunctions.makeLogTag(DialogSketchColour.class);

    private IOnColourSelectedListener mIOnColourSelectedListener;

    public DialogSketchColour() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_colour_selection, null);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.dialog_stroke_colour_selection, new FragmentColourSelection())
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

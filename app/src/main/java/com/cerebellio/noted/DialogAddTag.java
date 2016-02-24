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
import android.widget.EditText;
import android.widget.TextView;

import com.cerebellio.noted.models.listeners.IOnTagAddedListener;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Allows ser to enter a tag
 */
public class DialogAddTag extends DialogFragment{

    @InjectView(R.id.dialog_add_tag_add_tag) EditText mEditTag;
    @InjectView(R.id.dialog_add_tag_cancel) TextView mTextCancel;
    @InjectView(R.id.dialog_add_tag_save) TextView mTextSave;

    private static final String LOG_TAG = DialogAddTag.class.getSimpleName();

    private IOnTagAddedListener mIOnTagAddedListener;

    public DialogAddTag() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_tag, null);
        ButterKnife.inject(this, rootView);

        mTextCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mTextSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIOnTagAddedListener.onTagAdded(mEditTag.getText().toString().toLowerCase(Locale.getDefault()));
                dismiss();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mIOnTagAddedListener = (IOnTagAddedListener) getParentFragment();
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "Calling context must implement IOnTagAddedListener");
        }
    }
}

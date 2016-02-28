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
import android.widget.TextView;

import com.cerebellio.noted.models.listeners.IOnDeleteDialogDismissedListener;
import com.cerebellio.noted.utils.TextFunctions;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Prompts user to decide whether to permanently delete item
 */
public class DialogDeleteItem extends DialogFragment {

    @InjectView(R.id.dialog_delete_item_delete)
    TextView mTextDelete;
    @InjectView(R.id.dialog_delete_item_cancel)
    TextView mTextCancel;

    private static final String LOG_TAG = TextFunctions.makeLogTag(DialogDeleteItem.class);

    private IOnDeleteDialogDismissedListener mIOnDeleteDialogDismissedListener;

    public DialogDeleteItem() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_delete_item, null);
        ButterKnife.inject(this, rootView);

        mTextCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mTextDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIOnDeleteDialogDismissedListener.onDelete();
                dismiss();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mIOnDeleteDialogDismissedListener = (IOnDeleteDialogDismissedListener) getParentFragment();
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "Calling context must implement IOnTagAddedListener");
        }
    }

}

package com.cerebellio.noted;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.cerebellio.noted.models.listeners.IOnTagOperationListener;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.TextFunctions;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Allows ser to enter a tag
 */
public class DialogAddTag extends DialogFragment{

    @InjectView(R.id.dialog_add_tag_add_tag) EditText mEditTag;
    @InjectView(R.id.dialog_add_tag_delete) TextView mTextDelete;
    @InjectView(R.id.dialog_add_tag_cancel) TextView mTextCancel;
    @InjectView(R.id.dialog_add_tag_save) TextView mTextSave;

    private static final String LOG_TAG = TextFunctions.makeLogTag(DialogAddTag.class);

    private IOnTagOperationListener mIOnTagOperationListener;
    private InputMethodManager mInputMethodManager;
    private String mOriginalTag;

    private boolean mIsInEditMode = false;

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
                if (getArguments() != null) {
                    mIOnTagOperationListener.onTagEdited(
                            mOriginalTag, mEditTag.getText().toString());
                } else {
                    mIOnTagOperationListener.onTagAdded(mEditTag.getText().toString());
                }
                dismiss();
            }
        });

        mTextDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIOnTagOperationListener.onTagDeleted(mOriginalTag);
                dismiss();
            }
        });

        mEditTag.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                //If user presses 'Done' on keyboard, save the tag
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (getArguments() != null) {
                        mIOnTagOperationListener.onTagEdited(
                                mOriginalTag, mEditTag.getText().toString());
                    } else {
                        mIOnTagOperationListener.onTagAdded(mEditTag.getText().toString());
                    }
                    dismiss();
                }
                return false;
            }
        });

        if (getArguments() != null) {

            //We are editing current tag
            mEditTag.setText(getArguments().getString(Constants.BUNDLE_TAG_VALUE));

            //Place cursor at end of tag
            mEditTag.setSelection(mEditTag.getText().length());

            mOriginalTag = getArguments().getString(Constants.BUNDLE_TAG_VALUE);
            mIsInEditMode = getArguments().getBoolean(Constants.BUNDLE_IS_IN_EDIT_MODE);
        }

        //Shouldn't be able to delete new tag
        mTextDelete.setVisibility(mIsInEditMode ? View.VISIBLE : View.GONE);

        /**
         * Automatically open soft keyboard,
         * pointless to keep it closed because the only thing
         * the user can do on the screen is type
         */
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mInputMethodManager != null) {
            mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mIOnTagOperationListener = (IOnTagOperationListener) getParentFragment();
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "Calling context must implement IOnTagAddedListener");
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        //Dismiss soft keyboard on exit
        if (mInputMethodManager != null) {
            mInputMethodManager.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0);
        }
    }
}

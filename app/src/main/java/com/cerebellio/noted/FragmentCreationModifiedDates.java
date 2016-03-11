package com.cerebellio.noted;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.listeners.IOnDatesLaidOutListener;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.DateFunctions;
import com.cerebellio.noted.utils.TextFunctions;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Displays the dates when the given {@link Item} was created/last edited
 */
public class FragmentCreationModifiedDates extends Fragment {

    @InjectView(R.id.fragment_creation_modified_dates_created_date) TextView mCreatedDate;
    @InjectView(R.id.fragment_creation_modified_dates_last_modified_date) TextView mModifiedDate;

    private static final String LOG_TAG = TextFunctions.makeLogTag(FragmentCreationModifiedDates.class);

    private IOnDatesLaidOutListener mIOnDatesLaidOutListener;

    private boolean mIsFirstLaidOut = false;

    public FragmentCreationModifiedDates() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_creation_modified_dates, container, false);
        ButterKnife.inject(this, rootView);

        long id = getArguments().getLong(Constants.BUNDLE_ITEM_ID);
        Item.Type type = (Item.Type) getArguments().getSerializable(Constants.BUNDLE_ITEM_TYPE);

        SqlDatabaseHelper databaseHelper = new SqlDatabaseHelper(getActivity());

        Item item = databaseHelper.getItemById(id, type);

        mCreatedDate.setText( DateFunctions.getTime(
                getString(R.string.date_created), item.getCreatedDate(), getActivity()));
        mModifiedDate.setText(DateFunctions.getTime(
                getString(R.string.date_last_modified), item.getEditedDate(), getActivity()));

        mCreatedDate.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mCreatedDate.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (mIsFirstLaidOut) {
                    mIOnDatesLaidOutListener.onLaidOut();
                }
                mIsFirstLaidOut = true;
            }
        });

        mModifiedDate.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mModifiedDate.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (mIsFirstLaidOut) {
                    mIOnDatesLaidOutListener.onLaidOut();
                }
                mIsFirstLaidOut = true;
            }
        });

        databaseHelper.closeDB();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mIOnDatesLaidOutListener = (IOnDatesLaidOutListener) getParentFragment();
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "Parent must implement listener");
        }
    }
}

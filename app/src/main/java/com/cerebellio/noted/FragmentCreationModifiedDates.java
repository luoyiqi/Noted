package com.cerebellio.noted;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.DateFunctions;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Sam on 11/02/2016.
 */
public class FragmentCreationModifiedDates extends Fragment {

    @InjectView(R.id.fragment_creation_modified_dates_created_date) TextView mCreatedDate;
    @InjectView(R.id.fragment_creation_modified_dates_last_modified_date) TextView mModifiedDate;

    public FragmentCreationModifiedDates() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_creation_modified_dates, container, false);
        ButterKnife.inject(this, rootView);

        long id = getArguments().getLong(Constants.BUNDLE_ITEM_ID_FOR_DATES_FRAGMENT);
        Item.Type type = (Item.Type) getArguments().getSerializable(Constants.BUNDLE_ITEM_TYPE_FOR_DATES_FRAGMENT);

        SqlDatabaseHelper databaseHelper = new SqlDatabaseHelper(getActivity());

        Item item = databaseHelper.getItemById(id, type);

        String created = DateFunctions.getPrettyTime(
                getString(R.string.date_created),
                item == null ? new Date().getTime() : item.getCreatedDate(),
                getActivity());

        String modified = DateFunctions.getPrettyTime(
                getString(R.string.date_last_modified),
                item == null ? new Date().getTime() : item.getLastModifiedDate(),
                getActivity());


        mCreatedDate.setText(created);
        mModifiedDate.setText(modified);

        databaseHelper.closeDB();

        return rootView;
    }
}

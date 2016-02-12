package com.cerebellio.noted;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.Sketch;
import com.cerebellio.noted.models.listeners.IOnColourSelectedListener;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.views.SketchView;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Sam on 11/02/2016.
 */
public class FragmentAddEditSketch extends Fragment implements IOnColourSelectedListener {

    @InjectView(R.id.fragment_add_edit_sketch_title) TextView mEditTitle;
    @InjectView(R.id.fragment_add_edit_sketch_drawingview) SketchView mSketchView;

    private Sketch mSketch = new Sketch();
    private SqlDatabaseHelper mSqlDatabaseHelper;

    private boolean mIsInEditMode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_edit_sketch, container, false);
        ButterKnife.inject(this, rootView);

        mIsInEditMode = getArguments().getBoolean(Constants.BUNDLE_IS_IN_EDIT_MODE);

        initDrawing();

        FragmentCreationModifiedDates fragmentCreationModifiedDates = new FragmentCreationModifiedDates();
        Bundle bundleDates = new Bundle();
        bundleDates.putLong(Constants.BUNDLE_ITEM_ID_FOR_DATES_FRAGMENT, mSketch.getId());
        bundleDates.putSerializable(Constants.BUNDLE_ITEM_TYPE_FOR_DATES_FRAGMENT, Item.Type.SKETCH);
        fragmentCreationModifiedDates.setArguments(bundleDates);

        FragmentColourSelection fragmentColourSelection = new FragmentColourSelection();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_CURRENT_COLOUR, mSketch.getColour());
        fragmentColourSelection.setArguments(bundle);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_add_edit_sketch_colour_selection_frame, fragmentColourSelection)
                .replace(R.id.fragment_add_edit_sketch_dates_frame, fragmentCreationModifiedDates)
                .commit();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mSqlDatabaseHelper = new SqlDatabaseHelper(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();

        mSketch.setTitle(mEditTitle.getText().toString());
        mSketch.setLastModifiedDate(new Date().getTime());
        mSketch.setBitmapAsByteArray(mSketchView.getBitmapAsByteArray());

        if (mSketch.isEmpty()) {
            mSketch.setIsTrashed(true);
        }

        mSqlDatabaseHelper.addOrEditSketch(mSketch);

        mSqlDatabaseHelper.closeDB();
    }

    @Override
    public void onColourSelected(Integer colour) {
        mSketch.setColour(colour);
        mSketchView.setColour(colour);
    }

    private void initDrawing() {
        SqlDatabaseHelper sqlDatabaseHelper = new SqlDatabaseHelper(getActivity());

        if (mIsInEditMode) {
            mSketch = sqlDatabaseHelper.getSketch(
                    getArguments().getLong(Constants.BUNDLE_DRAWING_TO_EDIT_ID));
            mEditTitle.setText(mSketch.getTitle());

            mSketchView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mSketchView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mSketchView.setCanvasBitmap(mSketch.getBitmap());
                }
            });

        } else {
            mSketch = new Sketch();
            mSketch.setCreatedDate(new Date().getTime());
        }

        mSketchView.setColour(mSketch.getColour());

        sqlDatabaseHelper.closeDB();
    }
}

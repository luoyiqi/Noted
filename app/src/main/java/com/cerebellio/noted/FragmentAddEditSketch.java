package com.cerebellio.noted;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.Sketch;
import com.cerebellio.noted.models.listeners.IOnColourSelectedListener;
import com.cerebellio.noted.models.listeners.IOnSketchActionListener;
import com.cerebellio.noted.models.listeners.IOnStrokeWidthChangedListener;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.UtilityFunctions;
import com.cerebellio.noted.views.SketchView;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Sam on 11/02/2016.
 */
public class FragmentAddEditSketch extends Fragment implements IOnColourSelectedListener,
        IOnStrokeWidthChangedListener, IOnSketchActionListener{

    @InjectView(R.id.fragment_add_edit_sketch_sketchview) SketchView mSketchView;
    @InjectView(R.id.fragment_add_edit_sketch_colour) TextView mTextColour;
    @InjectView(R.id.fragment_add_edit_sketch_paintbrush) ImageView mPaintbrush;
    @InjectView(R.id.fragment_add_edit_sketch_eraser) ImageView mEraser;
    @InjectView(R.id.fragment_add_edit_sketch_undo) ImageView mUndo;
    @InjectView(R.id.fragment_add_edit_sketch_redo) ImageView mRedo;

    private static final int POPUP_PAINT = 0;
    private static final int POPUP_ERASER = 1;

    private Sketch mSketch = new Sketch();
    private SqlDatabaseHelper mSqlDatabaseHelper;

    private int mAlpha;
    private boolean mIsInEditMode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_edit_sketch, container, false);
        ButterKnife.inject(this, rootView);

        mIsInEditMode = getArguments().getBoolean(Constants.BUNDLE_IS_IN_EDIT_MODE);

        initSketch();

        mAlpha = Color.alpha(mSketch.getColour());

        FragmentCreationModifiedDates fragmentCreationModifiedDates = new FragmentCreationModifiedDates();
        Bundle bundleDates = new Bundle();
        bundleDates.putLong(Constants.BUNDLE_ITEM_ID_FOR_DATES_FRAGMENT, mSketch.getId());
        bundleDates.putSerializable(Constants.BUNDLE_ITEM_TYPE_FOR_DATES_FRAGMENT, Item.Type.SKETCH);
        fragmentCreationModifiedDates.setArguments(bundleDates);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_add_edit_sketch_dates_frame, fragmentCreationModifiedDates)
                .commit();

        mTextColour.setBackgroundColor(mSketch.getColour());

        mTextColour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogSketchColour().show(getChildFragmentManager(), null);
            }
        });

        mPaintbrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSketchView.getStrokeType().equals(SketchView.StrokeType.STROKE)) {
                    showPopup(view, POPUP_PAINT);
                } else {
                    mSketchView.setStrokeType(SketchView.StrokeType.STROKE);
                    switchStrokeTypeViews(SketchView.StrokeType.STROKE);
                }
            }
        });

        mEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSketchView.getStrokeType().equals(SketchView.StrokeType.ERASER)) {
                    showPopup(view, POPUP_ERASER);
                } else {
                    mSketchView.setStrokeType(SketchView.StrokeType.ERASER);
                    switchStrokeTypeViews(SketchView.StrokeType.ERASER);
                }
            }
        });

        mUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSketchView.undoAction();
            }
        });

        mRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSketchView.redoAction();
            }
        });

        switchStrokeTypeViews(mSketchView.getStrokeType());

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mSketchView.hasChangeBeenMade()) {
            mSketch.setLastModifiedDate(new Date().getTime());
        }

        mSketch.setBitmapAsByteArray(mSketchView.getBitmapAsByteArray());

        if (mSketch.isEmpty()) {
            mSketch.setStatus(Item.Status.DELETED);
        }

        mSqlDatabaseHelper.addOrEditSketch(mSketch);

        mSqlDatabaseHelper.closeDB();
    }

    @Override
    public void onColourSelected(Integer colour) {
        mSketch.setColour(UtilityFunctions.adjustAlpha(colour, mAlpha));
        mSketchView.setColour(UtilityFunctions.adjustAlpha(colour, mAlpha));
        mTextColour.setBackgroundColor(UtilityFunctions.adjustAlpha(colour, mAlpha));
    }

    @Override
    public void onStrokeWidthChanged(int width) {
        mSketchView.setStrokeSize(width);
    }

    @Override
    public void onChange() {
        Animation popOut = AnimationUtils.loadAnimation(getActivity(), R.anim.pop_out);
        Animation popIn = AnimationUtils.loadAnimation(getActivity(), R.anim.pop_in);

        if (mSketchView.isUndoAvailable()) {
            if (!mUndo.isEnabled()) {
                mUndo.setEnabled(true);
                mUndo.startAnimation(popOut);
            }
        } else {
            if (mUndo.isEnabled()) {
                mUndo.startAnimation(popIn);
                mUndo.setEnabled(false);
            }
        }

        if (mSketchView.isRedoAvailable()) {
            if (!mRedo.isEnabled()) {
                mRedo.setEnabled(true);
                mRedo.startAnimation(popOut);
            }
        } else {
            if (mRedo.isEnabled()) {
                mRedo.startAnimation(popIn);
                mRedo.setEnabled(false);
            }
        }
    }

    private void initSketch() {
        mSqlDatabaseHelper = new SqlDatabaseHelper(getActivity());

        if (mIsInEditMode) {
            mSketch = (Sketch) mSqlDatabaseHelper.getItemById(
                    getArguments().getLong(Constants.BUNDLE_ITEM_TO_EDIT_ID), Item.Type.SKETCH);

            mSketchView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mSketchView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mSketchView.setCanvasBitmap(mSketch.getBitmap());
                }
            });

        } else {
            mSketch = (Sketch) mSqlDatabaseHelper.getItemById(
                    mSqlDatabaseHelper.addBlankSketch(), Item.Type.SKETCH);
        }

        mSketchView.setColour(mSketch.getColour());
        mSketchView.setIOnSketchActionListener(this);
    }

    private void showPopup(final View anchor, int type) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(AppCompatActivity
            .LAYOUT_INFLATER_SERVICE);
        View popupLayout = inflater.inflate(R.layout.popup_stroke, null);

        PopupWindow popup = new PopupWindow(getActivity());
        popup.setContentView(popupLayout);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);
        popup.setBackgroundDrawable(new BitmapDrawable(
                getResources(), Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)));
        popup.showAsDropDown(anchor);

        SeekBar seekWidth = (SeekBar) popupLayout.findViewById(R.id.popup_stroke_thickness_seekbar);
        seekWidth.setProgress(mSketchView.getStrokeSize());
        seekWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mSketchView.setStrokeSize(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar seekAlpha = (SeekBar) popupLayout.findViewById(R.id.popup_stroke_alpha_seekbar);
        TextView textAlpha = (TextView) popupLayout.findViewById(R.id.popup_stroke_alpha_seekbar_label);
        seekAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                mAlpha = progress;
                onColourSelected(mSketch.getColour());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekAlpha.setProgress(mAlpha);

        seekAlpha.setVisibility(type == POPUP_PAINT ? View.VISIBLE : View.GONE);
        textAlpha.setVisibility(type == POPUP_PAINT ? View.VISIBLE : View.GONE);

    }

    private void switchStrokeTypeViews(SketchView.StrokeType strokeType) {
        Animation popOut = AnimationUtils.loadAnimation(getActivity(), R.anim.pop_out);
        Animation popIn = AnimationUtils.loadAnimation(getActivity(), R.anim.pop_in);

        if (strokeType.equals(SketchView.StrokeType.STROKE)) {
//            mPaintbrush.setAlpha(1f);
//            mEraser.setAlpha(0.25f);
            mPaintbrush.startAnimation(popOut);
            mEraser.startAnimation(popIn);
        } else {
//            mEraser.setAlpha(1f);
//            mPaintbrush.setAlpha(0.25f);
            mPaintbrush.startAnimation(popIn);
            mEraser.startAnimation(popOut);
        }
    }
}

package com.cerebellio.noted;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.cerebellio.noted.models.events.TitleChangedEvent;
import com.cerebellio.noted.models.listeners.IOnColourSelectedListener;
import com.cerebellio.noted.models.listeners.IOnSketchActionListener;
import com.cerebellio.noted.utils.ColourFunctions;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.FileFunctions;
import com.cerebellio.noted.utils.TextFunctions;
import com.cerebellio.noted.views.SketchView;

import java.io.IOException;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Allows user to create a {@link Sketch} or edit an existing one
 */
public class FragmentAddEditSketch extends FragmentBase implements IOnColourSelectedListener,
         IOnSketchActionListener{

    @InjectView(R.id.fragment_add_edit_sketch_sketchview) SketchView mSketchView;
    @InjectView(R.id.fragment_add_edit_sketch_colour) TextView mTextColour;
    @InjectView(R.id.fragment_add_edit_sketch_paintbrush) ImageView mPaintbrush;
    @InjectView(R.id.fragment_add_edit_sketch_eraser) ImageView mEraser;
    @InjectView(R.id.fragment_add_edit_sketch_undo) ImageView mUndo;
    @InjectView(R.id.fragment_add_edit_sketch_redo) ImageView mRedo;

    private static final String LOG_TAG = TextFunctions.makeLogTag(FragmentAddEditSketch.class);

    private static final int POPUP_PAINT = 0;
    private static final int POPUP_ERASER = 1;

    private Sketch mSketch = new Sketch();
    private SqlDatabaseHelper mSqlDatabaseHelper;

    //Current alpha value as provided by popup
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

        //Set colour selection View to current colour
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
                    toggleStrokeTypeViews(SketchView.StrokeType.STROKE);
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
                    toggleStrokeTypeViews(SketchView.StrokeType.ERASER);
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

        toggleStrokeTypeViews(mSketchView.getStrokeType());

        ApplicationNoted.bus.post(new TitleChangedEvent(
                mIsInEditMode ? getString(R.string.title_sketch_edit) : getString(R.string.title_sketch_new)));

        return rootView;
    }

    @Override
    public void onPause() {

        super.onPause();

        if (mSketchView.hasChangeBeenMade()) {
            mSketch.setEditedDate(new Date().getTime());
        }

        if (mSketch.isEmpty() && !mSketchView.hasChangeBeenMade()) {
            mSketch.setStatus(Item.Status.DELETED);
        } else {

            try {
                mSketch.setImagePath(FileFunctions.saveSketchToStorage(
                        mSketchView.getSketch(), "sketches", getActivity()));
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error saving image to file");
            }
        }

        mSqlDatabaseHelper.addOrEditSketch(mSketch);

        mSqlDatabaseHelper.closeDB();
    }

    @Override
    public void onColourSelected(Integer colour) {

        int newColour = ColourFunctions.adjustAlpha(colour, mAlpha);

        mSketch.setColour(newColour);
        mSketchView.setColour(newColour);
        mTextColour.setBackgroundColor(newColour);
    }

    @Override
    public void onChange() {

        Animation popOut = AnimationUtils.loadAnimation(getActivity(), R.anim.jump_out);
        Animation popIn = AnimationUtils.loadAnimation(getActivity(), R.anim.jump_in);

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

    /**
     * Performs operation after determining whether we are editing an existing {@link Sketch}
     * or creating a new one
     */
    private void initSketch() {

        mSqlDatabaseHelper = new SqlDatabaseHelper(getActivity());

        if (mIsInEditMode) {

            mSketch = (Sketch) mSqlDatabaseHelper.getItemById(
                    getArguments().getLong(Constants.BUNDLE_ITEM_TO_EDIT_ID), Item.Type.SKETCH);

            mSketchView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    //When SketchView is laid out we paint the image to its canvas
                    //If we don't wait for this the image isn't drawn
                    mSketchView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mSketchView.setCanvasBitmap(FileFunctions.getBitmapFromFile(mSketch.getImagePath()));
                }
            });

        } else {

            mSketch = (Sketch) mSqlDatabaseHelper.getItemById(
                    mSqlDatabaseHelper.addBlankSketch(), Item.Type.SKETCH);
        }

        mSketchView.setColour(mSketch.getColour());
        mSketchView.setIOnSketchActionListener(this);
    }

    /**
     * Creates a popup which allows user to select width/opacity of the paint stroke
     *
     * @param anchor            View from which popup should originate
     * @param type              either {@link #POPUP_PAINT} or {@link #POPUP_ERASER}
     */
    private void showPopup(final View anchor, int type) {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(AppCompatActivity
            .LAYOUT_INFLATER_SERVICE);
        View popupLayout = inflater.inflate(R.layout.popup_stroke, null);

        //incorrect type passed, default to paint stroke
        if (type != POPUP_PAINT && type != POPUP_ERASER) {
            type = POPUP_PAINT;
        }

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

        //We don't need to be able to change alpha if the eraser is the current tool
        seekAlpha.setVisibility(type == POPUP_PAINT ? View.VISIBLE : View.GONE);
        textAlpha.setVisibility(type == POPUP_PAINT ? View.VISIBLE : View.GONE);
    }

    /**
     * Switches the focus between Paint and Eraser views
     *
     * @param requestedType     {@link com.cerebellio.noted.views.SketchView.StrokeType} current type
     */
    private void toggleStrokeTypeViews(SketchView.StrokeType requestedType) {

        Animation popOut = AnimationUtils.loadAnimation(getActivity(), R.anim.jump_out);
        Animation popIn = AnimationUtils.loadAnimation(getActivity(), R.anim.jump_in);

        if (requestedType.equals(SketchView.StrokeType.STROKE)) {
            mPaintbrush.startAnimation(popOut);
            mEraser.startAnimation(popIn);
        } else {
            mPaintbrush.startAnimation(popIn);
            mEraser.startAnimation(popOut);
        }
    }
}

package com.cerebellio.noted;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.Sketch;
import com.cerebellio.noted.models.adapters.TagsAdapter;
import com.cerebellio.noted.models.events.TagEvent;
import com.cerebellio.noted.models.listeners.IOnColourSelectedListener;
import com.cerebellio.noted.models.listeners.IOnDeleteDialogDismissedListener;
import com.cerebellio.noted.models.listeners.IOnItemFocusNeedsUpdatingListener;
import com.cerebellio.noted.models.listeners.IOnItemSelectedToEditListener;
import com.cerebellio.noted.models.listeners.IOnTagOperationListener;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.FileFunctions;
import com.cerebellio.noted.utils.TextFunctions;
import com.cerebellio.noted.utils.UtilityFunctions;
import com.cerebellio.noted.views.FilteredIconView;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Provides the user a number of operations for the selected Item
 */
public class DialogItemFocus extends DialogFragment
        implements IOnTagOperationListener, IOnColourSelectedListener, IOnDeleteDialogDismissedListener {

    @InjectView(R.id.dialog_item_focus_add_tag_frame)
    LinearLayout mTagFrame;
    @InjectView(R.id.dialog_item_focus_current_tags_expand_collapse)
    FilteredIconView mTagExpandCollapse;
    @InjectView(R.id.dialog_item_focus_current_tags)
    TextView mTextCurrentTags;
    @InjectView(R.id.dialog_item_focus_tag_recycler)
    RecyclerView mTagsRecycler;
    @InjectView(R.id.dialog_item_focus_edit_item_frame)
    LinearLayout mEditItemFrame;
    @InjectView(R.id.dialog_item_focus_important)
    ImageView mImportant;
    @InjectView(R.id.dialog_item_focus_pinboard)
    ImageView mPinboard;
    @InjectView(R.id.dialog_item_focus_archive)
    ImageView mArchive;
    @InjectView(R.id.dialog_item_focus_trash)
    ImageView mTrash;
    @InjectView(R.id.dialog_item_focus_delete)
    ImageView mDelete;
    @InjectView(R.id.dialog_item_focus_colour)
    TextView mTextColour;
    @InjectView(R.id.dialog_item_focus_colour_frame)
    FrameLayout mFrameColour;

    private static final String LOG_TAG = TextFunctions.makeLogTag(DialogItemFocus.class);

    private static final int NUM_TAG_COLUMNS = 4;

    private IOnItemSelectedToEditListener mIOnItemSelectedToEditListener;
    private IOnItemFocusNeedsUpdatingListener mIOnItemFocusNeedsUpdatingListener;

    private TagsAdapter mAdapter;
    private Item mItem;
    private SqlDatabaseHelper mDatabaseHelper;

    private int mPosition;
    private boolean mIsTagFrameExpanded = false;

    public DialogItemFocus() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_item_focus, null);
        ButterKnife.inject(this, rootView);

        //Parse arguments
        long id = getArguments().getLong(Constants.BUNDLE_ITEM_ID);
        Item.Type type = (Item.Type) getArguments().getSerializable(Constants.BUNDLE_ITEM_TYPE);
        mPosition = getArguments().getInt(Constants.BUNDLE_ITEM_POSITION);

        mDatabaseHelper = new SqlDatabaseHelper(getActivity());
        mItem = mDatabaseHelper.getItemById(id, type);

        //Pass tag string to adapter and display in RecyclerView
        mAdapter = new TagsAdapter(getActivity(), mItem.getRawTagString());
        UtilityFunctions.setUpWrapContentGridRecycler(
                getActivity(), mTagsRecycler, mAdapter, NUM_TAG_COLUMNS);

        mTextColour.setBackgroundColor(mItem.getColour());
        mTextColour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogSketchColour().show(getChildFragmentManager(), null);
            }
        });

        //Colour isn't relevant for sketches in the same way it is for notes and checklists
        mTextColour.setVisibility(mItem instanceof Sketch ? View.GONE : View.VISIBLE);
        mFrameColour.setVisibility(mItem instanceof Sketch ? View.GONE : View.VISIBLE);

        mTagFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsTagFrameExpanded = !mIsTagFrameExpanded;
                mTagsRecycler.setVisibility(mIsTagFrameExpanded ? View.VISIBLE : View.GONE);

                //This animation causes the collapse/expand arrow to rotate
                //in the required direction, i.e. when the tag draw is open
                //it points upwards, when closed it points down
                RotateAnimation animation = new RotateAnimation(
                        mIsTagFrameExpanded ? 0 : 180, mIsTagFrameExpanded ? 180 : 360,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
                animation.setInterpolator(new DecelerateInterpolator());
                animation.setFillAfter(true);
                mTagExpandCollapse.startAnimation(animation);
            }
        });

        mEditItemFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIOnItemSelectedToEditListener.onItemToEdit(mItem);
                dismiss();
            }
        });

        initDatesFragment();

        //Set visibilities depending on Item.Status
        //i.e. if an item is archived, the archive icon should be removed
        mPinboard.setVisibility(mItem.canBePinboarded() ? View.VISIBLE : View.GONE);
        mArchive.setVisibility(mItem.canBeArchived() ? View.VISIBLE : View.GONE);
        mTrash.setVisibility(mItem.canBeTrashed() ? View.VISIBLE : View.GONE);
        mDelete.setVisibility(mItem.canBeDeleted() ? View.VISIBLE : View.GONE);

        mPinboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItem.setStatus(Item.Status.PINBOARD);
                mIOnItemFocusNeedsUpdatingListener.onRemove(mPosition);
                dismiss();
            }
        });

        mArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItem.setStatus(Item.Status.ARCHIVED);
                mIOnItemFocusNeedsUpdatingListener.onRemove(mPosition);
                dismiss();
            }
        });

        mTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItem.setStatus(Item.Status.TRASHED);
                mIOnItemFocusNeedsUpdatingListener.onRemove(mPosition);
                dismiss();
            }
        });

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogDeleteItem().show(getChildFragmentManager(), null);
            }
        });

        mImportant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItem.setIsImportant(!mItem.isImportant());
                colourImportantView();
            }
        });

        updateTagTextView();
        colourImportantView();

        return rootView;
    }

    @Override
    public void onResume() {

        super.onResume();

        ApplicationNoted.bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        mDatabaseHelper.addOrEditItem(mItem);
        mDatabaseHelper.closeDB();

        ApplicationNoted.bus.unregister(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mIOnItemSelectedToEditListener = (IOnItemSelectedToEditListener) getParentFragment();
            mIOnItemFocusNeedsUpdatingListener = (IOnItemFocusNeedsUpdatingListener) getParentFragment();
        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "Calling context must implement IOnItemSelectedListener");
        }
    }

    @Override
    public void onTagAdded(String tag) {
        mItem.addTag(tag);
        mAdapter.setTagsList(mItem.getRawTagString());
        updateTagTextView();
    }

    @Override
    public void onTagEdited(String originalTag, String newTag) {
        mItem.editTag(originalTag, newTag);
        mAdapter.setTagsList(mItem.getRawTagString());
        updateTagTextView();
    }

    @Override
    public void onTagDeleted(String tag) {
        mItem.deleteTag(tag);
        mAdapter.setTagsList(mItem.getRawTagString());
        updateTagTextView();
    }

    @Override
    public void onColourSelected(Integer colour) {
        mItem.setColour(colour);
        mTextColour.setBackgroundColor(colour);
        mIOnItemFocusNeedsUpdatingListener.onUpdateColour(mPosition, colour);
    }

    @Override
    public void onDelete() {
        mItem.setStatus(Item.Status.DELETED);
        mIOnItemFocusNeedsUpdatingListener.onRemove(mPosition);

        if (mItem instanceof Sketch) {
            FileFunctions.deleteSketchFromStorage(((Sketch) mItem).getImagePath());
        }

        dismiss();
    }

    @Subscribe
    public void receiveTagEvent(TagEvent event) {

        if (event.getType().equals(TagEvent.Type.ADD)) {
            new DialogAddTag().show(getChildFragmentManager(), null);
        } else {
            DialogAddTag dialogAddTag = new DialogAddTag();
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.BUNDLE_IS_IN_EDIT_MODE, true);
            bundle.putString(Constants.BUNDLE_TAG_VALUE, event.getTag());
            dialogAddTag.setArguments(bundle);
            dialogAddTag.show(getChildFragmentManager(), null);
        }
    }

    private void colourImportantView() {
        mImportant.setColorFilter(mItem.isImportant()
                ? ContextCompat.getColor(getActivity(),
                        UtilityFunctions.getResIdFromAttribute(R.attr.colorAccent, getActivity()))
                : ContextCompat.getColor(getActivity(),
                        UtilityFunctions.getResIdFromAttribute(R.attr.textColorPrimary, getActivity())),
                PorterDuff.Mode.MULTIPLY);
    }

    /**
     * Initialise fragment which displays creation and last edited dates of {@link Item}
     */
    private void initDatesFragment() {

        FragmentCreationModifiedDates fragmentCreationModifiedDates = new FragmentCreationModifiedDates();
        Bundle bundleDates = new Bundle();
        bundleDates.putLong(Constants.BUNDLE_ITEM_ID, mItem.getId());
        bundleDates.putSerializable(Constants.BUNDLE_ITEM_TYPE, mItem.getItemType());
        fragmentCreationModifiedDates.setArguments(getArguments());

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.dialog_item_focus_dates_frame, fragmentCreationModifiedDates)
                .commit();
    }

    /**
     * Populated View which lists tags of selected {@link Item}
     */
    private void updateTagTextView() {

        if (!mItem.areTagsEmpty()) {
            mTextCurrentTags.setText(mItem.getFormattedTagString());
        } else {

            //No tags so display default text from String resources
            mTextCurrentTags.setText(
                    getParentFragment().getString(R.string.dialog_item_focus_add_tags));
        }
    }
}

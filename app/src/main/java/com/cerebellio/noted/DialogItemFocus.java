package com.cerebellio.noted;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.helpers.AnimationsHelper;
import com.cerebellio.noted.helpers.PreferenceHelper;
import com.cerebellio.noted.helpers.ReminderHelper;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.Reminder;
import com.cerebellio.noted.models.Sketch;
import com.cerebellio.noted.models.adapters.TagsAdapter;
import com.cerebellio.noted.models.events.TagEvent;
import com.cerebellio.noted.models.listeners.IOnColourSelectedListener;
import com.cerebellio.noted.models.listeners.IOnDatesLaidOutListener;
import com.cerebellio.noted.models.listeners.IOnDeleteDialogDismissedListener;
import com.cerebellio.noted.models.listeners.IOnItemFocusNeedsUpdatingListener;
import com.cerebellio.noted.models.listeners.IOnItemSelectedToEditListener;
import com.cerebellio.noted.models.listeners.IOnTagOperationListener;
import com.cerebellio.noted.utils.ColourFunctions;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.DateFunctions;
import com.cerebellio.noted.utils.FileFunctions;
import com.cerebellio.noted.utils.TextFunctions;
import com.cerebellio.noted.utils.UtilityFunctions;
import com.cerebellio.noted.views.FilteredIconView;
import com.squareup.otto.Subscribe;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Provides the user a number of operations for the selected Item
 */
public class DialogItemFocus extends DialogFragment
        implements IOnTagOperationListener, IOnColourSelectedListener, IOnDeleteDialogDismissedListener,
        IOnDatesLaidOutListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    @InjectView(R.id.dialog_item_focus_container)
    LinearLayout mContainer;
    @InjectView(R.id.dialog_item_focus_add_tag_frame)
    LinearLayout mTagFrame;
    @InjectView(R.id.dialog_item_focus_current_tags_expand_collapse)
    FilteredIconView mTagExpandCollapse;
    @InjectView(R.id.dialog_item_focus_current_tags)
    TextView mTextCurrentTags;
    @InjectView(R.id.dialog_item_focus_tag_recycler)
    RecyclerView mTagsRecycler;
    @InjectView(R.id.dialog_item_focus_reminder_frame)
    LinearLayout mReminderFrame;
    @InjectView(R.id.dialog_item_focus_reminder)
    TextView mTextReminder;
    @InjectView(R.id.dialog_item_focus_reminder_add_remove)
    FilteredIconView mReminderRemove;
    @InjectView(R.id.dialog_item_focus_reminder_recurrence_frame)
    LinearLayout mReminderRecurrenceFrame;
    @InjectView(R.id.dialog_item_focus_reminder_recurrence_add_expand_collapse)
    FilteredIconView mRecurrenceExpandCollapse;
    @InjectView(R.id.dialog_item_focus_reminder_recurrence)
    TextView mTextReminderRecurrence;
    @InjectView(R.id.dialog_item_focus_reminder_recurrence_selection_frame)
    LinearLayout mReminderRecurrenceSelectionFrame;
    @InjectView(R.id.dialog_item_focus_reminder_recurrence_never)
    TextView mTextRecurrenceNever;
    @InjectView(R.id.dialog_item_focus_reminder_recurrence_daily)
    TextView mTextRecurrenceDaily;
    @InjectView(R.id.dialog_item_focus_reminder_recurrence_weekly)
    TextView mTextRecurrenceWeekly;
    @InjectView(R.id.dialog_item_focus_reminder_recurrence_monthly)
    TextView mTextRecurrenceMonthly;
    @InjectView(R.id.dialog_item_focus_reminder_recurrence_annually)
    TextView mTextRecurrenceAnnually;
    @InjectView(R.id.dialog_item_focus_edit_item_frame)
    LinearLayout mEditItemFrame;
    @InjectView(R.id.dialog_item_focus_locked)
    ImageView mLocked;
    @InjectView(R.id.dialog_item_focus_important)
    ImageView mImportant;
    @InjectView(R.id.dialog_item_focus_pinboard)
    ImageView mPinboard;
    @InjectView(R.id.dialog_item_focus_archive)
    ImageView mArchive;
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
    private ReminderHelper mReminderHelper;

    private int mPosition;
    private boolean mIsTagFrameExpanded = false;
    private boolean mIsRecurrenceSelectionFrameExpanded = false;

    public DialogItemFocus() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        final View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_item_focus, null);
        ButterKnife.inject(this, rootView);

        //Parse arguments
        long id = getArguments().getLong(Constants.BUNDLE_ITEM_ID);
        Item.Type type = (Item.Type) getArguments().getSerializable(Constants.BUNDLE_ITEM_TYPE);
        mPosition = getArguments().getInt(Constants.BUNDLE_ITEM_POSITION);

        mDatabaseHelper = new SqlDatabaseHelper(getActivity());
        mItem = mDatabaseHelper.getItemById(id, type);

        mReminderHelper = new ReminderHelper(getActivity(), mItem.getReminder());

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
                AnimationsHelper.halfRotate(mTagExpandCollapse, mIsTagFrameExpanded ? 0 : 180,
                        getResources().getInteger(android.R.integer.config_longAnimTime), true);
            }
        });

        mReminderFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectReminderDate();
            }
        });

        mReminderRecurrenceFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsRecurrenceSelectionFrameExpanded = !mIsRecurrenceSelectionFrameExpanded;
                mReminderRecurrenceSelectionFrame.setVisibility(mIsRecurrenceSelectionFrameExpanded ? View.VISIBLE : View.GONE);

                AnimationsHelper.halfRotate(mRecurrenceExpandCollapse,
                        mIsRecurrenceSelectionFrameExpanded ? 0 : 180,
                        getResources().getInteger(android.R.integer.config_longAnimTime), true);
            }
        });

        mTextRecurrenceNever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItem.getReminder().setRecurrenceRule(Reminder.RecurrenceRule.NEVER);
                updateReminderViews();
            }
        });

        mTextRecurrenceDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItem.getReminder().setRecurrenceRule(Reminder.RecurrenceRule.DAILY);
                updateReminderViews();
            }
        });

        mTextRecurrenceWeekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItem.getReminder().setRecurrenceRule(Reminder.RecurrenceRule.WEEKLY);
                updateReminderViews();
            }
        });


        mTextRecurrenceMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItem.getReminder().setRecurrenceRule(Reminder.RecurrenceRule.MONTHLY);
                updateReminderViews();
            }
        });


        mTextRecurrenceAnnually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItem.getReminder().setRecurrenceRule(Reminder.RecurrenceRule.ANNUALLY);
                updateReminderViews();
            }
        });

        updateReminderViews();

        mReminderRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItem.getReminder().isEmpty()) {
                    selectReminderDate();
                } else {
                    mItem.getReminder().setTime(Reminder.DEFAULT_TIME);
                    mReminderHelper.cancel();
                }
                updateReminderViews();
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

        mLocked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimationsHelper.fullRotate(mLocked,
                        getResources().getInteger(android.R.integer.config_shortAnimTime), true);
                mItem.setIsLocked(!mItem.isLocked());
                onLockToggle();
            }
        });

        mPinboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIOnItemFocusNeedsUpdatingListener.onRemove(mItem.getStatus(), Item.Status.PINBOARD, mItem, mPosition);
                mItem.setStatus(Item.Status.PINBOARD);
                dismiss();
            }
        });

        mArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIOnItemFocusNeedsUpdatingListener.onRemove(mItem.getStatus(), Item.Status.ARCHIVED, mItem, mPosition);
                mItem.setStatus(Item.Status.ARCHIVED);
                dismiss();
            }
        });

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PreferenceHelper.getPrefBehaviourConfirmDelete(getActivity())) {
                    new DialogDeleteItem().show(getChildFragmentManager(), null);
                } else {
                    onDelete();
                }
            }
        });

        mImportant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItem.setIsImportant(!mItem.isImportant());

                AnimationsHelper.fullRotate(mImportant,
                        getResources().getInteger(android.R.integer.config_shortAnimTime), true);
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
        mReminderHelper.broadcast();
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
        mIOnItemFocusNeedsUpdatingListener.onRemove(mItem.getStatus(), Item.Status.DELETED, mItem, mPosition);
        mItem.setStatus(Item.Status.DELETED);

        if (mItem instanceof Sketch) {
            FileFunctions.deleteSketchFromStorage(((Sketch) mItem).getImagePath());
        }

        dismiss();
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        mReminderHelper.setYear(year);
        mReminderHelper.setMonth(month);
        mReminderHelper.setDay(day);

        //Allow user to select specific time
        selectReminderTime();
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute, int second) {
        mReminderHelper.setHour(hour);
        mReminderHelper.setMinute(minute);

        mItem.getReminder().setTime(mReminderHelper.convertToUnixTime());

        if (mReminderHelper.convertToUnixTime() < System.currentTimeMillis()) {
            mReminderHelper.cancel();
            mItem.getReminder().setTime(Reminder.DEFAULT_TIME);
            Toast.makeText(getActivity(), "Reminder must be set in the future!", Toast.LENGTH_SHORT).show();
        }

        updateReminderViews();
    }

    @Override
    public void onLaidOut() {
        onLockToggle();
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
                ? ColourFunctions.getAccentColour(getActivity())
                : ColourFunctions.getPrimaryTextColour(getActivity()),
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
            mTextCurrentTags.setText(mItem.getFormattedTagString(""));
        } else {

            //No tags so display default text from String resources
            mTextCurrentTags.setText(
                    getParentFragment().getString(R.string.dialog_item_focus_add_tags));
        }
    }

    /**
     * Performs View manipulation on lock toggle
     */
    private void onLockToggle() {
        final float LOCKED_ALPHA = 0.1f;

        List<View> views = UtilityFunctions.getAllViewsInFrame(mContainer);

        if (mItem.isLocked()) {
            mLocked.setImageResource(R.drawable.ic_lock_closed);
        } else {
            mLocked.setImageResource(R.drawable.ic_lock_open);
        }

        for (View currentView : views) {
            if (currentView != mLocked) {
                currentView.setEnabled(!mItem.isLocked());
                if (!(currentView instanceof ViewGroup)) {
                    if (mItem.isLocked()) {
                        currentView.setAlpha(LOCKED_ALPHA);
                    } else {
                        currentView.setAlpha(1f);
                    }
                }
            }
        }

        //Special case, it's a container but we want to treat it as though it isn't
        mFrameColour.setAlpha(mItem.isLocked() ? LOCKED_ALPHA : 1f);

    }

    /**
     * Changes reminder text and recurrence highlight
     */
    private void updateReminderViews() {
        mTextReminder.setText(mItem.getReminder().isEmpty()
                ? getString(R.string.dialog_item_focus_add_reminder)
                : DateFunctions.getTime("", mItem.getReminder().getTime(), getActivity()));
        mReminderRemove.setImageResource(mItem.getReminder().isEmpty()
                ? R.drawable.ic_add : R.drawable.ic_remove);
        mTextReminderRecurrence.setText(mItem.getReminder().getRecurrenceText(getActivity()));

        switch (mItem.getReminder().getRecurrenceRule()) {
            case NEVER:
                swapRecurrenceHighlight(mTextRecurrenceNever);
                break;
            case DAILY:
                swapRecurrenceHighlight(mTextRecurrenceDaily);
                break;
            case WEEKLY:
                swapRecurrenceHighlight(mTextRecurrenceWeekly);
                break;
            case MONTHLY:
                swapRecurrenceHighlight(mTextRecurrenceMonthly);
                break;
            case ANNUALLY:
                swapRecurrenceHighlight(mTextRecurrenceAnnually);
                break;
        }
    }

    /**
     * Highlight the given TextView and unhighlight the others
     *
     * @param toHighlight           TextView to highlight
     */
    private void swapRecurrenceHighlight(TextView toHighlight) {
        deselectRecurrence(mTextRecurrenceNever);
        deselectRecurrence(mTextRecurrenceDaily);
        deselectRecurrence(mTextRecurrenceWeekly);
        deselectRecurrence(mTextRecurrenceMonthly);
        deselectRecurrence(mTextRecurrenceAnnually);

        toHighlight.setBackgroundColor(ColourFunctions.getAccentColour(getActivity()));
        toHighlight.setTextColor(ColourFunctions.getPrimaryTextColour(getActivity()));
    }

    /**
     * Unhighlight given TextView
     *
     * @param textView              TextView to unhighlight
     */
    private void deselectRecurrence(TextView textView) {
        textView.setBackgroundColor(ColourFunctions.getBackgroundColour(getActivity()));
        textView.setTextColor(ColourFunctions.getTertiaryTextColour(getActivity()));
    }

    /**
     * Open {@link DatePickerDialog}
     */
    private void selectReminderDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(DialogItemFocus.this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        datePickerDialog.vibrate(PreferenceHelper.getPrefVibration(getActivity()));
        datePickerDialog.setMinDate(calendar);
        datePickerDialog.setYearRange(calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR) + 10);
        datePickerDialog.setThemeDark(UtilityFunctions.isConsideredDarkTheme(getActivity()));
        datePickerDialog.show(getActivity().getFragmentManager(), null);
    }

    /**
     * Open {@link TimePickerDialog}
     */
    private void selectReminderTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(DialogItemFocus.this,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        timePickerDialog.vibrate(PreferenceHelper.getPrefVibration(getActivity()));
        timePickerDialog.setThemeDark(UtilityFunctions.isConsideredDarkTheme(getActivity()));
        timePickerDialog.show(getActivity().getFragmentManager(), null);
    }
}

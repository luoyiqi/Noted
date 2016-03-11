package com.cerebellio.noted.models;

import android.content.Context;

import com.cerebellio.noted.R;

import java.util.Calendar;

/**
 * Represents a single reminder, with a unique id and time
 */
public class Reminder {

    public static final long DEFAULT_TIME = -1;

    private RecurrenceRule mRecurrenceRule;

    private long mId = -1;

    /**
     * Stored as millis since Epoch
     */
    private long mTime = DEFAULT_TIME;

    public enum RecurrenceRule {
        NEVER,
        DAILY,
        WEEKLY,
        MONTHLY,
        ANNUALLY
    }

    public Reminder() {
    }

    /**
     * Checks whether this {@link Reminder} is empty
     *
     * @return          true iff empty
     */
    public boolean isEmpty() {
        return mTime == DEFAULT_TIME;
    }

    public void fire() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mTime);
        switch (mRecurrenceRule) {
            case NEVER:
                mTime = DEFAULT_TIME;
                break;
            case DAILY:
                calendar.add(Calendar.DATE, 1);
                mTime = calendar.getTimeInMillis();
                break;
            case WEEKLY:
                calendar.add(Calendar.DATE, 7);
                mTime = calendar.getTimeInMillis();
                break;
            case MONTHLY:
                calendar.add(Calendar.MONTH, 1);
                mTime = calendar.getTimeInMillis();
                break;
            case ANNUALLY:
                calendar.add(Calendar.YEAR, 1);
                mTime = calendar.getTimeInMillis();
                break;
        }
    }

    public String getRecurrenceText(Context context) {
        switch (mRecurrenceRule) {
            default:
            case NEVER:
                return context.getString(R.string.dialog_item_focus_reminder_recurrence_never_title);
            case DAILY:
                return context.getString(R.string.dialog_item_focus_reminder_recurrence_daily_title);
            case WEEKLY:
                return context.getString(R.string.dialog_item_focus_reminder_recurrence_weekly_title);
            case MONTHLY:
                return context.getString(R.string.dialog_item_focus_reminder_recurrence_monthly_title);
            case ANNUALLY:
                return context.getString(R.string.dialog_item_focus_reminder_recurrence_annually_title);
        }
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public RecurrenceRule getRecurrenceRule() {
        return mRecurrenceRule;
    }

    public void setRecurrenceRule(RecurrenceRule recurrenceRule) {
        mRecurrenceRule = recurrenceRule;
    }
}

package com.cerebellio.noted.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.cerebellio.noted.async.ReminderReceiver;
import com.cerebellio.noted.models.Reminder;
import com.cerebellio.noted.utils.Constants;

import java.util.Calendar;

/**
 * Helper class to store elements of a calendar instance for a {@link Reminder}
 */
public class ReminderHelper {

    private static int DEFAULT = -1;

    private Context mContext;

    private long mReminderId;
    private int mYear = DEFAULT;
    private int mMonth = DEFAULT;
    private int mDay = DEFAULT;
    private int mHour = DEFAULT;
    private int mMinute = DEFAULT;

    public ReminderHelper(Context context, Reminder reminder) {
        mContext = context;
        mReminderId = reminder.getId();
        init(reminder);
    }

    /**
     * Converts the fields stored into milliseconds since Epoch
     *
     * @return          converted time
     */
    public long convertToUnixTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(mYear, mMonth, mDay, mHour, mMinute, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * Broadcast an Intent to sound an alarm at the currently set time
     */
    public void broadcast() {
        AlarmManager alarmManager =
                (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(mContext, ReminderReceiver.class);
        intent.putExtra(Constants.INTENT_REMINDER_ID, mReminderId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, (int) mReminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Alarm is not valid, need to cancel it
        if (!isValidAlarm()) {
            pendingIntent.cancel();
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, convertToUnixTime(), pendingIntent);
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, convertToUnixTime(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, convertToUnixTime(), pendingIntent);
        }
    }

    /**
     * Cancel the current alarm
     */
    public void cancel() {
        mYear = DEFAULT;
        mMonth = DEFAULT;
        mDay = DEFAULT;
        mHour = DEFAULT;
        mMinute = DEFAULT;

        broadcast();
    }

    /**
     * Check if user has set an alarm
     *
     * @return          true iff alarm is valid
     */
    public boolean isValidAlarm() {
        return !(mYear == DEFAULT &&
                mMonth == DEFAULT &&
                mDay == DEFAULT &&
                mHour == DEFAULT &&
                mMinute == DEFAULT);
    }

    public void init(Reminder reminder) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(reminder.getTime());

        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
    }

    public void setYear(int year) {
        mYear = year;
    }

    public void setMonth(int month) {
        mMonth = month;
    }

    public void setDay(int day) {
        mDay = day;
    }

    public void setHour(int hour) {
        mHour = hour;
    }

    public void setMinute(int minute) {
        mMinute = minute;
    }
}

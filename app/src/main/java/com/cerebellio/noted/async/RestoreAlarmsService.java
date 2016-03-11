package com.cerebellio.noted.async;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.helpers.ReminderHelper;
import com.cerebellio.noted.models.Reminder;

/**
 * Notifies system of all alarms set on reboot
 */
public class RestoreAlarmsService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SqlDatabaseHelper sqlDatabaseHelper = new SqlDatabaseHelper(getApplicationContext());

        for (Reminder reminder : sqlDatabaseHelper.getAllUnfiredReminders()) {
            ReminderHelper reminderHelper = new ReminderHelper(getApplicationContext(), reminder);
            reminderHelper.broadcast();
        }

        sqlDatabaseHelper.closeDB();

        return Service.START_NOT_STICKY;
    }

}

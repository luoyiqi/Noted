package com.cerebellio.noted.async;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cerebellio.noted.ActivityMain;
import com.cerebellio.noted.R;
import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.TextFunctions;

/**
 * BroadcastReceiver to receive message when a reminder that
 * the user set is due to be alarmed
 */
public class ReminderReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = TextFunctions.makeLogTag(ReminderReceiver.class);

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;

        try {

            SqlDatabaseHelper sqlDatabaseHelper = new SqlDatabaseHelper(context);

            Item.Type type = Item.Type.valueOf(intent.getStringExtra(Constants.INTENT_ITEM_TYPE));
            Item item = sqlDatabaseHelper.getItemById(
                    intent.getLongExtra(Constants.INTENT_ITEM_ID, -1), type);

            showNotification(item);

            sqlDatabaseHelper.closeDB();

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error getting Item");
        }
    }

    /**
     * Display a notification for the given {@link Item}
     *
     * @param item      given {@link Item}
     */
    private void showNotification(Item item) {

        Intent intent = new Intent(mContext, ActivityMain.class);
        intent.putExtra(Constants.INTENT_ITEM_ID, item.getId());
        intent.putExtra(Constants.INTENT_ITEM_TYPE, item.getItemType().toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), intent, 0);

        // build notification
        Notification n = new Notification.Builder(mContext)
                .setContentTitle("Reminder")
                .setContentText(item.getFormattedTagString())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();


        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
    }

}

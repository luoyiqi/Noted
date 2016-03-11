package com.cerebellio.noted.async;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.cerebellio.noted.ActivityMain;
import com.cerebellio.noted.R;
import com.cerebellio.noted.database.SqlDatabaseHelper;
import com.cerebellio.noted.helpers.PreferenceHelper;
import com.cerebellio.noted.helpers.ReminderHelper;
import com.cerebellio.noted.models.Item;
import com.cerebellio.noted.models.Sketch;
import com.cerebellio.noted.utils.Constants;
import com.cerebellio.noted.utils.FileFunctions;
import com.cerebellio.noted.utils.TextFunctions;
import com.cerebellio.noted.utils.UtilityFunctions;

/**
 * BroadcastReceiver to receive message when a reminder that
 * the user set is due to be alarmed
 */
public class ReminderReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = TextFunctions.makeLogTag(ReminderReceiver.class);
    private static final int MAX_TITLE_LENGTH = 25;

    private Context mContext;
    private Item mItem;

    private long mReminderId;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        try {
            SqlDatabaseHelper sqlDatabaseHelper = new SqlDatabaseHelper(context);
            mReminderId = intent.getLongExtra(Constants.INTENT_REMINDER_ID, -1);
            mItem = sqlDatabaseHelper.getItemByReminderId(mReminderId);

            if (mItem.getReminder().isEmpty()) {
                return;
            }

            //Tell the reminder it has been fired and rebroadcast in case
            //there are any recurrence rules attached
            mItem.getReminder().fire();
            new ReminderHelper(context, mItem.getReminder()).broadcast();
            sqlDatabaseHelper.addOrEditItem(mItem);
            sqlDatabaseHelper.closeDB();

            if (!mItem.getStatus().equals(Item.Status.DELETED)) {
                showNotification();
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error getting Item");
        }
    }

    /**
     * Display a notification for the given {@link Item}
     */
    private void showNotification() {
        Intent intent = new Intent(mContext, ActivityMain.class);
        intent.putExtra(Constants.INTENT_REMINDER_ID, mReminderId);
        intent.putExtra(Constants.INTENT_FROM_NOTIFICATION, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(mContext, (int) System.currentTimeMillis(), intent, 0);

        // build notification
        Notification notification = new NotificationCompat.Builder(mContext)
                .setContentTitle(getContentTitle())
                .setContentText(getContentText())
                .setSmallIcon(R.drawable.ic_notification)
                .setTicker(mContext.getString(R.string.notification_ticker))
                .setLargeIcon(getLargeIcon())
                .setVibrate(getVibrationPattern())
                .setSound(getSound())
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int) mItem.getReminder().getId(), notification);
    }

    /**
     * Get the vibration pattern to play on notification
     *
     * @return          vibration pattern
     */
    private long[] getVibrationPattern() {
        return PreferenceHelper.getPrefBehaviourNotificationVibration(mContext)
                ? new long[]{0, 200, 100, 200, 100}
                : new long[] {0};
    }

    /**
     * Get the sound to play on notification
     *
     * @return          sound to play
     */
    private Uri getSound() {
        return PreferenceHelper.getPrefBehaviourNotificationSound(mContext)
                ? Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getApplicationContext().getPackageName()
                + "/raw/sound_notification")
                : Uri.parse("");
    }

    /**
     * Get notification title
     *
     * @return          title
     */
    private String getContentTitle() {
        return mItem instanceof Sketch
                ? mContext.getString(R.string.notification_sketch_title)
                : TextFunctions.getStartSubstringWithEllipse(mItem.getText(), MAX_TITLE_LENGTH);
    }

    /**
     * Get notification text body
     *
     * @return          text body
     */
    private String getContentText() {
        switch (mItem.getItemType()) {
            default:
            case NOTE:
                return mContext.getString(R.string.notification_text_note);
            case CHECKLIST:
                return mContext.getString(R.string.notification_text_checklist);
            case SKETCH:
                return mContext.getString(R.string.notification_text_sketch);
        }
    }

    /**
     * Get the large icon to display.
     * If {@link ReminderReceiver#mItem} is a {@link Sketch} we scale it down and show that.
     * Otherwise we just show the app launcher icon
     *
     * @return          Bitmap created
     */
    private Bitmap getLargeIcon() {
        if (mItem instanceof Sketch) {
            Bitmap bitmap =  Bitmap.createScaledBitmap(
                    FileFunctions.getBitmapFromFile(((Sketch) mItem).getImagePath()),
                    (int) UtilityFunctions.convertDpToPixels(mContext, 64),
                    (int) UtilityFunctions.convertDpToPixels(mContext, 64),
                    true);
            return FileFunctions.colourBitmapBackgroundWhite(bitmap);
        }

        return Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher),
                (int) UtilityFunctions.convertDpToPixels(mContext, 64),
                (int) UtilityFunctions.convertDpToPixels(mContext, 64),
                true);
    }
}

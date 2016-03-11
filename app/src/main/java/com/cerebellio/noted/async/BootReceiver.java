package com.cerebellio.noted.async;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Starts {@link RestoreAlarmsService} on boot
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, RestoreAlarmsService.class));
    }
}

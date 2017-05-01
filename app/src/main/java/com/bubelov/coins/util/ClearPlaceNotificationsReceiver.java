package com.bubelov.coins.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bubelov.coins.domain.PlaceNotification;

/**
 * @author Igor Bubelov
 */

public class ClearPlaceNotificationsReceiver extends BroadcastReceiver {
    public static final String ACTION = "PLACE_NOTIFICATION_DELETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        PlaceNotification.deleteAll();
        context.unregisterReceiver(this);
    }
}
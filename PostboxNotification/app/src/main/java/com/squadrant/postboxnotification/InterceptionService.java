package com.squadrant.postboxnotification;

import android.content.Context;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;


public class InterceptionService extends NotificationListenerService {
    private final String TAG = this .getClass().getSimpleName();
    Context context;

    private boolean blockingEnabled = true;

    private static ArrayList<StatusBarNotification> postbox;

    @Override
    public void onCreate () {
        super .onCreate();
        context = getApplicationContext();
        postbox = new ArrayList<>();
    }
    @Override
    public void onNotificationPosted (StatusBarNotification sbn) {
        Log.i(TAG ,"Posted ID:" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName() + "\t" + sbn.getKey());

        // Are we currently in an un-snoozing window?

        // Check if we should block the notification
        if (blockingEnabled && shouldBlock(sbn)) {
            postbox.add(sbn);
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("Postbox-Update"));
        }

    }
    @Override
    public void onNotificationRemoved (StatusBarNotification sbn) {
        Log.i(TAG ,"Removed ID:" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
    }

    public StatusBarNotification[] getNotifications() {
        return postbox.toArray(new StatusBarNotification[0]);
    }

    public static ArrayList<StatusBarNotification> GetSBNs() {
        return postbox;
    }

    public static void RemoveSBN(int index) {
        postbox.remove(index);
    }

    private boolean shouldBlock(StatusBarNotification sbn) {
        // Blocking logic goes here

        return true;
    }
}
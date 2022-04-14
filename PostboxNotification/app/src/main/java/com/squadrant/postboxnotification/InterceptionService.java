package com.squadrant.postboxnotification;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;


public class InterceptionService extends NotificationListenerService {
    private final String TAG = this .getClass().getSimpleName();
    Context context;

    private static ArrayList<StatusBarNotification> postbox;

    @Override
    public void onCreate () {
        super.onCreate();
        context = getApplicationContext();
        postbox = new ArrayList<>();
    }
    @Override
    public void onNotificationPosted (StatusBarNotification sbn) {
        Log.i(TAG ,"Posted ID:" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName() + "\t" + sbn.getKey());

        Log.i(TAG, sbn.getNotification().getChannelId());

        // Check if we should block the notification
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean interceptionEnabled = preferences.getBoolean("intercept_notifications", false);
        boolean blockingEnabled = preferences.getBoolean("block_notifications", false);

        if (interceptionEnabled && shouldBlock(sbn)) {
            postbox.add(sbn);
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("Postbox-Update"));

            if (blockingEnabled)
                cancelNotification(sbn.getKey());
        }

    }
    @Override
    public void onNotificationRemoved (StatusBarNotification sbn) {
        Log.i(TAG ,"Removed ID:" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
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
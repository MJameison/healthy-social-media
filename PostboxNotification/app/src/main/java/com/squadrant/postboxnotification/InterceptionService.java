package com.squadrant.postboxnotification;

import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.ArrayList;


public class InterceptionService extends NotificationListenerService {
    private final String TAG = this .getClass().getSimpleName();
    Context context;

    private boolean blockingEnabled = true;
    protected static InterceptionService instance;

    private ArrayList<StatusBarNotification> postbox;

    @Override
    public void onCreate () {
        super .onCreate();
        context = getApplicationContext();
        instance = this;
        postbox = new ArrayList<>();
    }
    @Override
    public void onNotificationPosted (StatusBarNotification sbn) {
        Log.i(TAG ,"Posted ID:" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName() + "\t" + sbn.getKey());

        // Are we currently in an un-snoozing window?

        // Check if we should block the notification
        if (blockingEnabled && shouldBlock(sbn)) {
            // Calculate time to next display period
            //long snoozeMillis = 10000;
            //snoozeNotification(sbn.getKey(), snoozeMillis);

            postbox.add(sbn);
        }

    }
    @Override
    public void onNotificationRemoved (StatusBarNotification sbn) {
        Log.i(TAG ,"Removed ID:" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
    }

    public StatusBarNotification[] getNotifications() {
        return postbox.toArray(new StatusBarNotification[0]);
    }

    public void clearPostbox() {
        postbox.clear();
    }

    private boolean shouldBlock(StatusBarNotification sbn) {
        // Blocking logic goes here

        return true;
    }
}
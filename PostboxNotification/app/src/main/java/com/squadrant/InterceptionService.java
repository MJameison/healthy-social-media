package com.squadrant;

import android.content.SharedPreferences;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.squadrant.model.StoredNotification;
import com.squadrant.repos.StoredNotificationRepository;
import com.squadrant.util.StoredNotificationBuilder;


public class InterceptionService extends NotificationListenerService {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onNotificationPosted (StatusBarNotification sbn) {
        Log.i(TAG ,"Received:" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName() + "\t" + sbn.getKey());

        // Check if we should block the notification
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean interceptionEnabled = preferences.getBoolean("intercept_notifications", false);
        boolean blockingEnabled = preferences.getBoolean("block_notifications", false);

        if (interceptionEnabled && shouldBlock(sbn)) {
            // Add notification to the repository
            StoredNotification sn = StoredNotificationBuilder.createFromStatusBarNotification(sbn);
            StoredNotificationRepository.getInstance().addItem(sn);

            if (blockingEnabled)
                cancelNotification(sbn.getKey());
        }
    }

    @Override
    public void onNotificationRemoved (StatusBarNotification sbn) {
        Log.i(TAG ,"Removed ID:" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
    }

    private boolean shouldBlock(StatusBarNotification sbn) {
        // Blocking logic goes here
        // TODO
        return true;
    }
}
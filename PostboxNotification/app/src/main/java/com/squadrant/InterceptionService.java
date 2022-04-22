package com.squadrant;

import android.content.SharedPreferences;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.squadrant.model.StoredNotification;
import com.squadrant.repos.StoredNotificationRepository;
import com.squadrant.util.StoredNotificationBuilder;

import java.util.HashSet;
import java.util.Set;


public class InterceptionService extends NotificationListenerService {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onNotificationPosted (StatusBarNotification sbn) {
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean interceptionEnabled = preferences.getBoolean("intercept_notifications", false);
        boolean blockingEnabled = preferences.getBoolean("block_notifications", false);
        boolean removeCancelled = preferences.getBoolean("remove_cancelled", false);

        if (interceptionEnabled && removeCancelled && !blockingEnabled) {
            // Remove the notification since the app wants us to
            StoredNotification sn = StoredNotificationBuilder.createFromStatusBarNotification(sbn);
            StoredNotificationRepository.getInstance().removeItem(sn);
        }
    }

    private boolean shouldBlock(StatusBarNotification sbn) {
        // Blocking logic goes here
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> appFilter = preferences.getStringSet("app_filter_set", new HashSet<>());
        return appFilter.contains(sbn.getPackageName());
    }
}
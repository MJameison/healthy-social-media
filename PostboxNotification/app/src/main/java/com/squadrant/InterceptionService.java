package com.squadrant;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.squadrant.model.StoredNotification;
import com.squadrant.repos.StoredNotificationRepository;
import com.squadrant.util.Settings;
import com.squadrant.util.StoredNotificationBuilder;

import java.util.Set;


public class InterceptionService extends NotificationListenerService {
    Settings settings;

    public InterceptionService() {
        settings = Settings.getDefaultSettings(App.getContext());
    }

    @Override
    public void onNotificationPosted (StatusBarNotification sbn) {
        // Check if we should block the notification
        boolean interceptionEnabled = settings.getBoolean("intercept_notifications");
        boolean blockingEnabled = settings.getBoolean("block_notifications");

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

        boolean interceptionEnabled = settings.getBoolean("intercept_notifications");
        boolean blockingEnabled = settings.getBoolean("block_notifications");
        boolean removeCancelled = settings.getBoolean("remove_cancelled");

        if (interceptionEnabled && removeCancelled && !blockingEnabled) {
            // Remove the notification since the app wants us to
            StoredNotification sn = StoredNotificationBuilder.createFromStatusBarNotification(sbn);
            StoredNotificationRepository.getInstance().removeItem(sn);
        }
    }

    private boolean shouldBlock(StatusBarNotification sbn) {
        Set<String> appFilter = settings.getStringSet("app_filter_set");
        String pkg = sbn.getPackageName();
        return appFilter.contains(sbn.getPackageName());
    }
}
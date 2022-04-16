package com.squadrant.util;

import android.app.Notification;
import android.service.notification.StatusBarNotification;

import com.squadrant.model.StoredNotification;

public class StoredNotificationBuilder {
    public static StoredNotification createFromStatusBarNotification(StatusBarNotification sbn) {
        StoredNotification sn = new StoredNotification(sbn.getKey());
        sn.notificationPackage = sbn.getPackageName();
        sn.notificationTitle = sbn.getNotification().extras.getString(Notification.EXTRA_TITLE);
        sn.notificationContent = sbn.getNotification().extras.getString(Notification.EXTRA_TEXT);
        sn.notificationChannelID = sbn.getNotification().getChannelId();
        sn.notificationWhen = sbn.getNotification().when;
        return sn;
    }
}

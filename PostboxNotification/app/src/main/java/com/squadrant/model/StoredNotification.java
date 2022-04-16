package com.squadrant.model;

/**
 * To decouple the notification storage from android specific features we have this data holding class that also implements conversion methods that extract the requisite information
 */
public class StoredNotification {
    // Identifying fields
    private final String key;

    // Extras
    public long notificationWhen;
    public String notificationTitle;
    public String notificationContent;
    public String notificationChannelID;

    public StoredNotification(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}

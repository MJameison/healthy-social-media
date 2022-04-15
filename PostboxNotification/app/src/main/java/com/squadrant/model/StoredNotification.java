package com.squadrant.model;

/**
 * To decouple the notification storage from android specific features we have this data holding class that also implements conversion methods that extract the requisite information
 */
public class StoredNotification {
    // Identifying fields
    private String sourcePackageName;
    private int notificationID;

    // Extras
    public long notificationWhen;
    public String notificationTitle;
    public String notificationContent;
    public String notificationChannelID;
}

package com.squadrant.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * To decouple the notification storage from android specific features we have this data holding class that also implements conversion methods that extract the requisite information
 */
public class StoredNotification implements Serializable {
    private static final long serialVersionUID = 1L;

    @NonNull
    private final String key;

    // Extras
    public String notificationPackage;
    public long notificationWhen;
    public String notificationTitle;
    public String notificationContent;
    public String notificationChannelID;

    public StoredNotification(@NonNull String key) {
        this.key = key;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoredNotification that = (StoredNotification) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}

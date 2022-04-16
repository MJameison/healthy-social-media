package com.squadrant.repos;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.squadrant.model.StoredNotification;

import java.util.ArrayList;
import java.util.List;

public class StoredNotificationRepository {
    private static final StoredNotificationRepository ourInstance = new StoredNotificationRepository();

    // LiveData views
    private final MutableLiveData<List<StoredNotification>> notificationListLiveData = new MutableLiveData<>();


    public static StoredNotificationRepository getInstance() { return ourInstance; }

    private StoredNotificationRepository() {
        // Temp code to display something for now
        List<StoredNotification> notifications = new ArrayList<>();
        notificationListLiveData.setValue(notifications);

        // Instead query and get the underlying value
    }

    public LiveData<List<StoredNotification>> getNotifications() {
        return notificationListLiveData;
    }

    /* These are the main methods to update the Stored Notification data
    * They should also handle interacting with the underlying storage mechanism to ensure it stays up to date */
    /**
     * Adds the provided StoredNotification. If a StoredNotification with matching source packet and id is already present this replaces that value
     */
    public void addItem(StoredNotification storedNotification) {
        Log.i("SNRepo", "Adding notification: " + storedNotification.getKey());
        // Add / Update the live data
        List<StoredNotification> list = notificationListLiveData.getValue();
        if (list == null)
            list = new ArrayList<>();
        list.add(storedNotification);
        notificationListLiveData.setValue(list);
        // Write it to the file
    }

    /**
     * Removes the notification with matching source package and id number.
     */
    public void removeItem(StoredNotification storedNotification) {
        // Remove the notification

        // Remove it from the file
    }
}

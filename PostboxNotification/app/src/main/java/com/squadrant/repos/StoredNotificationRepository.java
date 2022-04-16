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
        List<StoredNotification> notifications = readNotificationsFromFile();
        notificationListLiveData.setValue(notifications);

        // Instead query and get the underlying value
    }

    public LiveData<List<StoredNotification>> getNotifications() {
        return notificationListLiveData;
    }

    public void addItem(StoredNotification storedNotification) {
        Log.i("SNRepo", "Adding notification: " + storedNotification.getKey());
        // Update the live data
        List<StoredNotification> list = notificationListLiveData.getValue();
        if (list == null)
            list = new ArrayList<>();
        // If there is already a notification with the same key remove it (no duplicate keys)
        list.remove(storedNotification);
        // Then insert the new value with that key
        list.add(storedNotification);
        notificationListLiveData.setValue(list);

        // Write to file for permanency
        writeNotificationsToFile(list);
    }

    public void removeItem(StoredNotification storedNotification) {
        Log.i("SNRepo", "Removing notification: " + storedNotification.getKey());
        // Update the live data
        List<StoredNotification> list = notificationListLiveData.getValue();
        if (list == null)
            list = new ArrayList<>();
        // If there is already a notification with the same key remove it (no duplicate keys)
        list.remove(storedNotification);
        notificationListLiveData.setValue(list);

        // Write to file for permanency
        writeNotificationsToFile(list);
    }

    private void writeNotificationsToFile(List<StoredNotification> notifications) {
        // TODO
    }

    private List<StoredNotification> readNotificationsFromFile() {
        // TODO
        return null;
    }
}

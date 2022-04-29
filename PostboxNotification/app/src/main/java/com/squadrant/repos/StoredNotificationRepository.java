package com.squadrant.repos;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.squadrant.App;
import com.squadrant.model.StoredNotification;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StoredNotificationRepository {
    private static final StoredNotificationRepository ourInstance = new StoredNotificationRepository();
    private static boolean loadedData;

    // LiveData views
    private final MutableLiveData<List<StoredNotification>> notificationListLiveData = new MutableLiveData<>();

    // Interfaces to storage
    private static StoredNotificationLocalDataStore localDataStore;

    public static StoredNotificationRepository getInstance() {
        if (!loadedData)
            ourInstance.loadDataFromSources();
        return ourInstance;
    }

    private StoredNotificationRepository() {
        loadedData = false;
        localDataStore = new StoredNotificationLocalDataStore();
    }

    private void loadDataFromSources() {
        // Get stored notifications from previous sessions
        List<StoredNotification> notifications = localDataStore.readNotificationsFromFile();
        notificationListLiveData.setValue(notifications);
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

        // Write to file for permanency on background thread
        localDataStore.writeNotificationsToFile(list);
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

        // Write to file for permanency on background thread
        localDataStore.writeNotificationsToFile(list);
    }
}

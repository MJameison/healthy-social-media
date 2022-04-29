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
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StoredNotificationRepository {
    private static final StoredNotificationRepository ourInstance = new StoredNotificationRepository();
    private static boolean loadedData = false;

    // LiveData views
    private static MutableLiveData<List<StoredNotification>> notificationListLiveData = new MutableLiveData<>();

    // Interfaces to storage
    private static StoredNotificationLocalDataStore localDataStore = new StoredNotificationLocalDataStore();

    public static StoredNotificationRepository getInstance() {
        if (!loadedData)
            ourInstance.loadDataFromSources();
        return ourInstance;
    }

    private StoredNotificationRepository() {
        loadedData = false;
    }

    // This should probably be done with dependency injection and is only here for testing
    public static void setLocalDataStore(StoredNotificationLocalDataStore dataStore) {
        loadedData = false;
        localDataStore = dataStore;
    }

    // This should also probably be dependency injection and is only here for testing
    public static void setNotificationListLiveData(MutableLiveData<List<StoredNotification>> liveData) {
        loadedData = false;
        notificationListLiveData = liveData;
    }


    private void loadDataFromSources() {
        // Get stored notifications from previous sessions
        List<StoredNotification> notifications = localDataStore.readNotificationsFromFile();
        notificationListLiveData.setValue(notifications);
    }

    public LiveData<List<StoredNotification>> getNotifications() {
        if (!loadedData)
            loadDataFromSources();
        return notificationListLiveData;
    }

    public void addItem(StoredNotification storedNotification) {
        Log.i("SNRepo", "Adding notification: " + storedNotification.getKey());
        // Get the live data value
        List<StoredNotification> list = notificationListLiveData.getValue();
        if (list == null)
            list = new ArrayList<>();
        // If there is already a notification with the same key remove it (no duplicate keys)
        list.remove(storedNotification);
        // Then insert the new value with that key
        list.add(storedNotification);

        // Ensure sorted
        list.sort(Comparator.comparingLong(StoredNotification::getWhen).reversed());

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
        // Remove the notification
        list.remove(storedNotification);
        // Ensure sorted
        list.sort(Comparator.comparingLong(StoredNotification::getWhen).reversed());

        notificationListLiveData.setValue(list);

        // Write to file for permanency on background thread
        localDataStore.writeNotificationsToFile(list);
    }

    public void clear() {
        Log.i("SNRepo", "Clearing repository");
        // Update the live data
        List<StoredNotification> list = new ArrayList<>();
        notificationListLiveData.setValue(list);

        // Write to file for permanency on background thread
        localDataStore.writeNotificationsToFile(list);
    }
}

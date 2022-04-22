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
    private static final String filename = "stored_notifications.bin";

    // LiveData views
    private final MutableLiveData<List<StoredNotification>> notificationListLiveData = new MutableLiveData<>();


    public static StoredNotificationRepository getInstance() { return ourInstance; }

    private StoredNotificationRepository() {
        // Get stored notifications from previous sessions
        List<StoredNotification> notifications = readNotificationsFromFile();
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

        // Write to file for permanency on background thread
       writeNotificationsToFile(list);
    }

    private void writeNotificationsToFile(List<StoredNotification> notifications) {
        Thread thread = new Thread(() -> {
            try (
                    FileOutputStream fos = App.getContext().openFileOutput(filename, Context.MODE_PRIVATE);
                    ObjectOutputStream os = new ObjectOutputStream(fos)
            ) {
                // Do stuff here
                os.writeObject(new CopyOnWriteArrayList<>(notifications));
            } catch (FileNotFoundException e) {
                Log.e("StoredNotificationRepository", "Writing exception: File not found");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    @SuppressWarnings("unchecked")
    private List<StoredNotification> readNotificationsFromFile() {
        List<StoredNotification> list = new ArrayList<>();
        try(
            FileInputStream fis = App.getContext().openFileInput(filename);
            ObjectInputStream is = new ObjectInputStream(fis)
        ) {

            list = (List<StoredNotification>) is.readObject();
        } catch (FileNotFoundException e) {
            Log.e("StoredNotificationRepository", "Reading exception: File not found");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Log.e("StoredNotificationRepository", "Reading exception: Class not found");
        }
        return list;
    }
}

package com.squadrant.repos;

import android.content.Context;
import android.util.Log;

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

public class StoredNotificationLocalDataStore {

    private static final String filename = "stored_notifications.bin";

    public void writeNotificationsToFile(List<StoredNotification> notifications) {
        Log.i("StoredNotificationRepository", "Writing to file");
        Thread thread = new Thread(() -> {
            Context context = App.getContext();
            try (
                    FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
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
    public List<StoredNotification> readNotificationsFromFile() {
        Log.i("StoredNotificationRepository", "Reading from file");
        Context context = App.getContext();
        List<StoredNotification> list = new ArrayList<>();
        try(
                FileInputStream fis = context.openFileInput(filename);
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

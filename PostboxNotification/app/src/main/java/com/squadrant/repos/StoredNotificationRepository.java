package com.squadrant.repos;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.squadrant.model.StoredNotification;

import java.util.ArrayList;
import java.util.List;

public class StoredNotificationRepository {
    private static final StoredNotificationRepository ourInstance = new StoredNotificationRepository();

    private MutableLiveData<List<StoredNotification>> notificationListLiveData = new MutableLiveData<>();

    public static StoredNotificationRepository getInstance() { return ourInstance; }

    private StoredNotificationRepository() {
        // Temp code to display something for now
        List<StoredNotification> list = new ArrayList<>();
        StoredNotification sn = new StoredNotification();
        sn.notificationTitle = "Test";
        list.add(sn);
        sn = new StoredNotification();
        sn.notificationTitle = "test2";
        list.add(sn);
        notificationListLiveData.postValue(list);
    }

    // TODO: Interface with file so this actually works
    public LiveData<List<StoredNotification>> getNotifications() {
        return notificationListLiveData;
    }
}

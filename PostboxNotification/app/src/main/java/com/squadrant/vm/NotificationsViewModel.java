package com.squadrant.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.squadrant.model.StoredNotification;
import com.squadrant.repos.StoredNotificationRepository;

import java.util.List;

public class NotificationsViewModel extends ViewModel {

    private final StoredNotificationRepository repository = StoredNotificationRepository.getInstance();
    private final LiveData<List<StoredNotification>> notificationLiveData;

    public NotificationsViewModel() {
        super();
        notificationLiveData = repository.getNotifications();
    }

    public LiveData<List<StoredNotification>> getNotificationLiveData() { return notificationLiveData; }
}

package com.squadrant.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.squadrant.model.StoredNotification;
import com.squadrant.repos.StoredNotificationRepository;

import java.util.List;

public class NotificationsViewModel extends ViewModel {

    private LiveData<List<StoredNotification>> notificationLiveData;

    public NotificationsViewModel() {
        super();
        // TODO: Setup repo and get notifs
        StoredNotificationRepository repository = StoredNotificationRepository.getInstance();
        notificationLiveData = repository.getNotifications();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public LiveData<List<StoredNotification>> getNotificationLiveData() { return notificationLiveData; }
}

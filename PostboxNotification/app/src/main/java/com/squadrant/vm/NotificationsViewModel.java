package com.squadrant.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.squadrant.model.StoredNotification;
import com.squadrant.repos.StoredNotificationRepository;

import java.util.List;
import java.util.Objects;

public class NotificationsViewModel extends ViewModel {

    private final StoredNotificationRepository repository = StoredNotificationRepository.getInstance();
    private final LiveData<List<StoredNotification>> notificationLiveData;

    public NotificationsViewModel() {
        super();
        notificationLiveData = repository.getNotifications();
    }

    public LiveData<List<StoredNotification>> getNotificationLiveData() { return notificationLiveData; }

    public StoredNotification removeAt(int position) {
        // Get the value to be removed
        StoredNotification sn = Objects.requireNonNull(notificationLiveData.getValue()).get(position);
        // Remove the value (from the repository)
        repository.removeItem(sn);

        return sn;
    }

    public void addNotification(StoredNotification sn) {
        repository.addItem(sn);
    }
}

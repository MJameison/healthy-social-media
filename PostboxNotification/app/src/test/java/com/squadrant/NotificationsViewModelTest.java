package com.squadrant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.squadrant.model.StoredNotification;
import com.squadrant.repos.StoredNotificationLocalDataStore;
import com.squadrant.repos.StoredNotificationRepository;
import com.squadrant.vm.NotificationsViewModel;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class NotificationsViewModelTest {

    StoredNotificationRepository repository;
    NotificationsViewModel vm;

    @Before
    public void init() {
        repository = mock(StoredNotificationRepository.class);
        when(repository.getNotifications()).thenReturn(new MutableLiveData<>());

        vm = new NotificationsViewModel(repository);
    }

    @Test
    public void liveData_InitiallyValid() {
        Assert.assertNotNull(vm.getNotificationLiveData());
    }

    @Test
    public void addNotification_callsRepository() {
        String[] keys = {"arbitrary_key", "", "another_key"};

        for (String key : keys) {
            StoredNotification sn = new StoredNotification(key);
            vm.addNotification(sn);
            verify(repository, times(1)).addItem(sn);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void removeAt_removesAndReturnsCorrectValue() {
        StoredNotification[] sns = {
                new StoredNotification("zero"),
                new StoredNotification("one"),
                new StoredNotification("two"),
                new StoredNotification("three")
        };

        LiveData<List<StoredNotification>> liveData = mock(LiveData.class);

        when(liveData.getValue()).thenReturn(Arrays.asList(sns));
        when(repository.getNotifications()).thenReturn(liveData);

        vm = new NotificationsViewModel(repository); // Recreate as changed livedata mock

        for (int i = 0; i < sns.length; i++) {
            Assert.assertEquals(sns[i], vm.removeAt(i));
            verify(repository, times(1)).removeItem(sns[i]);
        }
    }
}

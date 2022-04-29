package com.squadrant;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.squadrant.model.StoredNotification;
import com.squadrant.repos.StoredNotificationLocalDataStore;
import com.squadrant.repos.StoredNotificationRepository;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class StoredNotificationRepositoryTest {

    StoredNotificationLocalDataStore dataStore;
    MutableLiveData<List<StoredNotification>> liveDataMock;
    MockedStatic<Log> logMock;

    @After
    public void cleanUp() {
        // Need to unsupress the log so that it can be mocked again for the next test
        logMock.close();
    }

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        /*
         * We need to mock the underlying LocalDataStore as it interacts with the Context and Android API
         * We also need to mock the LiveData as it interacts with the MainLooper since it is observable
         * This would better done via dependency injection but c'est la vie
         */
        dataStore = mock(StoredNotificationLocalDataStore.class);
        StoredNotificationRepository.setLocalDataStore(dataStore);

        liveDataMock = (MutableLiveData<List<StoredNotification>>) mock(MutableLiveData.class);
        StoredNotificationRepository.setNotificationListLiveData(liveDataMock);

        logMock = Mockito.mockStatic(android.util.Log.class);
    }

    @Test
    public void repository_isSingleton() {
        StoredNotificationRepository instance1 = StoredNotificationRepository.getInstance();
        StoredNotificationRepository instance2 = StoredNotificationRepository.getInstance();

        Assert.assertEquals(instance1, instance2);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void repository_addNewCallsLocalDataStore() {
        StoredNotificationRepository repository = StoredNotificationRepository.getInstance();

        repository.addItem(new StoredNotification("arbitrary-key"));

        ArgumentCaptor<List<StoredNotification>> argument = ArgumentCaptor.forClass(List.class);
        verify(dataStore, times(1)).writeNotificationsToFile(argument.capture());

        Assert.assertTrue(argument.getValue().contains(new StoredNotification("arbitrary-key")));
        // Should be the only one
        Assert.assertEquals(1, argument.getValue().size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void repository_addNewGivesCorrectLiveData() {
        StoredNotificationRepository repository = StoredNotificationRepository.getInstance();

        repository.addItem(new StoredNotification("arbitrary-key"));

        ArgumentCaptor<List<StoredNotification>> argument = ArgumentCaptor.forClass(List.class);
        // may be called multiple times as getInstance() might have called it via loadDataStore
        verify(liveDataMock, atLeastOnce()).setValue(argument.capture());

        Assert.assertTrue(argument.getValue().contains(new StoredNotification("arbitrary-key")));
        // Should be the only one
        Assert.assertEquals(1, argument.getValue().size());
    }


    @Test
    @SuppressWarnings("unchecked")
    public void repository_updateNotificationCallsLocalDataStore() {
        StoredNotificationRepository repository = StoredNotificationRepository.getInstance();

        repository.addItem(new StoredNotification("arbitrary-key"));

        ArgumentCaptor<List<StoredNotification>> argument = ArgumentCaptor.forClass(List.class);
        verify(dataStore, times(1)).writeNotificationsToFile(argument.capture());

        Assert.assertTrue(argument.getValue().contains(new StoredNotification("arbitrary-key")));
        // Should be the only one
        Assert.assertEquals(1, argument.getValue().size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void repository_updateNotificationGivesCorrectLiveData() {
        StoredNotificationRepository repository = StoredNotificationRepository.getInstance();

        List<StoredNotification> list = new ArrayList<>();
        StoredNotification oldValue = new StoredNotification("existing-key");
        oldValue.notificationPackage = "com.old.package"; // Arbitrary non-identifying field
        list.add(oldValue);

        when(liveDataMock.getValue()).thenReturn(list);

        StoredNotification newValue = new StoredNotification("existing-key");
        newValue.notificationPackage = "com.new.package";
        repository.addItem(newValue);

        ArgumentCaptor<List<StoredNotification>> argument = ArgumentCaptor.forClass(List.class);

        // may be called multiple times but we only care about the last value
        verify(liveDataMock, atLeastOnce()).setValue(argument.capture());

        // No changes to keys stored
        Assert.assertEquals(list, argument.getValue());
        // But the data should store the new non-identifying data
        Assert.assertEquals(newValue.notificationPackage, argument.getValue().get(0).notificationPackage);
        Assert.assertNotEquals(oldValue.notificationPackage, argument.getValue().get(0).notificationPackage);
    }


    @Test
    @SuppressWarnings("unchecked")
    public void repository_removeCallsLocalDataStore() {
        StoredNotificationRepository repository = StoredNotificationRepository.getInstance();

        // What data is already in the live data?
        ArrayList<StoredNotification> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(new StoredNotification(String.valueOf(i)));
        }
        ArrayList<StoredNotification> storedList = new ArrayList<>(list);

        for (StoredNotification toBeRemoved : storedList) {
            // Calculate expected result
            List<StoredNotification> correctResult = new ArrayList<>(storedList);
            correctResult.remove(toBeRemoved);

            // Reset the list back to full length if modified and reset the mock return
            list = new ArrayList<>(storedList);
            when(liveDataMock.getValue()).thenReturn(list);

            repository.removeItem(toBeRemoved);

            ArgumentCaptor<List<StoredNotification>> argument = ArgumentCaptor.forClass(List.class);
            // may be called multiple times as getInstance() might have called it via loadDataStore but we only care about the last value
            verify(dataStore, atLeastOnce()).writeNotificationsToFile(argument.capture());

            Assert.assertEquals(correctResult, argument.getValue());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void repository_removeUpdatesLiveData() {
        StoredNotificationRepository repository = StoredNotificationRepository.getInstance();

        // What data is already in the live data?
        ArrayList<StoredNotification> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(new StoredNotification(String.valueOf(i)));
        }
        ArrayList<StoredNotification> storedList = new ArrayList<>(list);

        for (StoredNotification toBeRemoved : storedList) {
            // Calculate expected result
            List<StoredNotification> correctResult = new ArrayList<>(storedList);
            correctResult.remove(toBeRemoved);

            // Reset the list back to full length if modified and reset the mock return
            list = new ArrayList<>(storedList);
            when(liveDataMock.getValue()).thenReturn(list);

            repository.removeItem(toBeRemoved);

            ArgumentCaptor<List<StoredNotification>> argument = ArgumentCaptor.forClass(List.class);
            // may be called multiple times as getInstance() might have called it via loadDataStore but we only care about the last value
            verify(liveDataMock, atLeastOnce()).setValue(argument.capture());

            Assert.assertEquals(correctResult, argument.getValue());
        }
    }
}

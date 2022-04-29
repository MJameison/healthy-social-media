package com.squadrant;

import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.squadrant.model.StoredNotification;
import com.squadrant.repos.StoredNotificationRepository;
import com.squadrant.util.Settings;
import com.squadrant.util.SharedPreferencesSettings;
import com.squadrant.util.StoredNotificationBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class InterceptionServiceTest {

    @Test
    public void interception_ValidIsAddedToRepositoryNoBlocking() {
        Set<String> set = new HashSet<>();
        set.add("com.example.package");

        // Mock the settings
        Settings settings = Mockito.mock(SharedPreferencesSettings.class);
        when(settings.getBoolean("intercept_notifications")).thenReturn(true);
        when(settings.getBoolean("block_notifications")).thenReturn(false);
        when(settings.getStringSet("app_filter_set")).thenReturn(set);

        // Mock the repository
        StoredNotificationRepository repository = mock(StoredNotificationRepository.class);

        try (
                MockedStatic<App> appMockedStatic = mockStatic(App.class);
                MockedStatic<Settings> settingsMockedStatic = mockStatic(Settings.class);
                MockedStatic<StoredNotificationBuilder> snBuilder = mockStatic(StoredNotificationBuilder.class);
                MockedStatic<StoredNotificationRepository> staticRepository = mockStatic(StoredNotificationRepository.class)
        ) {
            // setup the static methods to return appropriate mocked values
            appMockedStatic.when(App::getContext).thenReturn(null);
            settingsMockedStatic.when(() -> Settings.getDefaultSettings(any())).thenReturn(settings);
            snBuilder.when(() -> StoredNotificationBuilder.createFromStatusBarNotification(any(StatusBarNotification.class))).thenReturn(new StoredNotification("arbitrary_key"));
            staticRepository.when(StoredNotificationRepository::getInstance).thenReturn(repository);

            // Mock the sbn
            StatusBarNotification sbn = mock(StatusBarNotification.class);
            when(sbn.getPackageName()).thenReturn("com.example.package");


            InterceptionService service = Mockito.spy(new InterceptionService());

            service.onNotificationPosted(sbn);

            verify(repository, times(1)).addItem(any(StoredNotification.class)); // Notification is added to the repository
            verify(service, Mockito.never()).cancelNotification(any(String.class));
        }
    }

    @Test
    public void interception_InvalidNotAddedToRepositoryNoBlocking() {
        Set<String> set = new HashSet<>();
        set.add("com.different.package");

        // Mock the settings
        Settings settings = Mockito.mock(SharedPreferencesSettings.class);
        when(settings.getBoolean("intercept_notifications")).thenReturn(true);
        when(settings.getBoolean("block_notifications")).thenReturn(true);
        when(settings.getStringSet("app_filter_set")).thenReturn(set);

        // Mock the repository
        StoredNotificationRepository repository = mock(StoredNotificationRepository.class);

        try (
                MockedStatic<App> appMockedStatic = mockStatic(App.class);
                MockedStatic<Settings> settingsMockedStatic = mockStatic(Settings.class);
                MockedStatic<StoredNotificationBuilder> snBuilder = mockStatic(StoredNotificationBuilder.class);
                MockedStatic<StoredNotificationRepository> staticRepository = mockStatic(StoredNotificationRepository.class)
        ) {
            // setup the static methods to return appropriate mocked values
            appMockedStatic.when(App::getContext).thenReturn(null);
            settingsMockedStatic.when(() -> Settings.getDefaultSettings(any())).thenReturn(settings);
            snBuilder.when(() -> StoredNotificationBuilder.createFromStatusBarNotification(any(StatusBarNotification.class))).thenReturn(new StoredNotification("arbitrary_key"));
            staticRepository.when(StoredNotificationRepository::getInstance).thenReturn(repository);

            // Mock the sbn
            StatusBarNotification sbn = mock(StatusBarNotification.class);
            when(sbn.getPackageName()).thenReturn("com.example.package");


            InterceptionService service = Mockito.spy(new InterceptionService());
            service.onNotificationPosted(sbn);

            verify(repository, never()).addItem(any(StoredNotification.class)); // Notification is not added to the repository
            verify((NotificationListenerService)service, never()).cancelNotification(any(String.class));
        }
    }

    @Test
    public void interceptionDisabled_NothingAddedToRepository() {
        Set<String> set = new HashSet<>();
        set.add("com.example.package");

        // Mock the settings
        Settings settings = Mockito.mock(SharedPreferencesSettings.class);
        when(settings.getBoolean("intercept_notifications")).thenReturn(false);
        when(settings.getBoolean("block_notifications")).thenReturn(false);
        Mockito.lenient().when(settings.getStringSet("app_filter_set")).thenReturn(set);

        // Mock the repository
        StoredNotificationRepository repository = mock(StoredNotificationRepository.class);

        try (
                MockedStatic<App> appMockedStatic = mockStatic(App.class);
                MockedStatic<Settings> settingsMockedStatic = mockStatic(Settings.class);
                MockedStatic<StoredNotificationBuilder> snBuilder = mockStatic(StoredNotificationBuilder.class);
                MockedStatic<StoredNotificationRepository> staticRepository = mockStatic(StoredNotificationRepository.class)
        ) {
            // setup the static methods to return appropriate mocked values
            appMockedStatic.when(App::getContext).thenReturn(null);
            settingsMockedStatic.when(() -> Settings.getDefaultSettings(any())).thenReturn(settings);
            snBuilder.when(() -> StoredNotificationBuilder.createFromStatusBarNotification(any(StatusBarNotification.class))).thenReturn(new StoredNotification("arbitrary_key"));
            staticRepository.when(StoredNotificationRepository::getInstance).thenReturn(repository);

            // Mock the sbns
            StatusBarNotification sbnA = mock(StatusBarNotification.class);
            Mockito.lenient().when(sbnA.getPackageName()).thenReturn("com.example.package");

            StatusBarNotification sbnB = mock(StatusBarNotification.class);
            Mockito.lenient().when(sbnA.getPackageName()).thenReturn("com.different.package");


            InterceptionService service = Mockito.spy(new InterceptionService());

            service.onNotificationPosted(sbnA);
            service.onNotificationPosted(sbnB);

            verify(repository, never()).addItem(any(StoredNotification.class)); // Nothing added
            verify(service, Mockito.never()).cancelNotification(any(String.class));
        }
    }

    @Test
    public void removeNotification_ValidCausesRemoval() {
        // Mock the settings
        Settings settings = Mockito.mock(SharedPreferencesSettings.class);
        when(settings.getBoolean("intercept_notifications")).thenReturn(true);
        when(settings.getBoolean("block_notifications")).thenReturn(false);
        when(settings.getBoolean("remove_cancelled")).thenReturn(true);

        // Mock the repository
        StoredNotificationRepository repository = mock(StoredNotificationRepository.class);

        cancelArbitrarySBN(settings, repository);

        verify(repository, times(1)).removeItem(any(StoredNotification.class)); // Notification not removed
    }

    @Test
    public void removeNotification_NoInterceptInvalidates() {
        // Mock the settings
        Settings settings = Mockito.mock(SharedPreferencesSettings.class);
        when(settings.getBoolean("intercept_notifications")).thenReturn(false);
        when(settings.getBoolean("block_notifications")).thenReturn(false);
        when(settings.getBoolean("remove_cancelled")).thenReturn(true);

        // Mock the repository
        StoredNotificationRepository repository = mock(StoredNotificationRepository.class);

        cancelArbitrarySBN(settings, repository);

        verify(repository, never()).removeItem(any(StoredNotification.class)); // Notification not removed
    }

    @Test
    public void removeNotification_BlockInvalidates() {
        // Mock the settings
        Settings settings = Mockito.mock(SharedPreferencesSettings.class);
        when(settings.getBoolean("intercept_notifications")).thenReturn(true);
        when(settings.getBoolean("block_notifications")).thenReturn(true);
        when(settings.getBoolean("remove_cancelled")).thenReturn(true);

        // Mock the repository
        StoredNotificationRepository repository = mock(StoredNotificationRepository.class);

        cancelArbitrarySBN(settings, repository);

        verify(repository, never()).removeItem(any(StoredNotification.class)); // Notification not removed
    }

    @Test
    public void removeNotification_RemoveCancelledInvalidates() {
        // Mock the settings
        Settings settings = Mockito.mock(SharedPreferencesSettings.class);
        when(settings.getBoolean("intercept_notifications")).thenReturn(true);
        when(settings.getBoolean("block_notifications")).thenReturn(false);
        when(settings.getBoolean("remove_cancelled")).thenReturn(false);

        // Mock the repository
        StoredNotificationRepository repository = mock(StoredNotificationRepository.class);

        cancelArbitrarySBN(settings, repository);

        verify(repository, never()).removeItem(any(StoredNotification.class)); // Notification not removed
    }

    private void cancelArbitrarySBN(Settings settings, StoredNotificationRepository repository) {
        try (
                MockedStatic<App> appMockedStatic = mockStatic(App.class);
                MockedStatic<Settings> settingsMockedStatic = mockStatic(Settings.class);
                MockedStatic<StoredNotificationBuilder> snBuilder = mockStatic(StoredNotificationBuilder.class);
                MockedStatic<StoredNotificationRepository> staticRepository = mockStatic(StoredNotificationRepository.class)
        ) {
            // setup the static methods to return appropriate mocked values
            appMockedStatic.when(App::getContext).thenReturn(null);
            settingsMockedStatic.when(() -> Settings.getDefaultSettings(any())).thenReturn(settings);
            snBuilder.when(() -> StoredNotificationBuilder.createFromStatusBarNotification(any(StatusBarNotification.class))).thenReturn(new StoredNotification("arbitrary_key"));
            staticRepository.when(StoredNotificationRepository::getInstance).thenReturn(repository);

            // Mock the sbn
            StatusBarNotification sbn = mock(StatusBarNotification.class);

            InterceptionService service = Mockito.spy(new InterceptionService());

            service.onNotificationRemoved(sbn);
        }
    }
}

package com.squadrant.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squadrant.vm.NotificationsViewModel;
import com.squadrant.postboxnotification.R;
import com.squadrant.model.StoredNotification;
import com.squadrant.adapter.StoredNotificationAdapter;

import java.util.List;

public class DisplayNotificationsFragment extends Fragment {

    RecyclerView notificationRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Get Views
        View rootView = inflater.inflate(R.layout.fragment_display_notifications, container, false);
        notificationRecyclerView = rootView.findViewById(R.id.notification_recycler_view);

        // Setup view model + callbacks
        NotificationsViewModel viewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
        viewModel.getNotificationLiveData().observe(getViewLifecycleOwner(), this::initRecyclerView);

        return rootView;
    }

    private void initRecyclerView(List<StoredNotification> notifications) {
        if (notifications.isEmpty()) {
            Log.i("DisplayNotificationsFragment", "Is Empty!");
        }
        StoredNotificationAdapter adapter = new StoredNotificationAdapter(requireContext(), notifications);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        notificationRecyclerView.setLayoutManager(layoutManager);
        notificationRecyclerView.setAdapter(adapter);
    }
}
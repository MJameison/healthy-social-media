package com.squadrant.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squadrant.adapter.StoredNotificationAdapter;
import com.squadrant.model.StoredNotification;
import com.squadrant.postboxnotification.R;
import com.squadrant.vm.NotificationsViewModel;
import com.squadrant.vm.NotificationsViewModelFactory;

import java.util.List;

public class DisplayNotificationsFragment extends Fragment {

    RecyclerView notificationRecyclerView;
    NotificationsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Get Views
        View rootView = inflater.inflate(R.layout.fragment_display_notifications, container, false);
        notificationRecyclerView = rootView.findViewById(R.id.notification_recycler_view);

        // Setup view model + callbacks
        viewModel = new ViewModelProvider(this, new NotificationsViewModelFactory()).get(NotificationsViewModel.class);
        viewModel.getNotificationLiveData().observe(getViewLifecycleOwner(), this::initRecyclerView);

        return rootView;
    }

    private void initRecyclerView(List<StoredNotification> notifications) {
        if (notifications == null) {
            Log.i("DisplayNotificationsFragment", "Is Null!");
            return;
        }
        if (notifications.isEmpty()) {
            Log.i("DisplayNotificationsFragment", "Is Empty!");
        }
        StoredNotificationAdapter adapter = new StoredNotificationAdapter(requireContext(), notifications);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        notificationRecyclerView.setLayoutManager(layoutManager);
        notificationRecyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final StoredNotification sn = viewModel.removeAt(position);

                Snackbar.make(notificationRecyclerView, "Item removed", Snackbar.LENGTH_SHORT)
                        .setAction("UNDO", v -> viewModel.addNotification(sn))
                        .setAnchorView(R.id.bottom_navigation)
                        .show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
        itemTouchHelper.attachToRecyclerView(notificationRecyclerView);
    }
}
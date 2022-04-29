package com.squadrant.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squadrant.App;
import com.squadrant.adapter.StoredNotificationAdapter;
import com.squadrant.model.StoredNotification;
import com.squadrant.postboxnotification.R;
import com.squadrant.util.Settings;
import com.squadrant.vm.NotificationsViewModel;
import com.squadrant.vm.NotificationsViewModelFactory;

import java.util.List;
import java.util.Set;

public class DisplayNotificationsFragment extends Fragment {

    RecyclerView notificationRecyclerView;
    NotificationsViewModel viewModel;
    View displayNotificationsContainer;
    //View displayHelpContainer;
    TextView helpTextView;
    Settings settings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Get Views
        View rootView = inflater.inflate(R.layout.fragment_display_notifications, container, false);
        notificationRecyclerView = rootView.findViewById(R.id.notification_recycler_view);
        //displayHelpContainer = rootView.findViewById(R.id.guide_user_container);
        helpTextView = rootView.findViewById(R.id.help_text_view);
        displayNotificationsContainer = rootView.findViewById(R.id.display_and_manage_notifications_container);

        // Setup view model + callbacks
        viewModel = new ViewModelProvider(this, new NotificationsViewModelFactory()).get(NotificationsViewModel.class);
        viewModel.getNotificationLiveData().observe(getViewLifecycleOwner(), this::initRecyclerView);

        // Setup button callback
        rootView.findViewById(R.id.clear_all_notifications_button).setOnClickListener(v -> viewModel.clear());

        settings = Settings.getDefaultSettings(App.getContext());

        return rootView;
    }

    private void initRecyclerView(List<StoredNotification> notifications) {
        if (notifications.isEmpty()) {
            Log.i("DisplayNotificationsFragment", "Is Empty!");

            displayNotificationsContainer.setVisibility(View.GONE);
            helpTextView.setVisibility(View.VISIBLE);

            if (settings.getBoolean("intercept_notifications")) {
                // Interception is on so this is just empty
                helpTextView.setText(getString(R.string.no_mail_text));
            } else {
                // Interception disabled so prompt go to settings fragment
                helpTextView.setText(getString(R.string.settings_prompt_text));
            }

            return;
        }

        displayNotificationsContainer.setVisibility(View.VISIBLE);
        helpTextView.setVisibility(View.GONE);

        // Setup the recycler view
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
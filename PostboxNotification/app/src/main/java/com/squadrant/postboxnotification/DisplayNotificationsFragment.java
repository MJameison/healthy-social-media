package com.squadrant.postboxnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;

public class DisplayNotificationsFragment extends Fragment {

    private LinearLayout notifContainer;

    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            redrawNotifications();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_display_notifications, container, false);
        notifContainer = rootView.findViewById(R.id.notifContainerLayout);
        return rootView;
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(messageReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(messageReceiver, new IntentFilter("Postbox-Update"));
        // Redraw since there may be new notifications
        redrawNotifications();
    }

    private void redrawNotifications() {
        // Fetch the SBNs to display
        ArrayList<StatusBarNotification> sbns = InterceptionService.GetSBNs();

        //Clear the existing display
        notifContainer.removeAllViews();

        // If we don't have any messages display the empty message
        if(sbns == null || sbns.size() == 0) {
            Log.i("Output", "Empty!!!");

            TextView empty = new TextView(getActivity());
            empty.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            empty.setText(R.string.no_mail_text);
            notifContainer.addView(empty);

            return;
        }

        int i = 0;
        for (StatusBarNotification sbn : sbns) {
            Log.i("Output", sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName() + "\t" + sbn.getKey());

            Bundle extras = sbn.getNotification().extras;

            // Horizontal Divider
            if (i > 0) {
                View divider = new View(getActivity());
                divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
                //divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, null));
                notifContainer.addView(divider);
            }

            NotificationView notifView = new NotificationView(getActivity());
            notifView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            notifContainer.addView(notifView);
            notifView.setSBN(sbn);

            int notificationID = i;
            notifView.setCloseMethod(v -> {
                InterceptionService.RemoveSBN(notificationID);
                redrawNotifications();
            });

            i++;
        }
    }
}
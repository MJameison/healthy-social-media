package com.squadrant.ui;

import android.app.Notification;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.squadrant.App;
import com.squadrant.postboxnotification.R;

public class DebugFragment extends Fragment {

    private NotificationManagerCompat notificationManager;
    private EditText editTextTitle;
    private EditText editTextMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_debug, container, false);
        notificationManager = NotificationManagerCompat.from(requireContext());
        editTextTitle = rootView.findViewById(R.id.edit_text_title);
        editTextMessage = rootView.findViewById(R.id.edit_text_message);

        Button send1 = rootView.findViewById(R.id.debug_send_ch1);
        Button send2 = rootView.findViewById(R.id.debug_send_ch2);

        send1.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString();
            String message = editTextMessage.getText().toString();

            if (title.equalsIgnoreCase("")) title = "Title";
            if (message.equalsIgnoreCase("")) message = "Message";

            Notification notification = new NotificationCompat.Builder(requireContext(), App.CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_one)
                    .setContentTitle(title)
                    .setContentText(message)
                    .build();
            notificationManager.notify(1, notification);
        });

        send2.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString();
            String message = editTextMessage.getText().toString();

            if (title.equalsIgnoreCase("")) title = "Title";
            if (message.equalsIgnoreCase("")) message = "Message";

            Notification notification = new NotificationCompat.Builder(requireContext(), App.CHANNEL_2_ID)
                    .setSmallIcon(R.drawable.ic_two)
                    .setContentTitle(title)
                    .setContentText(message)
                    .build();
            notificationManager.notify(2, notification);
        });

        return rootView;
    }
}
package com.squadrant.postboxnotification;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class RequestPermissionDialogFragment extends DialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.notification_listener_service)
                .setMessage(R.string.notification_listener_service_explanation)
                .setPositiveButton(R.string.yes, (dialog, id) -> {
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.no, (dialog, id) -> { });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}

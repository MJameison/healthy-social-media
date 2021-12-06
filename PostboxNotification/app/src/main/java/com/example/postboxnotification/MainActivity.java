package com.example.postboxnotification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private NotificationManagerCompat notificationManager;
    private EditText editTextTitle;
    private EditText editTextMessage;
    private LinearLayout linearScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationManager = NotificationManagerCompat.from(this);
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextMessage = findViewById(R.id.edit_text_message);
        linearScroll = findViewById(R.id.linear_layout_scroll);

        if(!isNotificationServiceEnabled()) {
            buildNotificationServiceAlertDialog().show();
        }

    }

    public void sendOnChannel1(View v) {
        String title = editTextTitle.getText().toString();
        String message = editTextMessage.getText().toString();

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_one)
                .setContentTitle(title)
                .setContentText(message)
                .build();
        notificationManager.notify(1, notification);
    }

    public void sendOnChannel2(View v) {
        String title = editTextTitle.getText().toString();
        String message = editTextMessage.getText().toString();

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_two)
                .setContentTitle(title)
                .setContentText(message)
                .build();
        notificationManager.notify(2, notification);
    }

    public void collectMail(View v) {
        StatusBarNotification[] sbns = InterceptionService.instance.getNotifications();

        //Clear the existing display
        linearScroll.removeAllViews();

        // If we don't have any messages display the empty message
        if(sbns.length == 0) {
            Log.i("Output", "Empty!!!");

            TextView empty = new TextView(this);
            empty.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            empty.setText(R.string.no_mail_text);
            linearScroll.addView(empty);

            return;
        }

        for (StatusBarNotification sbn : sbns) {
            Log.i("Output", sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName() + "\t" + sbn.getKey());

            Bundle extras = sbn.getNotification().extras;


            // Horizontal Layout
            LinearLayout notif = new LinearLayout(this);
            notif.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            notif.setOrientation(LinearLayout.HORIZONTAL);
            linearScroll.addView(notif);

            // Add image to LHS
            // TODO: Get notification image
            ImageView small_icon = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            small_icon.setLayoutParams(params);
            small_icon.setImageResource(R.drawable.ic_one);
            notif.addView(small_icon);

            // Add LinearLayout to RHS
            LinearLayout details = new LinearLayout(this);
            details.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            details.setOrientation(LinearLayout.VERTICAL);
            notif.addView(details);

            // Add app title
            TextView app = new TextView(this);
            Log.i("OUTPUT", sbn.getPackageName());
            app.setText(sbn.getPackageName());
            app.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            details.addView(app);

            // Add notif title
            TextView title = new TextView(this);
            Log.i("OUTPUT", extras.getString(Notification.EXTRA_TITLE));
            title.setText(extras.getString(Notification.EXTRA_TITLE));
            title.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            details.addView(title);

            // Add notif content
            TextView content = new TextView(this);
            Log.i("OUTPUT", extras.getString(Notification.EXTRA_TEXT));
            content.setText(extras.getString(Notification.EXTRA_TEXT));
            content.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            details.addView(content);
        }
        InterceptionService.instance.clearPostbox();
    }

    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),"enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private AlertDialog buildNotificationServiceAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.notification_listener_service);
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation);
        // Button actions
        alertDialogBuilder.setPositiveButton(R.string.yes,
                (dialog, id) -> startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")));
        alertDialogBuilder.setNegativeButton(R.string.no,
                (dialog, id) -> { });
        return(alertDialogBuilder.create());
    }
}
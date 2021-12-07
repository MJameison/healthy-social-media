package com.squadrant.postboxnotification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NotificationView extends LinearLayout {
    private ImageView mNotifIcon;
    private LinearLayout mInfo;
    private TextView mAppName;
    private TextView mNotifTitle;
    private TextView mNotifContent;
    private ImageView mClose;

    public NotificationView(Context context) {
        this(context, null);
    }

    public NotificationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NotificationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.notification_layout, this);

        mNotifIcon = (ImageView) findViewById(R.id.notifIcon);
        mInfo = (LinearLayout) findViewById(R.id.notifDetails);
        mAppName = (TextView) findViewById(R.id.notifAppName);
        mNotifTitle = (TextView) findViewById(R.id.notifTitle);
        mNotifContent = (TextView) findViewById(R.id.notifContent);
        mClose = (ImageView) findViewById(R.id.notifClose);
    }

    // TODO: Add button callback method!
    public void setSBN(StatusBarNotification sbn) {
        Notification notif = sbn.getNotification();
        Bundle extras = notif.extras;

        // App icon
        /*
        try {
            Resources res = getPackageManager().getResourcesForApplication(sbn.getPackageName());
            int resId = sbn.getNotification().getSmallIcon().getResId();
            if(resId != 0) {
                Log.i("ResId", res.getResourceName(resId));
                small_icon.setImageDrawable(res.getDrawable(resId, getTheme()));
            }

        } catch (PackageManager.NameNotFoundException e) {
            Log.w("ResId", "Error!!!");
        }
         */
        // TODO: Add default if not in our package! For each main app a default unknown
        mNotifIcon.setImageResource(notif.getSmallIcon().getResId());

        // App title
        ApplicationInfo ai;
        try {
            ai = getContext().getPackageManager().getApplicationInfo(sbn.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            ai = null;
        }
        mAppName.setText(ai != null ? getContext().getPackageManager().getApplicationLabel(ai) : "(unknown)");


        // Notif Title
        mNotifTitle.setText(extras.getString(Notification.EXTRA_TITLE));

        // Notif Content
        mNotifContent.setText(extras.getString(Notification.EXTRA_TEXT));
    }

    public void setCloseMethod(OnClickListener listener) {
        mClose.setOnClickListener(listener);
    }
}

package com.example.postboxnotification;

import android.content.Context ;
import android.service.notification.NotificationListenerService ;
import android.service.notification.StatusBarNotification ;
import android.util.Log ;
public class InterceptionService extends NotificationListenerService {
    private final String TAG = this .getClass().getSimpleName() ;
    Context context ;

    @Override
    public void onCreate () {
        super .onCreate() ;
        context = getApplicationContext() ;
    }
    @Override
    public void onNotificationPosted (StatusBarNotification sbn) {
        Log.i(TAG ,"Posted ID:" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
        // Here's how we can kill notifications of any type
        //cancelNotification(sbn.getKey());
    }
    @Override
    public void onNotificationRemoved (StatusBarNotification sbn) {
        Log.i(TAG ,"Removed ID:" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
    }
}
package com.squadrant.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.format.DateUtils;

import com.squadrant.App;
import com.squadrant.postboxnotification.R;

/**
 * Contains utilities for converting package names to human readable data.
 */
public class PackageNameUtils {
    public static String getAppName(String packageName) {
        ApplicationInfo ai;
        Context context = App.getContext();
        try {
            ai = context.getPackageManager().getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            ai = null;
        }
        return ai != null ? (String) context.getPackageManager().getApplicationLabel(ai) : "(unknown)";
    }

    public static String getTimeSent(Context context, long time) {
        return DateUtils.formatDateTime(context, time, DateUtils.FORMAT_SHOW_TIME);
    }

    public static int getIconResource(String packageName) {
        // TODO: Populate with common SM apps
        switch (packageName) {
            case "com.google.android.apps.messaging":
                return R.drawable.text_message;
            case "com.example":
                return R.drawable.f_logo_rgb_blue_512;
            default:
                return R.drawable.exclamation_mark;
        }
    }
}

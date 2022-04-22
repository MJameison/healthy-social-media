package com.squadrant.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;

import androidx.appcompat.content.res.AppCompatResources;

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

    public static Drawable getAppIcon(String packageName) {
        try {
            return App.getContext().getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            return AppCompatResources.getDrawable(App.getContext(), R.drawable.exclamation_mark);
        }
    }
}

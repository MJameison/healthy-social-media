package com.squadrant.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.squadrant.App;
import com.squadrant.postboxnotification.R;

import java.util.Objects;

/**
 * Contains utilities for converting package names to human readable data.
 */
public class PackageNameUtils {

    @NonNull
    public static String getAppName(@NonNull Context context, String packageName) {
        ApplicationInfo ai;
        try {
            ai = context.getPackageManager().getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            ai = null;
        }
        return ai != null ? (String) context.getPackageManager().getApplicationLabel(ai) : "(unknown)";
    }

    @NonNull
    public static String getTimeSent(@NonNull Context context, long time) {
        return DateUtils.formatDateTime(context, time, DateUtils.FORMAT_SHOW_TIME);
    }

    @NonNull
    public static Drawable getAppIcon(@NonNull Context context, String packageName) {
        try {
            return context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            return Objects.requireNonNull(AppCompatResources.getDrawable(context, R.drawable.exclamation_mark));
        }
    }
}

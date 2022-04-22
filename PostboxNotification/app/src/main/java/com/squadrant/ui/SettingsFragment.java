package com.squadrant.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.squadrant.App;
import com.squadrant.postboxnotification.R;
import com.squadrant.util.PackageNameUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String APP_FILTER_SET = "app_filter_set";

    private static List<AppFilterItem> appItems = new ArrayList<>();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        SwitchPreferenceCompat interceptEnablePreference = findPreference("intercept_notifications");
        if (interceptEnablePreference != null) {
            interceptEnablePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                if ((boolean) newValue && !isNotificationServiceEnabled()) {
                    // We don't have the permission
                    RequestPermissionDialogFragment dialogFragment = new RequestPermissionDialogFragment();
                    dialogFragment.show(requireActivity().getSupportFragmentManager(), null);
                    return false;
                }
                return true;
            });
        }

        // Get App List
        PreferenceCategory appListCategory = findPreference("app_list_category");
        if (appListCategory == null) {
            Log.w("SettingsFragment", "App list category is null");
            return;
        }

        // Add items to preference display
        for (AppFilterItem item : appItems) {
            SwitchPreferenceCompat appSwitch = item.createSwitchPreference();
            appListCategory.addPreference(appSwitch);
        }
    }

    public void findAllApps() {
        // Get app info
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> appList = App.getContext().getPackageManager().queryIntentActivities( intent , 0);

        // Convert to AppFilterItems
        appItems = new ArrayList<>();
        for (ResolveInfo ri : appList) {
            appItems.add(new AppFilterItem(ri.activityInfo.packageName));
        }
        // Remove duplicates and sort
        appItems = appItems.stream().filter(o -> !o.appName.startsWith("com."))
                .collect(Collectors.toMap(AppFilterItem::getPackageName, p -> p, (p, q) -> p)).values()
                .stream().sorted(Comparator.comparing(o->o.appName)).collect(Collectors.toList());
    }


    @Override
    public void onResume() {
        super.onResume();
        // if the user revoked permission update the preference display to match
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        if (!isNotificationServiceEnabled() && preferences.getBoolean("intercept_notifications", false)) {
            preferences.edit().putBoolean("intercept_notifications", false).apply();
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isNotificationServiceEnabled(){
        String pkgName = requireContext().getPackageName();
        String[] listeners = Settings.Secure.getString(requireContext().getContentResolver(), "enabled_notification_listeners").split(":");
        for (String name : listeners) {
            final ComponentName cn = ComponentName.unflattenFromString(name);
            if (cn != null && TextUtils.equals(pkgName, cn.getPackageName()))
                return true;
        }
        return false;
    }

    private class AppFilterItem {
        String packageName;
        String appName;
        boolean checked;

        public String getPackageName() {
            return packageName;
        }

        public AppFilterItem(String packageName) {
            this.packageName = packageName;
            this.appName = PackageNameUtils.getAppName(packageName);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getContext());
            Set<String> appFilter = prefs.getStringSet(APP_FILTER_SET, new HashSet<>());
            this.checked = appFilter.contains(packageName);
        }

        public SwitchPreferenceCompat createSwitchPreference() {
            SwitchPreferenceCompat appSwitch = new SwitchPreferenceCompat(getPreferenceManager().getContext());
            appSwitch.setChecked(checked);
            appSwitch.setTitle(appName);
            appSwitch.setKey(packageName);
            appSwitch.setIcon(PackageNameUtils.getAppIcon(packageName));
            appSwitch.setPersistent(false);

            // Set listener to handle custom write back to shared preferences
            appSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                String key = preference.getKey();
                boolean value = (boolean) newValue;

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
                // Get the current appFilter
                Set<String> newSet = new HashSet<>(prefs.getStringSet(APP_FILTER_SET, new HashSet<>()));
                if (value) {
                    newSet.add(key);
                } else {
                    newSet.remove(key);
                }
                // Write back
                prefs.edit().putStringSet(APP_FILTER_SET, newSet).apply();

                return true;
            });
            return appSwitch;
        }

    }
}
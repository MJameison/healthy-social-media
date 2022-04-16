package com.squadrant.ui;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.squadrant.postboxnotification.R;

public class SettingsFragment extends PreferenceFragmentCompat {

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
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void onResume() {
        super.onResume();

        // if the user revoked permission update the preference display to match
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        if (!isNotificationServiceEnabled() && preferences.getBoolean("intercept_notifications", false)) {
            preferences.edit().putBoolean("intercept_notifications", false).commit();
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
}
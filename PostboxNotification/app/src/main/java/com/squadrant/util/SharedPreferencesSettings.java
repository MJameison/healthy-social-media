package com.squadrant.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesSettings implements Settings {

    private final SharedPreferences prefs;

    public SharedPreferencesSettings(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public boolean getBoolean(String key) {
        return prefs.getBoolean(key, false);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    @Override
    public Set<String> getStringSet(String key) {
        return new HashSet<>(prefs.getStringSet("app_filter_set", new HashSet<>()));
    }

    @Override
    public void writeStringSet(String key, Set<String> value) {
        prefs.edit().putStringSet(key, value).apply();
    }
}

package com.squadrant.util;

import android.content.Context;

import java.util.Set;

public interface Settings {
    boolean getBoolean(String key);
    void setBoolean(String key, boolean value);
    Set<String> getStringSet(String key);
    void writeStringSet(String key, Set<String> newValue);

    static Settings getDefaultSettings(Context context) {
        return new SharedPreferencesSettings(context);
    }
}

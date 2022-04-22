package com.squadrant;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squadrant.ui.DebugFragment;
import com.squadrant.ui.DisplayNotificationsFragment;
import com.squadrant.ui.SettingsFragment;
import com.squadrant.postboxnotification.R;

public class MainActivity extends AppCompatActivity {

    private final DisplayNotificationsFragment notificationsFragment = new DisplayNotificationsFragment();
    private final SettingsFragment settingsFragment = new SettingsFragment();
    private final DebugFragment debugFragment = new DebugFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, notificationsFragment, null)
                    .commit();
        }

        // This is a lengthy operation so done now on a background thread
        Thread thread = new Thread(settingsFragment::findAllApps);
        thread.start();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_postbox) {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container_view, notificationsFragment)
                        .commit();
                return true;
            }
            if (itemId == R.id.nav_settings) {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container_view, settingsFragment)
                        .commit();
                return true;
            }
            if (itemId == R.id.nav_debug) {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container_view, debugFragment)
                        .commit();
                return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_postbox);
    }
}
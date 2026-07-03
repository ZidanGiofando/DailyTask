package com.example.dailytask.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.dailytask.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity menampung 3 Fragment (Dashboard, Task, Profile) yang dipindah
 * menggunakan BottomNavigationView (bukan berpindah Activity, agar UX lebih halus).
 * Navigasi antar Activity (Intent) tetap dipakai untuk:
 *  - MainActivity -> AddTaskActivity
 *  - MainActivity -> DetailTaskActivity
 *  - LoginActivity <-> RegisterActivity <-> MainActivity
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Tampilkan Dashboard sebagai halaman awal
        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
        }

        // Cari baris ini di MainActivity.java dan ubah sesuai ID di menu:

        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();

            // Ubah nav_home menjadi menu_dashboard
            if (id == R.id.menu_dashboard) {
                fragment = new DashboardFragment();
            }
            // Ubah nav_task menjadi menu_task
            else if (id == R.id.menu_task) {
                fragment = new TaskFragment();
            }
            // Ubah nav_profile menjadi menu_profile
            else if (id == R.id.menu_profile) {
                fragment = new ProfileFragment();
            }

            return loadFragment(fragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment == null) return false;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainer, fragment);
        transaction.commit();
        return true;
    }

    /** Dipanggil dari TaskFragment agar bottom nav pindah ke tab Task setelah tambah data, dsb. */
    public void goToTaskTab() {
        bottomNavigation.setSelectedItemId(R.id.menu_task); // Ubah nav_task menjadi menu_task
    }
}

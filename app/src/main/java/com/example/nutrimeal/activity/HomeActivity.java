package com.example.nutrimeal.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.nutrimeal.R;
import com.example.nutrimeal.utils.ThemeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_home);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Setup manual supaya setiap tap BottomNav selalu fresh
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            int currentId = navController.getCurrentDestination() != null
                    ? navController.getCurrentDestination().getId() : -1;

            // Kalau sudah di fragment yang sama, tidak perlu navigate
            if (currentId == itemId) return true;

            NavOptions navOptions = new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setPopUpTo(R.id.homeFragment, false)
                    .build();

            navController.navigate(itemId, null, navOptions);
            return true;
        });

        // Sync BottomNav highlight dengan fragment aktif
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destId = destination.getId();
            if (destId == R.id.homeFragment
                    || destId == R.id.searchFragment
                    || destId == R.id.plannerFragment
                    || destId == R.id.profileFragment) {
                bottomNav.setSelectedItemId(destId);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
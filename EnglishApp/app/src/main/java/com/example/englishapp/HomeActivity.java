package com.example.englishapp;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.englishapp.Fragment.LessonFragment;
import com.example.englishapp.Fragment.NotificationFragment;
import com.example.englishapp.Fragment.ProfileFragment;
import com.example.englishapp.Fragment.StatisticsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private BottomNavigationView bottomNavigationView;
    private ViewGroup topBar;
    private View scrollViewHomeContent;
    private String currentTab = "home"; // Track current tab

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        try {
            setupViews();
            setupBottomNavigation();
            setupVocabularyButton();

            // Mặc định mở Home
            setHomeActive();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Failed to initialize app", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViews() {
        try {
            ImageView btnNotification = findViewById(R.id.btn_notification);
            if (btnNotification != null) {
                btnNotification.setOnClickListener(v -> showNotificationFragment());
            }

            topBar = findViewById(R.id.top_bar);
            scrollViewHomeContent = findViewById(R.id.scrollView2);
            bottomNavigationView = findViewById(R.id.bottom_navigation);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up views", e);
        }
    }

    private void setupBottomNavigation() {
        if (bottomNavigationView == null) {
            Log.e(TAG, "Bottom navigation view is null");
            return;
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                try {
                    int itemId = item.getItemId();

                    if (itemId == R.id.nav_home) {
                        navigateToHome();
                    } else if (itemId == R.id.nav_lesson) {
                        navigateToLesson();
                    } else if (itemId == R.id.nav_statistics) {
                        navigateToStatistics();
                    } else if (itemId == R.id.nav_profile) {
                        navigateToProfile();
                    }

                    return true;
                } catch (Exception e) {
                    Log.e(TAG, "Error handling navigation item", e);
                    Toast.makeText(HomeActivity.this, "Navigation failed", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });
    }

    private void setupVocabularyButton() {
        Button btn_onVocab = findViewById(R.id.btn_onVocab);
        if (btn_onVocab != null) {
            btn_onVocab.setOnClickListener(v -> {
                navigateToLesson();
                if (bottomNavigationView != null) {
                    bottomNavigationView.setSelectedItemId(R.id.nav_lesson);
                }
            });
        }
    }

    /**
     * Navigate to Home - show home content
     */
    private void navigateToHome() {
        try {
            if (currentTab.equals("home")) {
                Log.d(TAG, "Already on home");
                return;
            }

            Log.d(TAG, "Navigating to Home");

            // Clear ALL fragments from back stack
            clearAllFragments();

            // Show home content
            setHomeContentVisibility(View.VISIBLE);
            setTopBarVisibility(View.VISIBLE);

            currentTab = "home";
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to home", e);
            Toast.makeText(this, "Failed to navigate to home", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Navigate to Lesson fragment
     */
    private void navigateToLesson() {
        try {
            if (currentTab.equals("lesson")) {
                Log.d(TAG, "Already on lesson");
                return;
            }

            Log.d(TAG, "Navigating to Lesson");

            // Clear all fragments first
            clearAllFragments();

            // Hide home content
            setHomeContentVisibility(View.GONE);
            setTopBarVisibility(View.GONE);

            // Load LessonFragment (which will show ListeningActivity by default)
            Fragment lessonFragment = new LessonFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, lessonFragment, "LessonFragment")
                    .commit();

            currentTab = "lesson";
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to lesson", e);
            Toast.makeText(this, "Failed to navigate to lesson", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Navigate to Statistics fragment
     */
    private void navigateToStatistics() {
        try {
            if (currentTab.equals("statistics")) {
                Log.d(TAG, "Already on statistics");
                return;
            }

            Log.d(TAG, "Navigating to Statistics");

            clearAllFragments();
            setHomeContentVisibility(View.GONE);
            setTopBarVisibility(View.GONE);

            Fragment statisticsFragment = new StatisticsFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, statisticsFragment, "StatisticsFragment")
                    .commit();

            currentTab = "statistics";
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to statistics", e);
            Toast.makeText(this, "Failed to navigate to statistics", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Navigate to Profile fragment
     */
    private void navigateToProfile() {
        try {
            if (currentTab.equals("profile")) {
                Log.d(TAG, "Already on profile");
                return;
            }

            Log.d(TAG, "Navigating to Profile");

            clearAllFragments();
            setHomeContentVisibility(View.GONE);
            setTopBarVisibility(View.GONE);

            Fragment profileFragment = new ProfileFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, profileFragment, "ProfileFragment")
                    .commit();

            currentTab = "profile";
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to profile", e);
            Toast.makeText(this, "Failed to navigate to profile", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Clear all fragments from container and back stack
     */
    private void clearAllFragments() {
        try {
            FragmentManager fm = getSupportFragmentManager();

            // Pop all back stack entries
            if (fm.getBackStackEntryCount() > 0) {
                Log.d(TAG, "Clearing " + fm.getBackStackEntryCount() + " back stack entries");
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

            // Remove current fragment if exists
            Fragment currentFragment = fm.findFragmentById(R.id.container);
            if (currentFragment != null) {
                Log.d(TAG, "Removing current fragment: " + currentFragment.getClass().getSimpleName());
                fm.beginTransaction()
                        .remove(currentFragment)
                        .commitNow(); // Use commitNow to execute immediately
            }

            Log.d(TAG, "All fragments cleared");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing fragments", e);
        }
    }

    /**
     * Set home active - called on app start
     */
    private void setHomeActive() {
        try {
            clearAllFragments();
            setHomeContentVisibility(View.VISIBLE);
            setTopBarVisibility(View.VISIBLE);
            currentTab = "home";

            if (bottomNavigationView != null) {
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting home active", e);
        }
    }

    private void setHomeContentVisibility(int visibility) {
        if (scrollViewHomeContent != null) {
            scrollViewHomeContent.setVisibility(visibility);
            Log.d(TAG, "Home content visibility: " + (visibility == View.VISIBLE ? "VISIBLE" : "GONE"));
        }
    }

    private void setTopBarVisibility(int visibility) {
        if (topBar != null) {
            topBar.setVisibility(visibility);
            Log.d(TAG, "Top bar visibility: " + (visibility == View.VISIBLE ? "VISIBLE" : "GONE"));
        }
    }

    /**
     * Show notification dialog
     */
    private void showNotificationFragment() {
        try {
            NotificationFragment fragment = new NotificationFragment();
            fragment.show(getSupportFragmentManager(), "notification_dialog");
        } catch (Exception e) {
            Log.e(TAG, "Error showing notification", e);
            Toast.makeText(this, "Failed to show notifications", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            FragmentManager fm = getSupportFragmentManager();

            // Check if there are fragments in back stack (nested navigation)
            if (fm.getBackStackEntryCount() > 0) {
                Log.d(TAG, "Popping back stack, entries: " + fm.getBackStackEntryCount());
                fm.popBackStack();
                return;
            }

            // Check current fragment
            Fragment currentFragment = fm.findFragmentById(R.id.container);
            if (currentFragment != null) {
                // If on a fragment, go back to home
                Log.d(TAG, "On fragment, returning to home");
                navigateToHome();
                if (bottomNavigationView != null) {
                    bottomNavigationView.setSelectedItemId(R.id.nav_home);
                }
                return;
            }

            // If already on home, exit app
            if (currentTab.equals("home")) {
                Log.d(TAG, "On home, exiting app");
                super.onBackPressed();
            } else {
                // Fallback: go to home
                navigateToHome();
                if (bottomNavigationView != null) {
                    bottomNavigationView.setSelectedItemId(R.id.nav_home);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling back press", e);
            super.onBackPressed();
        }
    }
}
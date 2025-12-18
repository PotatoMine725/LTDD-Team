package com.example.englishapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.TraceCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.englishapp.ui.vocabulary.LessonFragment;
import com.example.englishapp.ui.common.NotificationFragment;
import com.example.englishapp.ui.profile.ProfileFragment;
import com.example.englishapp.ui.stats.StatisticsFragment;
import com.example.englishapp.ui.listening.ListeningActivity;
import com.example.englishapp.R;
import com.example.englishapp.ui.speaking.SpeakingActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private BottomNavigationView bottomNavigationView;
    private ViewGroup topBar;
    private View scrollViewHomeContent;
    private String currentTab = "home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        try {
            setupViews();
            setupBottomNavigation();
            setupVocabularyButton();

            // Hàm thiết lập xử lý nút back mới
            setupOnBackPressed();

            handleIncomingIntent();
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

    /**
     * PUBLIC method để TopTabNavigationHelper có thể restore listener
     */
    public void setupBottomNavigation() {
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

            // 🔧 FIX #3: Clear ALL fragments properly
            clearAllFragmentsProperly();

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
            clearAllFragmentsProperly();

            // Hide home content
            setHomeContentVisibility(View.GONE);
            setTopBarVisibility(View.GONE);

            // Load LessonFragment (which will show Vocabulary by default)
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

            clearAllFragmentsProperly();
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

            clearAllFragmentsProperly();
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

    // Thêm vào HomeActivity.java

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIncomingIntent();
    }

    /**
     * Handle intent từ QuizActivity hoặc các Activity khác
     */
    private void handleIncomingIntent() {
        try {
            Intent intent = getIntent();
            String selectedTab = intent.getStringExtra("SELECTED_TAB");

            if (selectedTab != null) {
                Log.d(TAG, "Handling intent with tab: " + selectedTab);

                switch (selectedTab) {
                    case "VOCABULARY":
                        // 🔧 FIX: Navigate đến Lesson, rồi trigger tab Vocabulary
                        navigateToLesson();
                        if (bottomNavigationView != null) {
                            bottomNavigationView.setSelectedItemId(R.id.nav_lesson);
                        }
                        break;

                    case "LISTENING":
                        // 🔧 FIX: Navigate đến Listening tab
                        navigateToListeningTab();
                        break;

                    case "SPEAKING":
                        // 🔧 FIX: Navigate đến Speaking tab
                        navigateToSpeakingTab();
                        break;

                    case "STATISTICS":
                        navigateToStatistics();
                        if (bottomNavigationView != null) {
                            bottomNavigationView.setSelectedItemId(R.id.nav_statistics);
                        }
                        break;

                    case "PROFILE":
                        navigateToProfile();
                        if (bottomNavigationView != null) {
                            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
                        }
                        break;

                    default:
                        setHomeActive();
                        break;
                }

                // Clear intent để tránh re-trigger khi rotate screen
                intent.removeExtra("SELECTED_TAB");
            } else {
                // Không có intent đặc biệt → hiển thị Home
                setHomeActive();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling intent", e);
            setHomeActive();
        }
    }

    /**
     * Navigate to Listening tab
     */
    private void navigateToListeningTab() {
        try {
            Log.d(TAG, "Navigating to Listening Tab");

            clearAllFragmentsProperly();
            setHomeContentVisibility(View.GONE);
            setTopBarVisibility(View.GONE);

            // Load ListeningActivity fragment
            Fragment listeningFragment = new ListeningActivity();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, listeningFragment, "ListeningActivity")
                    .commit();

            currentTab = "lesson";

            if (bottomNavigationView != null) {
                bottomNavigationView.setSelectedItemId(R.id.nav_lesson);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to listening", e);
            Toast.makeText(this, "Failed to navigate to listening", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Navigate to Speaking tab
     */
    private void navigateToSpeakingTab() {
        try {
            Log.d(TAG, "Navigating to Speaking Tab");

            clearAllFragmentsProperly();
            setHomeContentVisibility(View.GONE);
            setTopBarVisibility(View.GONE);

            // Load SpeakingActivity fragment
            Fragment speakingFragment = new SpeakingActivity();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, speakingFragment, "SpeakingActivity")
                    .commit();

            currentTab = "lesson";

            if (bottomNavigationView != null) {
                bottomNavigationView.setSelectedItemId(R.id.nav_lesson);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to speaking", e);
            Toast.makeText(this, "Failed to navigate to speaking", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 🔧 FIX #3: Clear all fragments PROPERLY - sử dụng popBackStackImmediate
     * Phương pháp này đảm bảo back stack được clear NGAY LẬP TỨC và ĐỒNG BỘ
     */
    private void clearAllFragmentsProperly() {
        try {
            FragmentManager fm = getSupportFragmentManager();

            // Method 1: Pop tất cả back stack entries NGAY LẬP TỨC
            if (fm.getBackStackEntryCount() > 0) {
                Log.d(TAG, "Clearing " + fm.getBackStackEntryCount() + " back stack entries");

                // 🔧 SỬ DỤNG popBackStackImmediate thay vì popBackStack
                // popBackStackImmediate() thực thi ĐỒNG BỘ (synchronous)
                // popBackStack() thực thi BẤT ĐỒNG BỘ (asynchronous) → có thể gây bug
                fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

            // Method 2: Remove current fragment if exists
            Fragment currentFragment = fm.findFragmentById(R.id.container);
            if (currentFragment != null) {
                Log.d(TAG, "Removing current fragment: " + currentFragment.getClass().getSimpleName());

                // 🔧 SỬ DỤNG commitNow() thay vì commit()
                // commitNow() thực thi ĐỒNG BỘ (synchronous)
                // commit() thực thi BẤT ĐỒNG BỘ (asynchronous)
                fm.beginTransaction()
                        .remove(currentFragment)
                        .commitNow();
            }

            Log.d(TAG, "All fragments cleared properly");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing fragments", e);

            // Fallback: Nếu có lỗi, thử clear bằng cách khác
            try {
                FragmentManager fm = getSupportFragmentManager();
                for (Fragment fragment : fm.getFragments()) {
                    if (fragment != null) {
                        fm.beginTransaction().remove(fragment).commitNowAllowingStateLoss();
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, "Fallback clear also failed", ex);
            }
        }
    }

    /**
     * Set home active - called on app start
     */
    private void setHomeActive() {
        try {
            clearAllFragmentsProperly();
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

    private void setupOnBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* bật callback theo mặc định */) {
            @Override
            public void handleOnBackPressed() {

                try {
                    FragmentManager fm = getSupportFragmentManager();

                    //Có fragments trong back stack
                    if (fm.getBackStackEntryCount() > 0) {
                        Log.d(TAG, "Popping back stack, entries: " + fm.getBackStackEntryCount());
                        fm.popBackStackImmediate();
                        return;
                    }

                    //Đang ở một fragment chính (Lesson/Statistics/Profile) -> quay về Home
                    Fragment currentFragment = fm.findFragmentById(R.id.container);
                    if (currentFragment != null && !currentTab.equals("home")) {
                        Log.d(TAG, "On fragment, returning to home");
                        navigateToHome();
                        if (bottomNavigationView != null) {
                            bottomNavigationView.setSelectedItemId(R.id.nav_home);
                        }
                        return;
                    }

                    //Đã ở Home -> thoát ứng dụng
                    // Để thoát, ta phải vô hiệu hóa callback này và gọi lại super.onBackPressed()
                    if (currentTab.equals("home")) {
                        Log.d(TAG, "On home, exiting app");
                        // Vô hiệu hóa callback này để tránh vòng lặp vô hạn
                        setEnabled(false);
                        // Gọi lại hành vi mặc định (sẽ thoát app vì không còn gì trong back stack)
                        getOnBackPressedDispatcher().onBackPressed();
                        // Bật lại nếu cần (ví dụ: nếu người dùng không thoát)
                        setEnabled(true);
                        return;
                    }

                    // Fallback: Về home
                    Log.d(TAG, "Fallback: navigating to home");
                    navigateToHome();
                    if (bottomNavigationView != null) {
                        bottomNavigationView.setSelectedItemId(R.id.nav_home);
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Error handling back press", e);
                    // Nếu có lỗi, gọi hành vi mặc định để tránh crash
                    if (TraceCompat.isEnabled()) {
                        setEnabled(false);
                        getOnBackPressedDispatcher().onBackPressed();
                        setEnabled(true);
                    }
                }
            }
        };

        // Đăng ký callback với dispatcher của Activity
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}
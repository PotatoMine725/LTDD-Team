package com.example.englishapp.ui.common.tabnavigation;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.englishapp.R;
import com.example.englishapp.ui.home.HomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Coordinator xử lý tích hợp với Bottom Navigation
 */
public class BottomNavCoordinator {
    private static final String TAG = "BottomNavCoordinator";

    private Context context;
    private BottomNavigationView bottomNav;
    private Runnable onNavigationComplete;

    public BottomNavCoordinator(Context context) {
        this.context = context;
    }

    /**
     * Kiểm tra và trigger bottom navigation nếu cần
     * @param onComplete Callback khi navigation hoàn tất
     * @return true nếu đã trigger navigation, false nếu không cần
     */
    public boolean checkAndTriggerBottomNav(Runnable onComplete) {
        try {
            this.onNavigationComplete = onComplete;

            // Check nếu context là HomeActivity
            if (context instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) context;
                bottomNav = homeActivity.findViewById(R.id.bottom_navigation);

                if (bottomNav != null) {
                    int selectedItemId = bottomNav.getSelectedItemId();

                    // Nếu KHÔNG đang ở tab Lesson → trigger bottom nav
                    if (selectedItemId != R.id.nav_lesson) {
                        Log.d(TAG, "Not on Lesson tab, triggering bottom navigation to Lesson");

                        // Set listener một lần để biết khi nào bottom nav đã switch xong
                        bottomNav.setOnItemSelectedListener(item -> {
                            if (item.getItemId() == R.id.nav_lesson) {
                                // post() sẽ chạy sau khi bottom nav đã update xong UI
                                bottomNav.post(() -> {
                                    if (onNavigationComplete != null) {
                                        onNavigationComplete.run();
                                    }
                                    // Restore lại listener cũ (từ HomeActivity)
                                    homeActivity.setupBottomNavigation();
                                });

                                return true;
                            }
                            return false;
                        });

                        // Trigger bottom nav switch
                        bottomNav.setSelectedItemId(R.id.nav_lesson);
                        return true;
                    }
                }
            }

            // Nếu đã ở tab Lesson hoặc không phải HomeActivity → không cần trigger
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error checking bottom nav", e);
            return false;
        }
    }
}


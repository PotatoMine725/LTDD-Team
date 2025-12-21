package com.example.englishapp.ui.common.tabnavigation;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import com.example.englishapp.ui.common.TopTabNavigationHelper;

/**
 * Handler xử lý logic navigation giữa các tab
 */
public class TabNavigationHandler {
    private static final String TAG = "TabNavigationHandler";

    private Context context;
    private FragmentNavigator fragmentNavigator;
    private BottomNavCoordinator bottomNavCoordinator;
    private TabUIHandler uiHandler;
    private TabType currentTab = TabType.VOCABULARY;
    private TopTabNavigationHelper.OnTabSelectedListener listener;
    private boolean isNavigating = false;

    public TabNavigationHandler(Context context, FragmentManager fragmentManager, int containerId, TabUIHandler uiHandler) {
        this.context = context;
        this.uiHandler = uiHandler;
        this.fragmentNavigator = new FragmentNavigator(fragmentManager, containerId);
        this.bottomNavCoordinator = new BottomNavCoordinator(context);
    }

    /**
     * Constructor cho Quiz mode (không cần FragmentManager)
     */
    public TabNavigationHandler(Context context, TabUIHandler uiHandler) {
        this.context = context;
        this.uiHandler = uiHandler;
        this.fragmentNavigator = null;
        this.bottomNavCoordinator = new BottomNavCoordinator(context);
    }

    public void selectTab(TabType tabType) {
        try {
            // Kiểm tra flag để tránh recursive call
            if (isNavigating) {
                Log.d(TAG, "Navigation already in progress, ignoring selectTab");
                return;
            }

            Log.d(TAG, "Selecting tab: " + tabType);

            // Update UI
            uiHandler.updateTabUI(tabType);

            // Notify listener
            if (listener != null) {
                listener.onTabSelected(tabType);
            }

            // Handle navigation
            handleNavigation(tabType);
            currentTab = tabType;
        } catch (Exception e) {
            Log.e(TAG, "Error selecting tab", e);
        }
    }

    private void handleNavigation(TabType tabType) {
        try {
            // Kiểm tra flag để tránh recursive call
            if (isNavigating) {
                Log.d(TAG, "Already navigating, skip");
                return;
            }

            // Nếu có FragmentNavigator (normal mode), kiểm tra bottom nav
            if (fragmentNavigator != null) {
                // Set flag TRƯỚC KHI trigger bottom nav
                isNavigating = true;

                // Kiểm tra và trigger bottom nav nếu cần
                boolean triggered = bottomNavCoordinator.checkAndTriggerBottomNav(() -> {
                    performTabNavigation(tabType);
                    // Reset flag sau khi navigation xong
                    isNavigating = false;
                });

                if (!triggered) {
                    // Không cần trigger bottom nav → navigate bình thường
                    performTabNavigation(tabType);
                    isNavigating = false;
                }
            } else {
                // Quiz mode - không navigate fragment
                isNavigating = false;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error handling navigation", e);
            isNavigating = false; // Reset flag nếu có lỗi
        }
    }

    /**
     * Thực hiện navigation thực sự giữa các tab
     */
    private void performTabNavigation(TabType tabType) {
        try {
            if (fragmentNavigator != null) {
                fragmentNavigator.navigateToTab(tabType);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error performing tab navigation", e);
        }
    }

    public void setCurrentTab(TabType tabType) {
        currentTab = tabType;
        uiHandler.updateTabUI(tabType);
        Log.d(TAG, "Current tab set to: " + tabType);
    }

    public TabType getCurrentTab() {
        return currentTab;
    }

    public void setOnTabSelectedListener(TopTabNavigationHelper.OnTabSelectedListener listener) {
        this.listener = listener;
    }

    public void resetNavigationFlag() {
        isNavigating = false;
    }
}


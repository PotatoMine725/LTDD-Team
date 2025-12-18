package com.example.englishapp.ui.speaking;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.englishapp.ui.common.NotificationFragment;
import com.example.englishapp.R;
import com.example.englishapp.ui.common.TopTabNavigationHelper;

/**
 * SpeakingActivity - Main speaking screen
 * Shows welcome screen with "Start Learning" button
 */
public class SpeakingActivity extends Fragment {

    private static final String TAG = "SpeakingActivity";
    private TopTabNavigationHelper tabNavigationHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.speaking_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            // Khởi tạo Tab Navigation Helper
            tabNavigationHelper = new TopTabNavigationHelper(
                    view,
                    requireContext(),
                    getParentFragmentManager()
            );

            tabNavigationHelper.resetQuizTabText();

            // Set tab hiện tại là Speaking
            tabNavigationHelper.setCurrentTab(TopTabNavigationHelper.TabType.SPEAKING);

            // Setup các components
            setupNotificationButton(view);
            setupAvatarClick(view);
            setupStartLearningButton(view);

            Log.d(TAG, "SpeakingActivity initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated", e);
            showMessage("Failed to initialize speaking screen");
        }
    }

    /**
     * Setup notification button
     */
    private void setupNotificationButton(View view) {
        ImageView btnNotification = view.findViewById(R.id.notification_icon);
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> showNotificationFragment());
        } else {
            Log.e(TAG, "notification_icon not found in layout");
        }
    }

    /**
     * Setup avatar click listener
     */
    private void setupAvatarClick(View view) {
        ImageView avatarImage = view.findViewById(R.id.avatar_image);
        if (avatarImage != null) {
            avatarImage.setOnClickListener(v -> {
                Log.d(TAG, "Avatar clicked");
                // TODO: Navigate to profile
                showMessage("Profile - Coming soon");
            });
        } else {
            Log.e(TAG, "avatar_image not found in layout");
        }
    }

    /**
     * Setup Start Learning button - Navigate to Speaking Topics
     */
    private void setupStartLearningButton(View view) {
        Button startButton = view.findViewById(R.id.btn_start);
        if (startButton != null) {
            startButton.setOnClickListener(v -> {
                Log.d(TAG, "Start Learning button clicked");
                navigateToSpeakingTopics();
            });
        } else {
            Log.e(TAG, "Start Learning button (btn_start) not found in layout");
        }
    }

    /**
     * Navigate to Speaking Topics Fragment
     */
    private void navigateToSpeakingTopics() {
        try {
            Log.d(TAG, "Navigating to Speaking Topics");

            if (getActivity() == null) {
                Log.e(TAG, "Activity is null, cannot navigate");
                showMessage("Cannot open speaking topics");
                return;
            }

            SpeakingTopicsActivity topicsFragment = new SpeakingTopicsActivity();

            // Use Activity's FragmentManager for navigation
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // enter
                            R.anim.slide_out_left,  // exit
                            R.anim.slide_in_left,   // popEnter
                            R.anim.slide_out_right  // popExit
                    )
                    .replace(R.id.container, topicsFragment, "SpeakingTopicsActivity")
                    .addToBackStack("SpeakingToTopics")
                    .commit();

            Log.d(TAG, "Navigation to Speaking Topics committed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to speaking topics", e);
            showMessage("Failed to open speaking topics");
        }
    }

    /**
     * Show notification fragment
     */
    private void showNotificationFragment() {
        try {
            NotificationFragment fragment = new NotificationFragment();
            FragmentManager fragmentManager = getParentFragmentManager();
            fragment.show(fragmentManager, "notification_dialog");
            Log.d(TAG, "Notification dialog shown");
        } catch (Exception e) {
            Log.e(TAG, "Error showing notification", e);
            showMessage("Failed to show notifications");
        }
    }

    /**
     * Show info message to user
     */
    private void showMessage(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "SpeakingActivity destroyed");
        tabNavigationHelper = null;
    }
}
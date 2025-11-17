package com.example.englishapp;

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

import com.example.englishapp.Fragment.NotificationFragment;
import com.example.englishapp.utils.TopTabNavigationHelper;

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

        // Khởi tạo Tab Navigation Helper
        tabNavigationHelper = new TopTabNavigationHelper(
                view,
                requireContext(),
                getParentFragmentManager()
        );

        // Set tab hiện tại là Speaking
        tabNavigationHelper.setCurrentTab(TopTabNavigationHelper.TabType.SPEAKING);

        // Tìm button notification
        ImageView btnNotification = view.findViewById(R.id.notification_icon);
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> showNotificationFragment());
        } else {
            Log.e(TAG, "notification_icon not found in layout");
        }

        // Setup avatar click
        setupAvatarClick(view);

        // Setup Start Learning button
        setupStartLearningButton(view);
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
     * Setup Start Learning button
     */
    private void setupStartLearningButton(View view) {
        Button startButton = view.findViewById(R.id.button);
        if (startButton != null) {
            startButton.setOnClickListener(v -> {
                Log.d(TAG, "Start Learning button clicked");
                startSpeakingLesson();
            });
        } else {
            Log.e(TAG, "Start Learning button not found in layout");
        }
    }

    /**
     * Show notification fragment
     */
    private void showNotificationFragment() {
        NotificationFragment fragment = new NotificationFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        fragment.show(fragmentManager, "notification_dialog");
    }

    /**
     * Start speaking lesson
     */
    private void startSpeakingLesson() {
        // TODO: Implement navigation to speaking lesson/practice screen
        showMessage("Starting speaking lesson...");
        Log.d(TAG, "Speaking lesson started");
    }

    /**
     * Show info message to user
     */
    private void showMessage(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
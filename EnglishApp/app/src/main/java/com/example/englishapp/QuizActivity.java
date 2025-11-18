package com.example.englishapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.englishapp.Fragment.NotificationFragment;
import com.example.englishapp.utils.TopTabNavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private String quizType;
    private TopTabNavigationHelper tabHelper;
    private BottomNavigationView bottomNavigationView;
    private ImageView btnNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lấy quiz type từ Intent
        quizType = getIntent().getStringExtra("QUIZ_TYPE");
        if (quizType == null) {
            quizType = "Vocabulary"; // Default
        }

        Log.d(TAG, "onCreate with quiz type: " + quizType);

        // Load layout tương ứng với quiz type
        loadLayoutForQuizType();

        //Setup notification button
        setupNotificationButton();

        // Initialize tab navigation helper
        initTabNavigation();

        // Load quiz content
        loadQuizContent();

        // Setup bottom navigation
        setupBottomNavigation();
    }

    /**
     * Setup notification button - lấy logic từ HomeActivity
     */
    private void setupNotificationButton() {
        try {
            btnNotification = findViewById(R.id.btn_notification);
            if (btnNotification != null) {
                btnNotification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showNotificationFragment();
                    }
                });
                Log.d(TAG, "Notification button setup successfully");
            } else {
                Log.w(TAG, "Notification button not found in layout");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up notification button", e);
        }
    }

    /**
     * Show notification dialog - lấy từ HomeActivity
     */
    private void showNotificationFragment() {
        try {
            NotificationFragment fragment = new NotificationFragment();
            fragment.show(getSupportFragmentManager(), "notification_dialog");
            Log.d(TAG, "Notification fragment shown");
        } catch (Exception e) {
            Log.e(TAG, "Error showing notification", e);
            Toast.makeText(this, "Failed to show notifications", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Load layout tương ứng với quiz type
     */
    private void loadLayoutForQuizType() {
        switch (quizType) {
            case "Vocabulary":
                setContentView(R.layout.activity_quiz_layout);
                break;
            case "Listening":
                setContentView(R.layout.quiz_listening);
                break;
            default:
                setContentView(R.layout.activity_quiz_layout);
                break;
        }
    }

    /**
     * Initialize tab navigation using TopTabNavigationHelper
     */
    private void initTabNavigation() {
        try {
            // Create helper with constructor cho QuizActivity (không cần FragmentManager)
            tabHelper = new TopTabNavigationHelper(
                    findViewById(android.R.id.content),
                    this
            );

            // Setup cho quiz mode - chỉ update text, KHÔNG highlight tab
            tabHelper.setupForQuizMode(quizType);

            // Handle tab clicks (Vocabulary, Listening, Speaking)
            tabHelper.setOnTabSelectedListener(tabType -> {
                switch (tabType) {
                    case VOCABULARY:
                        // Navigate về HomeActivity với tab Vocabulary
                        navigateToHomeWithTab("VOCABULARY");
                        break;
                    case LISTENING:
                        // Navigate về HomeActivity với tab Listening
                        navigateToHomeWithTab("LISTENING");
                        break;
                    case SPEAKING:
                        // Navigate về HomeActivity với tab Speaking
                        navigateToHomeWithTab("SPEAKING");
                        break;
                }
            });

            // Handle quiz type selection from popup menu
            tabHelper.setOnQuizTypeSelectedListener(selectedQuizType -> {
                if (!selectedQuizType.equals(quizType)) {
                    // Reload quiz với type mới
                    switchQuizType(selectedQuizType);
                }
            });

            Log.d(TAG, "Tab navigation initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing tab navigation", e);
        }
    }

    /**
     * Navigate về HomeActivity với tab được chọn
     */
    private void navigateToHomeWithTab(String tabName) {
        try {
            Log.d(TAG, "Navigating to Home with tab: " + tabName);

            Intent intent = new Intent(QuizActivity.this, HomeActivity.class);
            intent.putExtra("SELECTED_TAB", tabName);

            // Clear task để tránh back stack issues
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            finish(); // Close QuizActivity
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to home", e);
            Toast.makeText(this, "Navigation failed", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Switch quiz type và reload layout
     */
    private void switchQuizType(String newQuizType) {
        try {
            Log.d(TAG, "Switching quiz type from " + quizType + " to " + newQuizType);

            quizType = newQuizType;

            // Reload everything
            loadLayoutForQuizType();
            setupNotificationButton(); // Setup lại notification button
            initTabNavigation();
            loadQuizContent();
            setupBottomNavigation();
        } catch (Exception e) {
            Log.e(TAG, "Error switching quiz type", e);
            Toast.makeText(this, "Failed to switch quiz type", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Load quiz content dựa trên type
     */
    private void loadQuizContent() {
        switch (quizType) {
            case "Vocabulary":
                loadVocabularyQuiz();
                break;
            case "Listening":
                loadListeningQuiz();
                break;
        }
    }

    /**
     * Load Vocabulary Quiz
     */
    private void loadVocabularyQuiz() {
        Toast.makeText(this, "Loading Vocabulary Quiz...", Toast.LENGTH_SHORT).show();
    }

    /**
     * Load Listening Quiz
     */
    private void loadListeningQuiz() {
        Toast.makeText(this, "Loading Listening Quiz...", Toast.LENGTH_SHORT).show();
    }

    /**
     * Setup bottom navigation với logic điều hướng - lấy từ HomeActivity
     */
    private void setupBottomNavigation() {
        try {
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            if (bottomNavigationView == null) {
                Log.w(TAG, "Bottom navigation view not found in layout");
                return;
            }

            // Không select item nào vì đang ở Quiz mode
            bottomNavigationView.setSelectedItemId(0);

            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    try {
                        int itemId = item.getItemId();

                        if (itemId == R.id.nav_home) {
                            // Navigate về Home
                            navigateToHome();
                        } else if (itemId == R.id.nav_lesson) {
                            // Navigate về Lesson (Vocabulary)
                            navigateToHomeWithTab("VOCABULARY");
                        } else if (itemId == R.id.nav_statistics) {
                            // Navigate về Statistics
                            navigateToHomeWithTab("STATISTICS");
                        } else if (itemId == R.id.nav_profile) {
                            // Navigate về Profile
                            navigateToHomeWithTab("PROFILE");
                        }

                        return true;
                    } catch (Exception e) {
                        Log.e(TAG, "Error handling bottom navigation", e);
                        Toast.makeText(QuizActivity.this, "Navigation failed", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            });

            Log.d(TAG, "Bottom navigation setup successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up bottom navigation", e);
        }
    }

    /**
     * Navigate về Home (không chọn tab cụ thể)
     */
    private void navigateToHome() {
        try {
            Intent intent = new Intent(QuizActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to home", e);
            Toast.makeText(this, "Failed to navigate", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cleanup
        if (tabHelper != null) {
            tabHelper.cleanup();
        }
    }

    @Override
    public void onBackPressed() {
        // Quay về HomeActivity
        navigateToHome();
    }
}
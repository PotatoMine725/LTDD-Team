package com.example.englishapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishapp.utils.TopTabNavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class QuizActivity extends AppCompatActivity {

    private String quizType;
    private TopTabNavigationHelper tabHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lấy quiz type từ Intent
        quizType = getIntent().getStringExtra("QUIZ_TYPE");
        if (quizType == null) {
            quizType = "Vocabulary"; // Default
        }

        // Load layout tương ứng với quiz type
        loadLayoutForQuizType();

        // Initialize tab navigation helper
        initTabNavigation();

        // Load quiz content
        loadQuizContent();

        // Setup bottom navigation
        setupBottomNavigation();
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
                    // Navigate về Vocabulary screen (không phải quiz)
                    navigateToMainScreen("VOCABULARY");
                    break;
                case LISTENING:
                    // Navigate về Listening screen (không phải quiz)
                    navigateToMainScreen("LISTENING");
                    break;
                case SPEAKING:
                    // Navigate về Speaking screen
                    navigateToMainScreen("SPEAKING");
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
    }

    /**
     * Navigate về main screen với tab tương ứng
     */
    private void navigateToMainScreen(String tabName) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("SELECTED_TAB", tabName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Switch quiz type và reload layout
     */
    private void switchQuizType(String newQuizType) {
        quizType = newQuizType;

        // Reload everything
        loadLayoutForQuizType();
        initTabNavigation();
        loadQuizContent();
        setupBottomNavigation();
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

        // TODO: Implement vocabulary quiz logic
        // - Load questions from database
        // - Setup image view with question image
        // - Setup answer buttons
        // - Handle answer selection
        // - Handle check and next buttons

        // Example:
        // ImageView quizImage = findViewById(R.id.iv_quiz_image);
        // Button btnAnswerA = findViewById(R.id.btn_answer_a);
        // etc.
    }

    /**
     * Load Listening Quiz
     */
    private void loadListeningQuiz() {
        Toast.makeText(this, "Loading Listening Quiz...", Toast.LENGTH_SHORT).show();

        // TODO: Implement listening quiz logic
        // - Load audio files
        // - Setup audio player controls (wave icon, reload icon)
        // - Setup answer input field
        // - Handle success/fail buttons
        // - Handle check and next buttons

        // Example:
        // ImageView waveIcon = findViewById(R.id.iv_wave);
        // ImageView reloadIcon = findViewById(R.id.iv_reload);
        // EditText answerInput = findViewById(R.id.editTextText3);
        // etc.
    }

    /**
     * Setup bottom navigation
     */
    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                // TODO: Handle bottom navigation clicks
                // Navigate to Home, Lesson, Profile, etc.
                return true;
            });
        }
    }
}
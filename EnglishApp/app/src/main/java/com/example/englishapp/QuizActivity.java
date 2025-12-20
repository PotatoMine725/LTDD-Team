package com.example.englishapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.englishapp.Fragment.NotificationFragment;
import com.example.englishapp.model.QuizQuestion; // Đảm bảo bạn đã tạo file này trong package model
import com.example.englishapp.utils.TopTabNavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private String quizType;
    private TopTabNavigationHelper tabHelper;
    private BottomNavigationView bottomNavigationView;
    private ImageView btnNotification;

    // --- BIẾN CHO VOCABULARY QUIZ ---
    private List<QuizQuestion> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0; // Biến tính điểm
    private int selectedAnswerIndex = -1; // -1 là chưa chọn

    // UI Elements cho Vocabulary
    private Button btnQuestion, btnAnswerA, btnAnswerB, btnAnswerC, btnAnswerD, btnCheck, btnNext;
    private TextView tvQuestionNumber;
    private Button[] answerButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Lấy loại Quiz từ Intent
        quizType = getIntent().getStringExtra("QUIZ_TYPE");
        if (quizType == null) {
            quizType = "Vocabulary"; // Mặc định
        }
        Log.d(TAG, "Starting QuizActivity with type: " + quizType);

        // 2. Load Layout dựa trên loại Quiz
        loadLayoutForQuizType();

        // 3. Setup các thành phần chung
        setupNotificationButton();
        initTabNavigation();
        setupBottomNavigation();

        // 4. Load dữ liệu
        loadQuizContent();
    }

    /**
     * Load layout XML tương ứng và ánh xạ View
     */
    private void loadLayoutForQuizType() {
        if ("Listening".equals(quizType)) {
            setContentView(R.layout.quiz_listening);
            // TODO: Ánh xạ view cho Listening nếu cần xử lý logic Listening
        } else {
            // Mặc định là Vocabulary
            setContentView(R.layout.activity_quiz_layout);
            initVocabularyViews();
        }
    }

    /**
     * Ánh xạ các View trong activity_quiz_layout.xml
     */
    private void initVocabularyViews() {
        tvQuestionNumber = findViewById(R.id.tv_question_number);
        btnQuestion = findViewById(R.id.btn_question);

        btnAnswerA = findViewById(R.id.btn_answer_a);
        btnAnswerB = findViewById(R.id.btn_answer_b);
        btnAnswerC = findViewById(R.id.btn_answer_c);
        btnAnswerD = findViewById(R.id.btn_answer_d);

        btnCheck = findViewById(R.id.btn_check);
        btnNext = findViewById(R.id.btn_next);

        // Gom các nút đáp án vào mảng để dễ xử lý vòng lặp
        answerButtons = new Button[]{btnAnswerA, btnAnswerB, btnAnswerC, btnAnswerD};
    }

    /**
     * Điều hướng load dữ liệu
     */
    private void loadQuizContent() {
        if ("Vocabulary".equals(quizType)) {
            loadVocabularyQuizFromFirebase();
        } else if ("Listening".equals(quizType)) {
            loadListeningQuiz();
        }
    }

    // ==========================================
    // LOGIC VOCABULARY QUIZ (FIREBASE)
    // ==========================================

    private void loadVocabularyQuizFromFirebase() {
        Toast.makeText(this, "Loading questions...", Toast.LENGTH_SHORT).show();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("quizzes").child("vocabulary_quiz");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questionList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        String id = snapshot.getKey();
                        String questionText = snapshot.child("question").getValue(String.class);

                        // Xử lý correct_index (Firebase có thể trả về Long hoặc Integer)
                        int correctIndex = 0;
                        if (snapshot.child("correct_index").exists()) {
                            correctIndex = snapshot.child("correct_index").getValue(Integer.class);
                        }

                        // Lấy danh sách options
                        GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                        List<String> options = snapshot.child("options").getValue(t);

                        if (questionText != null && options != null && options.size() >= 2) {
                            questionList.add(new QuizQuestion(id, questionText, options, correctIndex));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing question: " + e.getMessage());
                    }
                }

                if (!questionList.isEmpty()) {
                    currentQuestionIndex = 0;
                    score = 0;
                    displayQuestion(currentQuestionIndex);
                } else {
                    Toast.makeText(QuizActivity.this, "No questions found in Database!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuizActivity.this, "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayQuestion(int index) {
        if (questionList == null || index >= questionList.size()) {
            finishQuiz();
            return;
        }

        QuizQuestion currentQ = questionList.get(index);

        // Reset trạng thái UI
        selectedAnswerIndex = -1;
        btnCheck.setEnabled(false);
        btnCheck.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.darker_gray));
        btnNext.setEnabled(false);
        btnNext.setVisibility(View.INVISIBLE); // Ẩn nút Next cho đến khi Check xong
        btnCheck.setVisibility(View.VISIBLE);

        // Hiển thị nội dung
        tvQuestionNumber.setText("Question " + (index + 1) + "/" + questionList.size());
        btnQuestion.setText(currentQ.getQuestion());

        // Hiển thị các đáp án
        List<String> options = currentQ.getOptions();
        for (int i = 0; i < answerButtons.length; i++) {
            if (i < options.size()) {
                answerButtons[i].setVisibility(View.VISIBLE);
                answerButtons[i].setText(options.get(i));
                answerButtons[i].setEnabled(true);

                // Reset màu (giả sử màu mặc định là xanh nhạt hoặc trắng)
                answerButtons[i].setBackgroundColor(Color.parseColor("#81D4FA"));
                answerButtons[i].setTextColor(Color.WHITE);

                int finalI = i;
                answerButtons[i].setOnClickListener(v -> onAnswerSelected(finalI));
            } else {
                answerButtons[i].setVisibility(View.GONE);
            }
        }

        // Sự kiện nút Check
        btnCheck.setOnClickListener(v -> checkAnswer());

        // Sự kiện nút Next
        btnNext.setOnClickListener(v -> {
            currentQuestionIndex++;
            displayQuestion(currentQuestionIndex);
        });
    }

    private void onAnswerSelected(int index) {
        selectedAnswerIndex = index;

        // Highlight đáp án được chọn (Vàng), các đáp án khác về bình thường
        for (int i = 0; i < answerButtons.length; i++) {
            if (answerButtons[i].getVisibility() == View.VISIBLE) {
                if (i == index) {
                    answerButtons[i].setBackgroundColor(Color.parseColor("#FFEB3B")); // Vàng
                    answerButtons[i].setTextColor(Color.BLACK);
                } else {
                    answerButtons[i].setBackgroundColor(Color.parseColor("#81D4FA")); // Màu gốc
                    answerButtons[i].setTextColor(Color.WHITE);
                }
            }
        }

        // Enable nút Check
        btnCheck.setEnabled(true);
        btnCheck.setBackgroundColor(Color.parseColor("#8BC34A")); // Xanh lá kích hoạt
    }

    private void checkAnswer() {
        if (selectedAnswerIndex == -1) return;

        QuizQuestion currentQ = questionList.get(currentQuestionIndex);
        boolean isCorrect = (selectedAnswerIndex == currentQ.getCorrectIndex());

        if (isCorrect) {
            score++;
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            answerButtons[selectedAnswerIndex].setBackgroundColor(Color.GREEN);
        } else {
            Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
            answerButtons[selectedAnswerIndex].setBackgroundColor(Color.RED);
            // Hiện đáp án đúng
            if (currentQ.getCorrectIndex() < answerButtons.length) {
                answerButtons[currentQ.getCorrectIndex()].setBackgroundColor(Color.GREEN);
            }
        }

        // Khóa tất cả nút đáp án
        for (Button btn : answerButtons) {
            btn.setEnabled(false);
        }

        // Đổi nút Check thành Next (hoặc ẩn Check hiện Next)
        btnCheck.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);
        btnNext.setEnabled(true);
    }

    private void finishQuiz() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quiz Completed!");
        builder.setMessage("Your score: " + score + " / " + questionList.size());
        builder.setCancelable(false);
        builder.setPositiveButton("Back to Home", (dialog, which) -> {
            navigateToHome();
        });
        builder.setNegativeButton("Retry", (dialog, which) -> {
            // Reset và làm lại
            currentQuestionIndex = 0;
            score = 0;
            displayQuestion(0);
        });
        builder.show();
    }

    // ==========================================
    // LOGIC LISTENING QUIZ (PLACEHOLDER)
    // ==========================================

    private void loadListeningQuiz() {
        // Hiện tại layout quiz_listening.xml là dạng điền từ (EditText)
        // nhưng JSON lại là trắc nghiệm (options).
        // Bạn cần thống nhất lại dữ liệu hoặc layout.
        Toast.makeText(this, "Listening Quiz feature is coming soon!", Toast.LENGTH_SHORT).show();
    }

    // ==========================================
    // NAVIGATION & COMMON SETUP
    // ==========================================

    private void setupNotificationButton() {
        try {
            btnNotification = findViewById(R.id.btn_notification);
            if (btnNotification != null) {
                btnNotification.setOnClickListener(v -> showNotificationFragment());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting notification btn", e);
        }
    }

    private void showNotificationFragment() {
        NotificationFragment fragment = new NotificationFragment();
        fragment.show(getSupportFragmentManager(), "notification_dialog");
    }

    private void initTabNavigation() {
        try {
            tabHelper = new TopTabNavigationHelper(findViewById(android.R.id.content), this);
            tabHelper.setupForQuizMode(quizType);

            tabHelper.setOnTabSelectedListener(tabType -> {
                switch (tabType) {
                    case VOCABULARY: navigateToHomeWithTab("VOCABULARY"); break;
                    case LISTENING: navigateToHomeWithTab("LISTENING"); break;
                    case SPEAKING: navigateToHomeWithTab("SPEAKING"); break;
                }
            });

            tabHelper.setOnQuizTypeSelectedListener(selectedQuizType -> {
                if (!selectedQuizType.equals(quizType)) {
                    switchQuizType(selectedQuizType);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error init tab nav", e);
        }
    }

    private void switchQuizType(String newQuizType) {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("QUIZ_TYPE", newQuizType);
        startActivity(intent);
        finish(); // Đóng activity cũ để mở cái mới với layout mới
    }

    private void setupBottomNavigation() {
        try {
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            if (bottomNavigationView == null) return;

            bottomNavigationView.setSelectedItemId(0); // Không chọn gì hoặc chọn Home giả
            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.nav_home) navigateToHome();
                    else if (itemId == R.id.nav_lesson) navigateToHomeWithTab("VOCABULARY");
                    else if (itemId == R.id.nav_statistics) navigateToHomeWithTab("STATISTICS");
                    else if (itemId == R.id.nav_profile) navigateToHomeWithTab("PROFILE");
                    return true;
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error setting bottom nav", e);
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(QuizActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void navigateToHomeWithTab(String tabName) {
        Intent intent = new Intent(QuizActivity.this, HomeActivity.class);
        intent.putExtra("SELECTED_TAB", tabName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tabHelper != null) tabHelper.cleanup();
    }
}
package com.example.englishapp.ui.quiz;
import com.example.englishapp.HomeActivity;
import com.example.englishapp.R;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.englishapp.model.QuizQuestion;
// Lưu ý: Nếu QuizResultActivity nằm ở package khác, hãy import đúng, ví dụ:
// import com.example.englishapp.ui.quiz.QuizResultActivity;
// Nếu chưa có file đó ở package ui.quiz thì tạm thời comment dòng Intent hoặc sửa đường dẫn

import com.example.englishapp.ui.common.TopTabNavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

    // --- BIẾN CHO VOCABULARY QUIZ ---
    private List<QuizQuestion> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int selectedAnswerIndex = -1;

    // UI Elements
    private TextView tvQuestionNumber;
    private ImageView ivQuizImage;
    private Button btnQuestion;
    private Button btnAnswerA, btnAnswerB, btnAnswerC, btnAnswerD;
    private Button btnCheck, btnNext;

    private Button[] answerButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Lấy loại Quiz
        quizType = getIntent().getStringExtra("QUIZ_TYPE");
        if (quizType == null) quizType = "Vocabulary";

        // 2. Load Layout
        loadLayoutForQuizType();

        // 3. Setup các thành phần chung
        initTabNavigation();
        setupBottomNavigation();

        // 4. Load dữ liệu
        loadQuizContent();
    }

    private void loadLayoutForQuizType() {
        if ("Listening".equals(quizType)) {
            setContentView(R.layout.quiz_listening);
        } else {
            setContentView(R.layout.activity_quiz_layout);
            initVocabularyViews();
        }
    }

    private void initVocabularyViews() {
        tvQuestionNumber = findViewById(R.id.tv_question_number);
        ivQuizImage = findViewById(R.id.iv_quiz_image); // Đảm bảo ID này có trong XML
        btnQuestion = findViewById(R.id.btn_question);

        btnAnswerA = findViewById(R.id.btn_answer_a);
        btnAnswerB = findViewById(R.id.btn_answer_b);
        btnAnswerC = findViewById(R.id.btn_answer_c);
        btnAnswerD = findViewById(R.id.btn_answer_d);

        btnCheck = findViewById(R.id.btn_check);
        btnNext = findViewById(R.id.btn_next);

        answerButtons = new Button[]{btnAnswerA, btnAnswerB, btnAnswerC, btnAnswerD};
    }

    private void loadQuizContent() {
        if ("Vocabulary".equals(quizType)) {
            loadVocabularyQuizFromFirebase();
        } else if ("Listening".equals(quizType)) {
            loadListeningQuiz();
        }
    }

    // ==========================================
    // LOGIC VOCABULARY QUIZ (SỬA LỖI TẠO OBJECT)
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
                        String imgUrl = snapshot.child("image_url").getValue(String.class);

                        int correctIndex = 0;
                        if (snapshot.child("correct_index").exists()) {
                            correctIndex = snapshot.child("correct_index").getValue(Integer.class);
                        }

                        GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                        List<String> options = snapshot.child("options").getValue(t);

                        if (questionText != null && options != null && options.size() >= 2) {
                            // --- SỬA CHÍNH: Gọi Constructor khớp với QuizQuestion.java ---
                            QuizQuestion q = new QuizQuestion(id, questionText, options, correctIndex);
                            q.setImageUrl(imgUrl); // Set ảnh riêng
                            questionList.add(q);
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
                    Toast.makeText(QuizActivity.this, "No data found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuizActivity.this, "Failed to load: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayQuestion(int index) {
        if (questionList == null || index >= questionList.size()) {
            finishQuiz();
            return;
        }

        QuizQuestion currentQ = questionList.get(index);

        // Reset UI
        selectedAnswerIndex = -1;
        btnCheck.setEnabled(false);
        btnCheck.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.darker_gray));
        btnNext.setEnabled(false);
        btnNext.setVisibility(View.INVISIBLE);
        btnCheck.setVisibility(View.VISIBLE);

        tvQuestionNumber.setText("Question " + (index + 1) + "/" + questionList.size());
        btnQuestion.setText(currentQ.getQuestion());

        // Load ảnh với Glide
        if (currentQ.getImageUrl() != null && !currentQ.getImageUrl().isEmpty()) {
            ivQuizImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(currentQ.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.background)
                    .into(ivQuizImage);
        } else {
            ivQuizImage.setVisibility(View.GONE);
        }

        // Hiển thị đáp án
        List<String> options = currentQ.getOptions();
        for (int i = 0; i < answerButtons.length; i++) {
            if (i < options.size()) {
                answerButtons[i].setVisibility(View.VISIBLE);
                answerButtons[i].setText(options.get(i));
                answerButtons[i].setEnabled(true);

                // Reset màu
                answerButtons[i].setBackgroundColor(Color.parseColor("#81D4FA"));
                answerButtons[i].setTextColor(Color.WHITE);

                int finalI = i;
                answerButtons[i].setOnClickListener(v -> onAnswerSelected(finalI));
            } else {
                answerButtons[i].setVisibility(View.GONE);
            }
        }

        btnCheck.setOnClickListener(v -> checkAnswer());

        btnNext.setOnClickListener(v -> {
            currentQuestionIndex++;
            displayQuestion(currentQuestionIndex);
        });
    }

    private void onAnswerSelected(int index) {
        selectedAnswerIndex = index;

        for (int i = 0; i < answerButtons.length; i++) {
            if (answerButtons[i].getVisibility() == View.VISIBLE) {
                if (i == index) {
                    answerButtons[i].setBackgroundColor(Color.parseColor("#FFEB3B"));
                    answerButtons[i].setTextColor(Color.BLACK);
                } else {
                    answerButtons[i].setBackgroundColor(Color.parseColor("#81D4FA"));
                    answerButtons[i].setTextColor(Color.WHITE);
                }
            }
        }

        btnCheck.setEnabled(true);
        btnCheck.setBackgroundColor(Color.parseColor("#8BC34A"));
    }

    private void checkAnswer() {
        if (selectedAnswerIndex == -1) return;

        QuizQuestion currentQ = questionList.get(currentQuestionIndex);
        currentQ.setUserSelectedIndex(selectedAnswerIndex);

        boolean isCorrect = (selectedAnswerIndex == currentQ.getCorrectIndex());
        if (isCorrect) {
            score++;
//            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            answerButtons[selectedAnswerIndex].setBackgroundColor(Color.GREEN);
        } else {
//            Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
            answerButtons[selectedAnswerIndex].setBackgroundColor(Color.RED);

            if (currentQ.getCorrectIndex() < answerButtons.length) {
                answerButtons[currentQ.getCorrectIndex()].setBackgroundColor(Color.GREEN);
            }
        }

        for (Button btn : answerButtons) btn.setEnabled(false);

        btnCheck.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);
        btnNext.setEnabled(true);
    }

    //chuyen sang man hinh ket qua
    private void finishQuiz() {
        // Tạo Intent chuyển sang màn hình kết quả
        Intent intent = new Intent(QuizActivity.this, QuizResultActivity.class);

        // Truyền điểm số và tổng số câu
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL", questionList.size());

        // Truyền danh sách câu hỏi (đã kèm đáp án user chọn) để hiển thị lại
        // Ép kiểu về Serializable vì ArrayList mặc định đã hỗ trợ
        intent.putExtra("RESULT_LIST", (java.io.Serializable) questionList);

        // Bắt đầu Activity mới và đóng QuizActivity hiện tại
        startActivity(intent);
        finish();
    }

    private void loadListeningQuiz() {
        Toast.makeText(this, "Listening Quiz coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void setupNotificationButton() {
        // Code cũ giữ nguyên hoặc thêm nếu cần
    }

    private void initTabNavigation() {
        tabHelper = new TopTabNavigationHelper(findViewById(android.R.id.content), this);
        tabHelper.setupForQuizMode(quizType);

        tabHelper.setOnTabSelectedListener(tabType -> {
            switch (tabType) {
                case VOCABULARY: navigateToHomeWithTab("VOCABULARY"); break;
                case LISTENING: navigateToHomeWithTab("LISTENING"); break;
                case SPEAKING: navigateToHomeWithTab("SPEAKING"); break;
            }
        });

        tabHelper.setOnQuizTypeSelectedListener(type -> {
            if (!type.equals(quizType)) {
                Intent intent = new Intent(this, QuizActivity.class);
                intent.putExtra("QUIZ_TYPE", type);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView == null) return;

        bottomNavigationView.setSelectedItemId(0);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) navigateToHome();
            else if (id == R.id.nav_lesson) navigateToHomeWithTab("VOCABULARY");
            else if (id == R.id.nav_statistics) navigateToHomeWithTab("STATISTICS");
            else if (id == R.id.nav_profile) navigateToHomeWithTab("PROFILE");
            return true;
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void navigateToHomeWithTab(String tabName) {
        Intent intent = new Intent(this, HomeActivity.class);
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
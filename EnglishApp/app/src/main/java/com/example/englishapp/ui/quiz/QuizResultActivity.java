package com.example.englishapp.ui.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.ui.home.HomeActivity;
import com.example.englishapp.R;
import com.example.englishapp.adapter.QuizResultAdapter;
import com.example.englishapp.model.QuizQuestion;

import java.util.List;

public class QuizResultActivity extends AppCompatActivity {

    private static final String TAG = "QuizResultActivity";
    private TextView tvTotalScore, tvScorePercentage;
    private Button btnRetry, btnHome;
    private RecyclerView recyclerView;
    private QuizResultAdapter adapter;
    private String quizType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        // 1. Ánh xạ View (Khớp với ID trong XML activity_quiz_result.xml)
        tvTotalScore = findViewById(R.id.tv_total_score);
        tvScorePercentage = findViewById(R.id.tv_score_percentage);
        btnRetry = findViewById(R.id.btn_retry);
        btnHome = findViewById(R.id.btn_home);
        recyclerView = findViewById(R.id.rv_quiz_review);

        // 2. Nhận dữ liệu từ Intent
        int score = getIntent().getIntExtra("SCORE", 0);
        int total = getIntent().getIntExtra("TOTAL", 0);
        quizType = getIntent().getStringExtra("QUIZ_TYPE");
        if (quizType == null) quizType = "Vocabulary";

        List<QuizQuestion> resultList = (List<QuizQuestion>) getIntent().getSerializableExtra("RESULT_LIST");

        // 3. Hiển thị điểm số
        if (tvTotalScore != null) {
            tvTotalScore.setText(score + " / " + total);
        }

        if (tvScorePercentage != null) {
            int percentage = (total > 0) ? (int) ((score / (float) total) * 100) : 0;
            tvScorePercentage.setText("Score: " + percentage + "%");
        }

        // 4. Setup RecyclerView
        if (resultList != null && recyclerView != null) {
            // --- SỬA LỖI TẠI ĐÂY: Chỉ truyền resultList, không truyền 'this' ---
            adapter = new QuizResultAdapter(resultList);
            // -------------------------------------------------------------------

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        } else {
            Log.e(TAG, "Lỗi: resultList hoặc recyclerView bị null");
        }

        // 5. Sự kiện Click
        if (btnRetry != null) {
            btnRetry.setOnClickListener(v -> {
                Intent intent = new Intent(QuizResultActivity.this, QuizActivity.class);
                intent.putExtra("QUIZ_TYPE", quizType);
                startActivity(intent);
                finish();
            });
        }

        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                Intent intent = new Intent(QuizResultActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
}
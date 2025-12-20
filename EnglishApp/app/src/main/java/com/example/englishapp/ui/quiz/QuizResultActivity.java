package com.example.englishapp.ui.quiz; // Hoặc package com.example.englishapp nếu bạn để ở ngoài


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.ui.home.HomeActivity;
import com.example.englishapp.R;
import com.example.englishapp.adapter.QuizResultAdapter;
import com.example.englishapp.model.QuizQuestion;

import java.util.ArrayList;
import java.util.List;

public class QuizResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        // 1. Nhận dữ liệu từ QuizActivity
        List<QuizQuestion> resultList = (List<QuizQuestion>) getIntent().getSerializableExtra("RESULT_LIST");
        int score = getIntent().getIntExtra("SCORE", 0);
        int total = getIntent().getIntExtra("TOTAL", 0);

        if (resultList == null) resultList = new ArrayList<>();

        // 2. Hiển thị điểm số
        TextView tvScore = findViewById(R.id.tv_total_score);
        TextView tvPercent = findViewById(R.id.tv_score_percentage);

        tvScore.setText(score + " / " + total);
        int percent = (total > 0) ? (score * 100 / total) : 0;
        tvPercent.setText("Score: " + percent + "%");

        // 3. Setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_quiz_review);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        QuizResultAdapter adapter = new QuizResultAdapter(resultList);
        recyclerView.setAdapter(adapter);

        // 4. Xử lý nút bấm
        Button btnHome = findViewById(R.id.btn_home);
        Button btnRetry = findViewById(R.id.btn_retry);

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(QuizResultActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnRetry.setOnClickListener(v -> {
            // Quay lại màn hình QuizActivity mới
            Intent intent = new Intent(QuizResultActivity.this, QuizActivity.class);
            // Có thể cần putExtra QUIZ_TYPE nếu cần
            intent.putExtra("QUIZ_TYPE", "Vocabulary");
            startActivity(intent);
            finish();
        });
    }
}
package com.example.englishapp.ui.quiz;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.englishapp.R;
import com.example.englishapp.model.QuizQuestion;
import com.example.englishapp.ui.common.NotificationFragment;
import com.example.englishapp.ui.common.TopTabNavigationHelper;
import com.example.englishapp.ui.home.HomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private String quizType;
    private TopTabNavigationHelper tabHelper;
    private BottomNavigationView bottomNavigationView;
    private ImageView btnNotification;

    // --- DỮ LIỆU ---
    private List<QuizQuestion> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;

    // --- UI COMMON (Dùng chung cho cả Vocab và Listening) ---
    private TextView tvQuestionNumber;
    private Button btnQuestionText; // Tiêu đề câu hỏi (Vocab dùng Button, Listening dùng TextView trong layout cũ nhưng giờ ánh xạ chung cũng được)
    private TextView tvListeningQuestionText; // Dành riêng cho Listening text
    private Button btnAnswerA, btnAnswerB, btnAnswerC, btnAnswerD;
    private Button[] answerButtons;
    private int selectedAnswerIndex = -1;
    private Button btnCheck, btnNext;

    // --- UI: VOCABULARY ONLY ---
    private ImageView ivQuizImage;

    // --- UI: LISTENING ONLY ---
    private ImageButton btnPlayPause;
    private SeekBar seekBar;
    private TextView tvCurrentTime, tvTotalTime;
    private MediaPlayer mediaPlayer;
    private Handler audioHandler = new Handler();
    private boolean isAudioPrepared = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Xác định loại Quiz
        quizType = getIntent().getStringExtra("QUIZ_TYPE");
        if (quizType == null) quizType = "Vocabulary";

        // 2. Load Layout tương ứng
        if ("Listening".equals(quizType)) {
            setContentView(R.layout.quiz_listening);
            initListeningViews();
        } else {
            setContentView(R.layout.activity_quiz_layout);
            initVocabularyViews();
        }

        // 3. Setup thành phần chung
        setupNotificationButton();
        initTabNavigation();
        setupBottomNavigation();

        // 4. Load dữ liệu từ Firebase
        if ("Listening".equals(quizType)) {
            loadListeningQuizFromFirebase();
        } else {
            loadVocabularyQuizFromFirebase();
        }
    }

    // ==========================================
    // KHỞI TẠO VIEW
    // ==========================================

    private void initVocabularyViews() {
        tvQuestionNumber = findViewById(R.id.tv_question_number);
        ivQuizImage = findViewById(R.id.iv_quiz_image);
        btnQuestionText = findViewById(R.id.btn_question); // Button làm text câu hỏi

        btnAnswerA = findViewById(R.id.btn_answer_a);
        btnAnswerB = findViewById(R.id.btn_answer_b);
        btnAnswerC = findViewById(R.id.btn_answer_c);
        btnAnswerD = findViewById(R.id.btn_answer_d);
        answerButtons = new Button[]{btnAnswerA, btnAnswerB, btnAnswerC, btnAnswerD};

        btnCheck = findViewById(R.id.btn_check);
        btnNext = findViewById(R.id.btn_next);

        setupAnswerButtons();
        btnCheck.setOnClickListener(v -> checkAnswer());
        btnNext.setOnClickListener(v -> nextQuestion());
    }

    private void initListeningViews() {
        tvQuestionNumber = findViewById(R.id.tv_question_number);
        tvListeningQuestionText = findViewById(R.id.tv_question_text); // Listening dùng TextView

        btnPlayPause = findViewById(R.id.imgBtn_play_pause);
        seekBar = findViewById(R.id.seekBar);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvTotalTime = findViewById(R.id.tv_total_time);

        // Ánh xạ 4 nút đáp án (Giờ Listening cũng có 4 nút này)
        btnAnswerA = findViewById(R.id.btn_answer_a);
        btnAnswerB = findViewById(R.id.btn_answer_b);
        btnAnswerC = findViewById(R.id.btn_answer_c);
        btnAnswerD = findViewById(R.id.btn_answer_d);
        answerButtons = new Button[]{btnAnswerA, btnAnswerB, btnAnswerC, btnAnswerD};

        btnCheck = findViewById(R.id.btn_check);
        btnNext = findViewById(R.id.btn_next);

        // Setup Audio Player
        btnPlayPause.setOnClickListener(v -> toggleAudio());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) mediaPlayer.seekTo(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        setupAnswerButtons();
        btnCheck.setOnClickListener(v -> checkAnswer()); // Dùng chung hàm checkAnswer
        btnNext.setOnClickListener(v -> nextQuestion());
    }

    // Thiết lập sự kiện click cho các nút đáp án (Dùng chung)
    private void setupAnswerButtons() {
        for (int i = 0; i < answerButtons.length; i++) {
            int finalI = i;
            answerButtons[i].setOnClickListener(v -> onAnswerSelected(finalI));
        }
    }

    // ==========================================
    // LOGIC LOAD DỮ LIỆU
    // ==========================================

    private void loadVocabularyQuizFromFirebase() { loadDataFromPath("vocabulary_quiz"); }
    private void loadListeningQuizFromFirebase() { loadDataFromPath("listening_quiz"); }

    private void loadDataFromPath(String path) {
        Toast.makeText(this, "Loading data...", Toast.LENGTH_SHORT).show();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("quizzes").child(path);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questionList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        String id = snapshot.getKey();
                        String questionText = snapshot.child("question").getValue(String.class);
                        String imgUrl = snapshot.child("image_url").getValue(String.class);
                        String audioUrl = snapshot.child("audio_url").getValue(String.class);

                        int correctIndex = 0;
                        if (snapshot.child("correct_index").exists())
                            correctIndex = snapshot.child("correct_index").getValue(Integer.class);

                        GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                        List<String> options = snapshot.child("options").getValue(t);

                        if (questionText != null && options != null) {
                            QuizQuestion q = new QuizQuestion(id, questionText, options, correctIndex);
                            q.setImageUrl(imgUrl);
                            q.setAudioUrl(audioUrl);
                            questionList.add(q);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing question: " + e.getMessage());
                    }
                }

                if (!questionList.isEmpty()) {
                    currentQuestionIndex = 0;
                    score = 0;
                    if ("Listening".equals(quizType)) displayListeningQuestion(0);
                    else displayVocabQuestion(0);
                } else {
                    Toast.makeText(QuizActivity.this, "No data found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // ==========================================
    // HIỂN THỊ CÂU HỎI
    // ==========================================

    private void displayVocabQuestion(int index) {
        if (index >= questionList.size()) { finishQuiz(); return; }
        QuizQuestion currentQ = questionList.get(index);

        resetCommonUI();
        tvQuestionNumber.setText("Question " + (index + 1) + "/" + questionList.size());
        btnQuestionText.setText(currentQ.getQuestion());

        if (currentQ.getImageUrl() != null && !currentQ.getImageUrl().isEmpty()) {
            ivQuizImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(currentQ.getImageUrl()).into(ivQuizImage);
        } else {
            ivQuizImage.setVisibility(View.GONE);
        }

        bindOptionsToButtons(currentQ.getOptions());
    }

    private void displayListeningQuestion(int index) {
        if (index >= questionList.size()) { finishQuiz(); return; }
        QuizQuestion currentQ = questionList.get(index);

        resetCommonUI();
        tvQuestionNumber.setText("Question " + (index + 1) + "/" + questionList.size());
        tvListeningQuestionText.setText(currentQ.getQuestion());

        // Chuẩn bị audio
        prepareAudio(currentQ.getAudioUrl());

        // Hiển thị 4 đáp án lên nút
        bindOptionsToButtons(currentQ.getOptions());
    }

    private void bindOptionsToButtons(List<String> options) {
        for (int i = 0; i < answerButtons.length; i++) {
            if (i < options.size()) {
                answerButtons[i].setVisibility(View.VISIBLE);
                answerButtons[i].setText(options.get(i));
                answerButtons[i].setEnabled(true);
                answerButtons[i].setBackgroundColor(Color.parseColor("#81D4FA")); // Xanh nhạt
                answerButtons[i].setTextColor(Color.WHITE);
            } else {
                answerButtons[i].setVisibility(View.GONE);
            }
        }
    }

    private void resetCommonUI() {
        btnCheck.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.INVISIBLE);
        btnNext.setEnabled(false);
        selectedAnswerIndex = -1;
        btnCheck.setEnabled(false);
        btnCheck.setBackgroundColor(Color.GRAY);
    }

    // ==========================================
    // TRÌNH PHÁT NHẠC (ROBUST VERSION)
    // ==========================================

    private void prepareAudio(String rawUrl) {
        releaseMediaPlayer();
        isAudioPrepared = false;
        btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
        seekBar.setProgress(0);
        tvCurrentTime.setText("00:00");
        tvTotalTime.setText("00:00");
        btnPlayPause.setEnabled(false);

        if (rawUrl == null || rawUrl.isEmpty()) {
            Toast.makeText(this, "Audio URL is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String finalUrl = rawUrl.replace("\"", "").trim();
        Log.d(TAG, "Downloading audio: [" + finalUrl + "]");
        Toast.makeText(this, "Downloading audio...", Toast.LENGTH_SHORT).show();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                File cacheDir = getCacheDir();
                String fileName = "audio_" + finalUrl.hashCode() + ".mp3";
                File tempFile = new File(cacheDir, fileName);

                if (!tempFile.exists()) {
                    downloadFile(finalUrl, tempFile);
                }
                runOnUiThread(() -> playLocalAudio(tempFile.getAbsolutePath()));

            } catch (Exception e) {
                Log.e(TAG, "Download Error", e);
                runOnUiThread(() -> {
                    Toast.makeText(QuizActivity.this, "Error loading audio", Toast.LENGTH_SHORT).show();
                    btnPlayPause.setEnabled(true);
                });
            }
        });
    }

    private void downloadFile(String stringUrl, File outputFile) throws IOException {
        URL url = new URL(stringUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Android)");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setInstanceFollowRedirects(false);
        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == 307 || responseCode == 308) {
            String newUrl = connection.getHeaderField("Location");
            downloadFile(newUrl, outputFile);
            return;
        }
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Server returned code: " + responseCode);
        }

        InputStream inputStream = connection.getInputStream();
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        byte[] buffer = new byte[4096];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, len);
        }
        fileOutputStream.close();
        inputStream.close();
    }

    private void playLocalAudio(String path) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
            mediaPlayer.setDataSource(path);
            mediaPlayer.setOnPreparedListener(mp -> {
                isAudioPrepared = true;
                tvTotalTime.setText(formatTime(mp.getDuration()));
                seekBar.setMax(mp.getDuration());
                btnPlayPause.setEnabled(true);
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e(TAG, "Local Play Exception", e);
        }
    }

    private void toggleAudio() {
        if (mediaPlayer == null || !isAudioPrepared) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
            audioHandler.removeCallbacks(updateSeekBar);
        } else {
            mediaPlayer.start();
            btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
            audioHandler.post(updateSeekBar);
        }
    }

    private Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                tvCurrentTime.setText(formatTime(mediaPlayer.getCurrentPosition()));
                audioHandler.postDelayed(this, 1000);
            }
        }
    };

    private String formatTime(int millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            audioHandler.removeCallbacks(updateSeekBar);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // ==========================================
    // CHECK KẾT QUẢ & NAVIGATION
    // ==========================================

    private void onAnswerSelected(int index) {
        selectedAnswerIndex = index;
        for (int i = 0; i < answerButtons.length; i++) {
            if (i == index) {
                answerButtons[i].setBackgroundColor(Color.parseColor("#FFEB3B")); // Vàng khi chọn
                answerButtons[i].setTextColor(Color.BLACK);
            } else {
                answerButtons[i].setBackgroundColor(Color.parseColor("#81D4FA")); // Reset màu
                answerButtons[i].setTextColor(Color.WHITE);
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
            answerButtons[selectedAnswerIndex].setBackgroundColor(Color.GREEN);
        } else {
            answerButtons[selectedAnswerIndex].setBackgroundColor(Color.RED);
            if (currentQ.getCorrectIndex() < answerButtons.length) {
                answerButtons[currentQ.getCorrectIndex()].setBackgroundColor(Color.GREEN);
            }
        }

        // Disable buttons
        for (Button btn : answerButtons) btn.setEnabled(false);

        btnCheck.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);
        btnNext.setEnabled(true);
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        if ("Listening".equals(quizType)) displayListeningQuestion(currentQuestionIndex);
        else displayVocabQuestion(currentQuestionIndex);
    }

    private void finishQuiz() {
        releaseMediaPlayer();
        Intent intent = new Intent(QuizActivity.this, QuizResultActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL", questionList.size());
        intent.putExtra("RESULT_LIST", (java.io.Serializable) questionList);
        startActivity(intent);
        finish();
    }

    // ==========================================
    // NAVIGATION & LIFECYCLE (Giữ nguyên)
    // ==========================================

    private void setupNotificationButton() {
        btnNotification = findViewById(R.id.btn_notification);
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                NotificationFragment fragment = new NotificationFragment();
                fragment.show(getSupportFragmentManager(), "notification_dialog");
            });
        }
    }

    private void initTabNavigation() {
        tabHelper = new TopTabNavigationHelper(findViewById(android.R.id.content), this);
        tabHelper.setupForQuizMode(quizType);

        tabHelper.setOnTabSelectedListener(tabType -> {
            releaseMediaPlayer();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("SELECTED_TAB", tabType.toString());
            startActivity(intent);
            finish();
        });

        tabHelper.setOnQuizTypeSelectedListener(type -> {
            if (!type.equals(quizType)) {
                releaseMediaPlayer();
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
            releaseMediaPlayer();
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
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        if (tabHelper != null) tabHelper.cleanup();
    }
}
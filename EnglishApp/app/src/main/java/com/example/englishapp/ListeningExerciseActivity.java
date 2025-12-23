package com.example.englishapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide; // Cần thêm thư viện này
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListeningExerciseActivity extends Fragment {

    private static final String TAG = "ListeningExercise";
    private static final String ARG_TOPIC_ID = "topic_id";
    private static final String ARG_LESSON_ID = "lesson_id";

    private String topicId, lessonId;
    private DatabaseReference mDatabase;

    private TextView tvQuestion1, tvQuestion2, tvTitle;
    private RadioGroup radioGroup1, radioGroup2;
    private Button submitButton, replayButton;
    private ImageView ivTopic; // Thêm ImageView để hiện ảnh
    private List<Integer> correctAnswers = new ArrayList<>();

    private MediaPlayer mediaPlayer;
    private String audioUrlFromFirebase; // Biến lưu link nhạc

    public static ListeningExerciseActivity newInstance(String topicId, String lessonId) {
        ListeningExerciseActivity fragment = new ListeningExerciseActivity();
        Bundle args = new Bundle();
        args.putString(ARG_TOPIC_ID, topicId);
        args.putString(ARG_LESSON_ID, lessonId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topicId = getArguments().getString(ARG_TOPIC_ID);
            lessonId = getArguments().getString(ARG_LESSON_ID);
        }
        mDatabase = FirebaseDatabase.getInstance("https://englishappdb-db02d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.listening_excercise, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvQuestion1 = view.findViewById(R.id.question_1);
        tvQuestion2 = view.findViewById(R.id.question_2);
        radioGroup1 = view.findViewById(R.id.radio_group_1);
        radioGroup2 = view.findViewById(R.id.radio_group_2);
        submitButton = view.findViewById(R.id.submit_button);
        replayButton = view.findViewById(R.id.replay_button);
        ivTopic = view.findViewById(R.id.now_playing_image); // Ánh xạ ImageView ảnh bài học
        tvTitle = view.findViewById(R.id.textView2);  // Ánh xạ tiêu đề bài học

        ImageView backIcon = view.findViewById(R.id.back_icon);
        ImageView playPauseButton = view.findViewById(R.id.play_pause_button);
        ImageView stopButton = view.findViewById(R.id.stop_button);

        loadDataFromFirebase();

        submitButton.setOnClickListener(v -> checkAnswers());
        backIcon.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Gắn logic xử lý MediaPlayer vào các nút
        playPauseButton.setOnClickListener(v -> {
            if (audioUrlFromFirebase != null) playAudio(audioUrlFromFirebase);
        });

        stopButton.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        });

        replayButton.setOnClickListener(v -> {
            if (audioUrlFromFirebase != null) playAudio(audioUrlFromFirebase);
        });
    }

    private void loadDataFromFirebase() {
//        if (topicId == null || lessonId == null) {
//            Log.e("CHECK_DATA", "LỖI: Một trong hai ID bị null!");
//            return;
//        }
        DatabaseReference lessonRef = FirebaseDatabase.getInstance("https://englishappdb-db02d-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("topics")
                .child("listening")
                .child(topicId)
                .child("lessons")
                .child(lessonId);

        lessonRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("DEBUG", "Dữ liệu đã về!");
                    // 1. Load tiêu đề và ảnh
                    String title = snapshot.child("title").getValue(String.class);
                    String imageUrl = snapshot.child("image_url").getValue(String.class);
                    audioUrlFromFirebase = snapshot.child("audio_url").getValue(String.class);

                    if (tvTitle != null) tvTitle.setText(title);

                    // Load ảnh bằng Glide (Phải đúng ID view là now_playing_image)
                    if (imageUrl != null && isAdded()) {
                        Glide.with(ListeningExerciseActivity.this).load(imageUrl).into(ivTopic);
                    }

                    // 2. Load danh sách câu hỏi
                    DataSnapshot questionsSnapshot = snapshot.child("questions");
                    correctAnswers.clear();
                    int count = 0;

                    for (DataSnapshot qSnap : questionsSnapshot.getChildren()) {
                        String qText = qSnap.child("question_text").getValue(String.class);
                        Integer correctIdx = qSnap.child("correct_index").getValue(Integer.class);

                        // Lấy danh sách options thủ công để tránh lỗi ép kiểu
                        List<String> options = new ArrayList<>();
                        for (DataSnapshot opt : qSnap.child("options").getChildren()) {
                            options.add(opt.getValue(String.class));
                        }

                        if (correctIdx != null) correctAnswers.add(correctIdx);

                        if (count == 0 && tvQuestion1 != null) {
                            tvQuestion1.setText(qText);
                            fillOptions(radioGroup1, options);
                        } else if (count == 1 && tvQuestion2 != null) {
                            tvQuestion2.setText(qText);
                            fillOptions(radioGroup2, options);
                        }
                        count++;
                    }

                    // Tự động phát nhạc nếu có link
                    if (audioUrlFromFirebase != null) playAudio(audioUrlFromFirebase);
                } else {
                    Log.d("DEBUG", "Đường dẫn sai, không tìm thấy dữ liệu tại: " + lessonRef.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase Error: " + error.getMessage());
            }
        });
    }

    private void fillOptions(RadioGroup group, List<String> options) {
        if (group == null || options == null) return;
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof RadioButton && i < options.size()) {
                ((RadioButton) child).setText(options.get(i));
            }
        }
    }

    private void playAudio(String audioUrl) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> mp.start());
        } catch (IOException e) {
            Log.e(TAG, "Error playing audio", e);
        }
    }

    private void checkAnswers() {
        if (correctAnswers.size() < 2) return;

        int id1 = radioGroup1.getCheckedRadioButtonId();
        int id2 = radioGroup2.getCheckedRadioButtonId();

        if (id1 == -1 || id2 == -1) {
            Toast.makeText(getContext(), "Vui lòng chọn đầy đủ đáp án!", Toast.LENGTH_SHORT).show();
            return;
        }

        View rb1 = radioGroup1.findViewById(id1);
        View rb2 = radioGroup2.findViewById(id2);

        int index1 = radioGroup1.indexOfChild(rb1);
        int index2 = radioGroup2.indexOfChild(rb2);

        if (index1 == correctAnswers.get(0) && index2 == correctAnswers.get(1)) {
            Toast.makeText(getContext(), "Chính xác!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Sai rồi, hãy nghe lại nhé!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
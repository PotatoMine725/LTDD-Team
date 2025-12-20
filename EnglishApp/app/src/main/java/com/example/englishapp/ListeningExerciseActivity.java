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
import java.util.Collections;
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
    
    // Danh sách lessons để chuyển đổi
    private List<LessonInfo> availableLessons = new ArrayList<>();
    private int currentLessonIndex = -1;
    
    // Inner class để lưu thông tin lesson
    private static class LessonInfo {
        String lessonId;
        String title;
        String imageUrl;
        boolean hasContent;

        LessonInfo(String lessonId, String title, String imageUrl, boolean hasContent) {
            this.lessonId = lessonId;
            this.title = title;
            this.imageUrl = imageUrl;
            this.hasContent = hasContent;
        }
    }
    
    // Inner class để lưu trữ dữ liệu câu hỏi
    private static class QuestionData {
        String questionText;
        List<String> options;
        int correctIndex;
        int order;

        QuestionData(String questionText, List<String> options, int correctIndex, int order) {
            this.questionText = questionText;
            this.options = options;
            this.correctIndex = correctIndex;
            this.order = order;
        }
    }

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

        // Thêm click listener cho ảnh để chuyển lesson
        if (ivTopic != null) {
            ivTopic.setOnClickListener(v -> {
                Log.d(TAG, "Image clicked! Current lesson: " + lessonId);
                switchToNextLesson();
            });
            // Đảm bảo ảnh có thể click được
            ivTopic.setClickable(true);
            ivTopic.setFocusable(true);
        } else {
            Log.e(TAG, "ivTopic is null! Cannot set click listener");
        }

        // Load danh sách lessons trước, sau đó load lesson hiện tại
        loadLessonsList();

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

    /**
     * Load danh sách lessons của topic để có thể chuyển đổi
     */
    private void loadLessonsList() {
        if (topicId == null) {
            Log.e(TAG, "Topic ID is null");
            loadDataFromFirebase();
            return;
        }

        DatabaseReference lessonsRef = mDatabase.child("topics")
                .child("listening")
                .child(topicId)
                .child("lessons");

        lessonsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                availableLessons.clear();
                
                if (snapshot.exists()) {
                    for (DataSnapshot lessonSnapshot : snapshot.getChildren()) {
                        try {
                            String lessonIdFromFirebase = lessonSnapshot.getKey();
                            String title = lessonSnapshot.child("title").getValue(String.class);
                            String imageUrl = lessonSnapshot.child("image_url").getValue(String.class);
                            
                            // Kiểm tra xem lesson có questions không (có nội dung)
                            DataSnapshot questionsSnapshot = lessonSnapshot.child("questions");
                            boolean hasContent = questionsSnapshot.exists() && questionsSnapshot.getChildrenCount() > 0;
                            
                            if (title != null && lessonIdFromFirebase != null) {
                                availableLessons.add(new LessonInfo(
                                        lessonIdFromFirebase,
                                        title,
                                        imageUrl != null ? imageUrl : "",
                                        hasContent
                                ));
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing lesson info", e);
                        }
                    }
                    
                    // Sắp xếp lessons theo ID để đảm bảo thứ tự
                    Collections.sort(availableLessons, (l1, l2) -> l1.lessonId.compareTo(l2.lessonId));
                    
                    // Tìm index của lesson hiện tại
                    for (int i = 0; i < availableLessons.size(); i++) {
                        if (availableLessons.get(i).lessonId.equals(lessonId)) {
                            currentLessonIndex = i;
                            break;
                        }
                    }
                    
                    Log.d(TAG, "Loaded " + availableLessons.size() + " lessons, current index: " + currentLessonIndex);
                }
                
                // Sau khi load xong danh sách lessons, load lesson hiện tại
                loadDataFromFirebase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase Error loading lessons list: " + error.getMessage());
                // Vẫn load lesson hiện tại nếu lỗi
                loadDataFromFirebase();
            }
        });
    }

    /**
     * Chuyển sang lesson tiếp theo (chỉ 2 lesson đầu tiên có nội dung)
     */
    private void switchToNextLesson() {
        Log.d(TAG, "switchToNextLesson called. Available lessons: " + availableLessons.size());
        
        if (availableLessons.isEmpty()) {
            Log.w(TAG, "No lessons available, reloading lessons list...");
            loadLessonsList();
            Toast.makeText(getContext(), "Đang tải danh sách bài học...", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lọc chỉ lấy 2 lesson đầu tiên có nội dung
        List<LessonInfo> lessonsWithContent = new ArrayList<>();
        for (LessonInfo lesson : availableLessons) {
            if (lesson.hasContent && lessonsWithContent.size() < 2) {
                lessonsWithContent.add(lesson);
                Log.d(TAG, "Added lesson with content: " + lesson.title + " (ID: " + lesson.lessonId + ")");
            }
        }

        if (lessonsWithContent.isEmpty()) {
            Toast.makeText(getContext(), "Không có lesson nào có nội dung", Toast.LENGTH_SHORT).show();
            return;
        }

        if (lessonsWithContent.size() < 2) {
            Toast.makeText(getContext(), "Chỉ có " + lessonsWithContent.size() + " lesson có nội dung", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tìm lesson hiện tại trong danh sách có nội dung
        int currentIndexInContentList = -1;
        for (int i = 0; i < lessonsWithContent.size(); i++) {
            if (lessonsWithContent.get(i).lessonId.equals(lessonId)) {
                currentIndexInContentList = i;
                Log.d(TAG, "Found current lesson at index: " + i);
                break;
            }
        }

        // Nếu không tìm thấy lesson hiện tại, mặc định chuyển sang lesson đầu tiên
        if (currentIndexInContentList == -1) {
            Log.w(TAG, "Current lesson not found in content list, switching to first lesson");
            currentIndexInContentList = 0;
        }

        // Chuyển sang lesson tiếp theo (hoặc quay lại lesson đầu tiên nếu đang ở lesson cuối)
        int nextIndex = (currentIndexInContentList + 1) % lessonsWithContent.size();
        LessonInfo nextLesson = lessonsWithContent.get(nextIndex);

        Log.d(TAG, "Switching from lesson " + lessonId + " to lesson: " + nextLesson.title + " (ID: " + nextLesson.lessonId + ")");

        // Dừng audio nếu đang phát
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        
        // Reset radio buttons
        if (radioGroup1 != null) radioGroup1.clearCheck();
        if (radioGroup2 != null) radioGroup2.clearCheck();
        
        // Cập nhật lessonId và reload
        lessonId = nextLesson.lessonId;
        
        // Reload dữ liệu
        loadDataFromFirebase();
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
                    Log.d(TAG, "Dữ liệu đã về! Topic: " + topicId + ", Lesson: " + lessonId);
                    
                    // 1. Load tiêu đề và ảnh
                    String title = snapshot.child("title").getValue(String.class);
                    String imageUrl = snapshot.child("image_url").getValue(String.class);
                    audioUrlFromFirebase = snapshot.child("audio_url").getValue(String.class);

                    if (tvTitle != null && title != null) {
                        tvTitle.setText(title);
                    }

                    // Load ảnh bằng Glide với placeholder và error handling
                    if (isAdded() && ivTopic != null) {
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Log.d(TAG, "Loading image from URL: " + imageUrl);
                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.topic_technology) // Ảnh placeholder
                                    .error(R.drawable.topic_technology) // Ảnh lỗi
                                    .into(ivTopic);
                            Log.d(TAG, "Image load request sent to Glide");
                        } else {
                            // Nếu không có imageUrl, dùng ảnh mặc định
                            ivTopic.setImageResource(R.drawable.topic_technology);
                            Log.w(TAG, "No image URL found, using default image");
                        }
                    } else {
                        Log.e(TAG, "Cannot load image: isAdded=" + isAdded() + ", ivTopic=" + (ivTopic != null));
                    }

                    // 2. Load danh sách câu hỏi (questions là một array)
                    DataSnapshot questionsSnapshot = snapshot.child("questions");
                    correctAnswers.clear();
                    
                    Log.d(TAG, "Questions snapshot exists: " + questionsSnapshot.exists());
                    if (questionsSnapshot.exists()) {
                        Log.d(TAG, "Questions children count: " + questionsSnapshot.getChildrenCount());
                    }
                    
                    // Tạo list để sắp xếp questions theo order
                    List<QuestionData> questionList = new ArrayList<>();
                    
                    if (questionsSnapshot.exists() && questionsSnapshot.getChildrenCount() > 0) {
                        int questionIndex = 0;
                        for (DataSnapshot qSnap : questionsSnapshot.getChildren()) {
                            try {
                                Log.d(TAG, "Processing question at index " + questionIndex + ", key: " + qSnap.getKey());
                                
                                String qText = qSnap.child("question_text").getValue(String.class);
                                Integer correctIdx = qSnap.child("correct_index").getValue(Integer.class);
                                Integer order = qSnap.child("order").getValue(Integer.class);

                                Log.d(TAG, "Question data - text: " + qText + ", correctIdx: " + correctIdx + ", order: " + order);

                                // Lấy danh sách options từ array
                                List<String> options = new ArrayList<>();
                                DataSnapshot optionsSnapshot = qSnap.child("options");
                                if (optionsSnapshot.exists()) {
                                    Log.d(TAG, "Options exists, children count: " + optionsSnapshot.getChildrenCount());
                                    
                                    // Sắp xếp theo key để đảm bảo thứ tự đúng
                                    List<DataSnapshot> optionSnapshots = new ArrayList<>();
                                    for (DataSnapshot opt : optionsSnapshot.getChildren()) {
                                        optionSnapshots.add(opt);
                                    }
                                    // Sắp xếp theo key (numeric)
                                    optionSnapshots.sort((s1, s2) -> {
                                        try {
                                            int key1 = Integer.parseInt(s1.getKey());
                                            int key2 = Integer.parseInt(s2.getKey());
                                            return Integer.compare(key1, key2);
                                        } catch (NumberFormatException e) {
                                            return s1.getKey().compareTo(s2.getKey());
                                        }
                                    });
                                    
                                    for (DataSnapshot opt : optionSnapshots) {
                                        String optionValue = opt.getValue(String.class);
                                        if (optionValue != null) {
                                            options.add(optionValue);
                                            Log.d(TAG, "Added option: " + optionValue);
                                        }
                                    }
                                } else {
                                    Log.w(TAG, "Options snapshot does not exist for question");
                                }

                                if (qText != null && correctIdx != null && order != null) {
                                    questionList.add(new QuestionData(qText, options, correctIdx, order));
                                    Log.d(TAG, "Successfully loaded question " + order + ": " + qText + " with " + options.size() + " options");
                                } else {
                                    Log.w(TAG, "Question data incomplete - text: " + (qText != null) + ", correctIdx: " + (correctIdx != null) + ", order: " + (order != null));
                                }
                                questionIndex++;
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing question at index " + questionIndex, e);
                            }
                        }
                    } else {
                        Log.w(TAG, "No questions found in snapshot or snapshot is empty");
                    }

                    // Sắp xếp questions theo order
                    questionList.sort((q1, q2) -> Integer.compare(q1.order, q2.order));

                    Log.d(TAG, "Total questions loaded: " + questionList.size());

                    // Hiển thị 2 câu hỏi đầu tiên
                    int count = 0;
                    for (QuestionData questionData : questionList) {
                        if (count >= 2) break;

                        correctAnswers.add(questionData.correctIndex);

                        if (count == 0 && tvQuestion1 != null) {
                            tvQuestion1.setText("Question " + (count + 1) + ":\n" + questionData.questionText);
                            fillOptions(radioGroup1, questionData.options);
                            Log.d(TAG, "Displayed question 1: " + questionData.questionText);
                        } else if (count == 1 && tvQuestion2 != null) {
                            tvQuestion2.setText("Question " + (count + 1) + ":\n" + questionData.questionText);
                            fillOptions(radioGroup2, questionData.options);
                            Log.d(TAG, "Displayed question 2: " + questionData.questionText);
                        }
                        count++;
                    }
                    
                    if (questionList.isEmpty()) {
                        Log.e(TAG, "No questions were loaded! Check Firebase structure.");
                        Toast.makeText(getContext(), "Không tìm thấy câu hỏi", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "Successfully loaded and displayed " + count + " questions out of " + questionList.size());
                    }
                    
                    // Reset radio buttons
                    if (radioGroup1 != null) radioGroup1.clearCheck();
                    if (radioGroup2 != null) radioGroup2.clearCheck();

                    // Tự động phát nhạc nếu có link
                    if (audioUrlFromFirebase != null && !audioUrlFromFirebase.isEmpty()) {
                        playAudio(audioUrlFromFirebase);
                    }
                } else {
                    Log.e(TAG, "Đường dẫn sai, không tìm thấy dữ liệu tại: " + lessonRef.toString());
                    Toast.makeText(getContext(), "Không tìm thấy dữ liệu bài học", Toast.LENGTH_SHORT).show();
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
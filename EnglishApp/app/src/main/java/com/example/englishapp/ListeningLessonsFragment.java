package com.example.englishapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.englishapp.adapter.ListeningLessonAdapter;
import com.example.englishapp.model.ListeningLesson;
import com.example.englishapp.service.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListeningLessonsFragment extends Fragment {

    private static final String TAG = "ListeningLessonsFragment";
    private static final String ARG_TOPIC_ID = "topic_id";
    private static final String ARG_TOPIC_NAME = "topic_name";

    private String topicId, topicName;
    private RecyclerView lessonsRecyclerView;
    private ListeningLessonAdapter lessonAdapter;
    private FirebaseService firebaseService;
    private TextView tvTopicTitle;

    public static ListeningLessonsFragment newInstance(String topicId, String topicName) {
        ListeningLessonsFragment fragment = new ListeningLessonsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOPIC_ID, topicId);
        args.putString(ARG_TOPIC_NAME, topicName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topicId = getArguments().getString(ARG_TOPIC_ID);
            topicName = getArguments().getString(ARG_TOPIC_NAME);
        }
        firebaseService = FirebaseService.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.listening_lessons, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup back button
        ImageView backIcon = view.findViewById(R.id.back_icon);
        if (backIcon != null) {
            backIcon.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }

        tvTopicTitle = view.findViewById(R.id.topic_title);
        if (tvTopicTitle != null && topicName != null) {
            tvTopicTitle.setText(topicName);
        }

        // Setup RecyclerView
        lessonsRecyclerView = view.findViewById(R.id.lessons_recycler_view);
        if (lessonsRecyclerView != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
            lessonsRecyclerView.setLayoutManager(layoutManager);

            lessonAdapter = new ListeningLessonAdapter(new ArrayList<>());
            lessonsRecyclerView.setAdapter(lessonAdapter);

            lessonAdapter.setOnLessonClickListener((lesson, position) -> {
                if (lesson.hasContent()) {
                    navigateToExercise(lesson);
                } else {
                    showMessage("Bài học này chưa có nội dung");
                }
            });
        }

        loadLessonsFromFirebase();
    }

    private void loadLessonsFromFirebase() {
        if (topicId == null) {
            Log.e(TAG, "Topic ID is null");
            showError("Topic ID không hợp lệ");
            return;
        }

        Log.d(TAG, "Loading lessons for topic: " + topicId);
        
        DatabaseReference lessonsRef = firebaseService.getLessonsRef(topicId);

        lessonsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ListeningLesson> lessons = new ArrayList<>();

                Log.d(TAG, "Firebase response - exists: " + snapshot.exists() + ", children: " + snapshot.getChildrenCount());

                if (snapshot.exists()) {
                    for (DataSnapshot lessonSnapshot : snapshot.getChildren()) {
                        try {
                            String lessonId = lessonSnapshot.getKey();
                            String title = lessonSnapshot.child("title").getValue(String.class);
                            String imageUrl = lessonSnapshot.child("image_url").getValue(String.class);
                            String audioUrl = lessonSnapshot.child("audio_url").getValue(String.class);
                            Integer duration = lessonSnapshot.child("duration").getValue(Integer.class);

                            // Kiểm tra xem lesson có questions không (có nội dung)
                            DataSnapshot questionsSnapshot = lessonSnapshot.child("questions");
                            boolean hasContent = questionsSnapshot.exists() && questionsSnapshot.getChildrenCount() > 0;

                            if (title != null && lessonId != null) {
                                ListeningLesson lesson = new ListeningLesson(
                                        lessonId,
                                        title,
                                        imageUrl != null ? imageUrl : "",
                                        audioUrl != null ? audioUrl : "",
                                        duration != null ? duration : 0,
                                        hasContent
                                );
                                lessons.add(lesson);
                                Log.d(TAG, "Loaded lesson: " + title + " (ID: " + lessonId + ", hasContent: " + hasContent + ")");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing lesson data", e);
                        }
                    }
                } else {
                    Log.w(TAG, "No lessons found for topic: " + topicId);
                    showError("Không tìm thấy bài học cho chủ đề này");
                }

                if (lessonAdapter != null) {
                    lessonAdapter.updateData(lessons);
                    Log.d(TAG, "Updated adapter with " + lessons.size() + " lessons");
                    
                    if (lessons.isEmpty()) {
                        showError("Chủ đề này chưa có bài học");
                    }
                } else {
                    Log.e(TAG, "Lesson adapter is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase Error: " + error.getMessage());
                Log.e(TAG, "Error details: " + error.getDetails());
                showError("Lỗi kết nối Firebase: " + error.getMessage());
            }
        });
    }

    private void navigateToExercise(ListeningLesson lesson) {
        try {
            if (getActivity() == null) {
                Log.e(TAG, "Activity is null, cannot navigate to exercise");
                return;
            }

            Log.d(TAG, "Navigating to exercise - Topic: " + topicId + ", Lesson: " + lesson.getLessonId());

            ListeningExerciseActivity exerciseFragment =
                    ListeningExerciseActivity.newInstance(topicId, lesson.getLessonId());

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.container, exerciseFragment, "ListeningExerciseActivity")
                    .addToBackStack("LessonsToExercise")
                    .commit();
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to exercise", e);
            showError("Failed to open exercise");
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessage(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}


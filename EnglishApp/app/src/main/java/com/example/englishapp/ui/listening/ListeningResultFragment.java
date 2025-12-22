package com.example.englishapp.ui.listening;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.adapter.QuestionResultAdapter;
import com.example.englishapp.model.QuestionResult;

import java.util.ArrayList;
import java.util.List;

public class ListeningResultFragment extends Fragment {

    private static final String TAG = "ListeningResultFragment";
    private static final String ARG_TOPIC_ID = "topic_id";
    private static final String ARG_LESSON_ID = "lesson_id";
    private static final String ARG_SCORE = "score";
    private static final String ARG_TOTAL = "total";
    private static final String ARG_RESULTS = "results";

    private String topicId, lessonId;
    private int score, total;
    private ArrayList<QuestionResult> questionResults;

    private TextView scoreText, percentageText;
    private RecyclerView resultsRecyclerView;
    private Button retryButton, continueButton;
    private QuestionResultAdapter resultAdapter;

    public static ListeningResultFragment newInstance(String topicId, String lessonId, 
                                                     int score, int total, 
                                                     ArrayList<QuestionResult> results) {
        ListeningResultFragment fragment = new ListeningResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOPIC_ID, topicId);
        args.putString(ARG_LESSON_ID, lessonId);
        args.putInt(ARG_SCORE, score);
        args.putInt(ARG_TOTAL, total);
        args.putParcelableArrayList(ARG_RESULTS, results);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topicId = getArguments().getString(ARG_TOPIC_ID);
            lessonId = getArguments().getString(ARG_LESSON_ID);
            score = getArguments().getInt(ARG_SCORE);
            total = getArguments().getInt(ARG_TOTAL);
            questionResults = getArguments().getParcelableArrayList(ARG_RESULTS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.listening_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupBackButton(view);
        displayResults();
        setupButtons();
    }

    private void initViews(View view) {
        scoreText = view.findViewById(R.id.score_text);
        percentageText = view.findViewById(R.id.percentage_text);
        resultsRecyclerView = view.findViewById(R.id.results_recycler_view);
        retryButton = view.findViewById(R.id.retry_button);
        continueButton = view.findViewById(R.id.continue_button);
    }

    private void setupBackButton(View view) {
        ImageView backIcon = view.findViewById(R.id.back_icon);
        if (backIcon != null) {
            backIcon.setOnClickListener(v -> {
                Log.d(TAG, "Back button clicked");
                navigateBack();
            });
        }
    }

    private void displayResults() {
        // Display score
        scoreText.setText(score + "/" + total);
        
        // Calculate and display percentage
        int percentage = total > 0 ? (score * 100) / total : 0;
        percentageText.setText(percentage + "%");
        
        // Change score color based on performance
        if (percentage >= 80) {
            scoreText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if (percentage >= 60) {
            scoreText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            scoreText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        // Setup RecyclerView
        if (questionResults != null) {
            Log.d(TAG, "Setting up RecyclerView with " + questionResults.size() + " results");
            
            // Debug: Log all results
            for (int i = 0; i < questionResults.size(); i++) {
                QuestionResult result = questionResults.get(i);
                Log.d(TAG, "Result " + i + ": Q" + result.getQuestionNumber() + 
                      " - " + result.getQuestionText() + 
                      " - User: " + result.getUserAnswer() + 
                      " - Correct: " + result.getCorrectAnswer() + 
                      " - IsCorrect: " + result.isCorrect());
            }
            
            resultAdapter = new QuestionResultAdapter(questionResults);
            resultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            resultsRecyclerView.setAdapter(resultAdapter);
            
            Log.d(TAG, "RecyclerView adapter set with " + resultAdapter.getItemCount() + " items");
        } else {
            Log.e(TAG, "questionResults is null!");
        }
    }

    private void setupButtons() {
        retryButton.setOnClickListener(v -> {
            Log.d(TAG, "Retry button clicked");
            retryExercise();
        });

        continueButton.setOnClickListener(v -> {
            Log.d(TAG, "Continue button clicked");
            navigateToLessons();
        });
    }

    private void retryExercise() {
        if (getActivity() == null) return;

        try {
            // Navigate back to exercise
            ListeningExerciseFragment exerciseFragment = 
                    ListeningExerciseFragment.newInstance(topicId, lessonId);

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left,
                            R.anim.slide_out_right,
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                    )
                    .replace(R.id.container, exerciseFragment, "ListeningExerciseFragment")
                    .addToBackStack("ResultToExercise")
                    .commit();

            Log.d(TAG, "Navigated back to exercise");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to exercise", e);
        }
    }

    private void navigateToLessons() {
        if (getActivity() == null) return;

        try {
            // Navigate to lessons list
            ListeningLessonsFragment lessonsFragment = 
                    ListeningLessonsFragment.newInstance(topicId, "Topic Lessons");

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.container, lessonsFragment, "ListeningLessonsFragment")
                    .addToBackStack("ResultToLessons")
                    .commit();

            Log.d(TAG, "Navigated to lessons");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to lessons", e);
        }
    }

    private void navigateBack() {
        if (getActivity() != null && getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}
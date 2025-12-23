package com.example.englishapp.ui.speaking;

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

import com.example.englishapp.R;

/**
 * SpeakingTestActivity - Màn hình test speaking với navigation
 */
public class SpeakingTestActivity extends Fragment {

    private static final String TAG = "SpeakingTestFragment";
    private static final String ARG_TOPIC = "topic_name";

    // UI Components
    private TextView tvPageNumber;
    private TextView tvQuestion;
    private ImageView icPrev;
    private ImageView icNext;
    private ImageView imgAvatar;
    private ImageView icBookmark;
    private ImageView icMicro;
    private ImageView icEars;

    // Data
    private String topicName;
    private int currentPage = 1;
    private int totalPages = 5;

    /**
     * Factory method để tạo instance với topic name
     */
    public static SpeakingTestActivity newInstance(String topicName) {
        SpeakingTestActivity fragment = new SpeakingTestActivity();
        Bundle args = new Bundle();
        args.putString(ARG_TOPIC, topicName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topicName = getArguments().getString(ARG_TOPIC, "Business English");
        }
        Log.d(TAG, "SpeakingTestActivity created for topic: " + topicName);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.speaking_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            initViews(view);
            setupListeners();
            updatePageDisplay();
            Log.d(TAG, "SpeakingTestFragment initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing SpeakingTestFragment", e);
            showError("Failed to load speaking test");
        }
    }

    /**
     * Initialize all views
     */
    private void initViews(View view) {
        try {
            // Navigation bar
            tvPageNumber = view.findViewById(R.id.tv_page_number);
            icPrev = view.findViewById(R.id.ic_prev);
            icNext = view.findViewById(R.id.ic_next);

            // Content
            tvQuestion = view.findViewById(R.id.textView11);
            imgAvatar = view.findViewById(R.id.imageView19);

            // Tool bar
            icBookmark = view.findViewById(R.id.imageView24);
            icMicro = view.findViewById(R.id.imageView22);
            icEars = view.findViewById(R.id.imageView23);

            Log.d(TAG, "All views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
        }
    }

    /**
     * Setup click listeners
     */
    private void setupListeners() {
        try {
            // Previous button
            if (icPrev != null) {
                icPrev.setOnClickListener(v -> onPreviousClicked());
            }

            // Next button
            if (icNext != null) {
                icNext.setOnClickListener(v -> onNextClicked());
            }

            // Bookmark button
            if (icBookmark != null) {
                icBookmark.setOnClickListener(v -> onBookmarkClicked());
            }

            // Microphone button
            if (icMicro != null) {
                icMicro.setOnClickListener(v -> onMicrophoneClicked());
            }

            // Listen button
            if (icEars != null) {
                icEars.setOnClickListener(v -> onListenClicked());
            }

            Log.d(TAG, "All listeners setup successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up listeners", e);
        }
    }

    /**
     * Handle previous page click
     */
    private void onPreviousClicked() {
        try {
            if (currentPage > 1) {
                currentPage--;
                updatePageDisplay();
                showMessage("Page " + currentPage);
                Log.d(TAG, "Moved to page: " + currentPage);
            } else {
                showMessage("This is the first page");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling previous click", e);
        }
    }

    /**
     * Handle next page click
     */
    private void onNextClicked() {
        try {
            if (currentPage < totalPages) {
                currentPage++;
                updatePageDisplay();
                showMessage("Page " + currentPage);
                Log.d(TAG, "Moved to page: " + currentPage);
            } else {
                showMessage("This is the last page");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling next click", e);
        }
    }

    /**
     * Handle bookmark click
     */
    private void onBookmarkClicked() {
        try {
            showMessage("Question bookmarked");
            Log.d(TAG, "Bookmark clicked for page: " + currentPage);
        } catch (Exception e) {
            Log.e(TAG, "Error handling bookmark click", e);
        }
    }

    /**
     * Handle microphone click
     */
    private void onMicrophoneClicked() {
        try {
            showMessage("Recording started...");
            Log.d(TAG, "Microphone clicked");
            // TODO: Implement recording logic
        } catch (Exception e) {
            Log.e(TAG, "Error handling microphone click", e);
        }
    }

    /**
     * Handle listen click
     */
    private void onListenClicked() {
        try {
            showMessage("Playing audio...");
            Log.d(TAG, "Listen button clicked");
            // TODO: Implement audio playback logic
        } catch (Exception e) {
            Log.e(TAG, "Error handling listen click", e);
        }
    }

    /**
     * Update page number display
     */
    private void updatePageDisplay() {
        if (tvPageNumber != null) {
            tvPageNumber.setText(currentPage + "/" + totalPages);
        }
    }

    /**
     * Show toast message
     */
    private void showMessage(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "SpeakingTestActivity destroyed");

        // Clean up references
        tvPageNumber = null;
        tvQuestion = null;
        icPrev = null;
        icNext = null;
        imgAvatar = null;
        icBookmark = null;
        icMicro = null;
        icEars = null;
    }
}
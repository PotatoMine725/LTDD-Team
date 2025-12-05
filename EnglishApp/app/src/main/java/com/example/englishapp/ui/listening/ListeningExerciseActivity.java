package com.example.englishapp.ui.listening;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.englishapp.R;

public class ListeningExerciseActivity extends Fragment {

    private static final String TAG = "ListeningExercise";
    private static final String ARG_TOPIC_NAME = "topic_name";

    private String topicName;
    private RadioGroup radioGroup1;
    private RadioGroup radioGroup2;
    private Button replayButton;
    private Button submitButton;

    public static ListeningExerciseActivity newInstance(String topicName) {
        ListeningExerciseActivity fragment = new ListeningExerciseActivity();
        Bundle args = new Bundle();
        args.putString(ARG_TOPIC_NAME, topicName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topicName = getArguments().getString(ARG_TOPIC_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        try {
            return inflater.inflate(R.layout.listening_excercise, container, false);
        } catch (Exception e) {
            Log.e(TAG, "Error inflating layout", e);
            showError("Failed to load screen");
            return null;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            setupBackButton(view);
            setupRefreshButton(view);
            setupRadioGroups(view);
            setupActionButtons(view);
            setupAudioControls(view);

            Log.d(TAG, "Exercise screen initialized for topic: " + topicName);
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated", e);
            showError("Failed to initialize screen");
        }
    }

    /**
     * Setup back button để quay về ListeningTopicActivity
     */
    private void setupBackButton(View view) {
        try {
            ImageView backIcon = view.findViewById(R.id.back_icon);
            if (backIcon != null) {
                backIcon.setOnClickListener(v -> {
                    Log.d(TAG, "Back button clicked");
                    navigateBack();
                });
                Log.d(TAG, "Back button setup successfully");
            } else {
                Log.e(TAG, "back_icon not found in layout");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up back button", e);
        }
    }

    /**
     * Setup refresh button
     */
    private void setupRefreshButton(View view) {
        try {
            ImageView refreshIcon = view.findViewById(R.id.visibility_icon);
            if (refreshIcon != null) {
                refreshIcon.setOnClickListener(v -> {
                    Log.d(TAG, "Refresh button clicked");
                    resetExercise();
                });
                Log.d(TAG, "Refresh button setup successfully");
            } else {
                Log.e(TAG, "visibility_icon not found in layout");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up refresh button", e);
        }
    }

    /**
     * Setup radio groups for questions
     */
    private void setupRadioGroups(View view) {
        try {
            radioGroup1 = view.findViewById(R.id.radio_group_1);
            radioGroup2 = view.findViewById(R.id.radio_group_2);

            if (radioGroup1 != null && radioGroup2 != null) {
                Log.d(TAG, "Radio groups setup successfully");
            } else {
                Log.e(TAG, "Radio groups not found in layout");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up radio groups", e);
        }
    }

    /**
     * Setup action buttons (Replay and Submit)
     */
    private void setupActionButtons(View view) {
        try {
            replayButton = view.findViewById(R.id.replay_button);
            submitButton = view.findViewById(R.id.submit_button);

            if (replayButton != null) {
                replayButton.setOnClickListener(v -> {
                    Log.d(TAG, "Replay button clicked");
                    replayAudio();
                });
            }

            if (submitButton != null) {
                submitButton.setOnClickListener(v -> {
                    Log.d(TAG, "Submit button clicked");
                    submitAnswers();
                });
            }

            Log.d(TAG, "Action buttons setup successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up action buttons", e);
        }
    }

    /**
     * Setup audio control buttons
     */
    private void setupAudioControls(View view) {
        try {
            ImageView playPauseButton = view.findViewById(R.id.play_pause_button);
            ImageView stopButton = view.findViewById(R.id.stop_button);

            if (playPauseButton != null) {
                playPauseButton.setOnClickListener(v -> {
                    Log.d(TAG, "Play/Pause button clicked");
                    togglePlayPause();
                });
            }

            if (stopButton != null) {
                stopButton.setOnClickListener(v -> {
                    Log.d(TAG, "Stop button clicked");
                    stopAudio();
                });
            }

            Log.d(TAG, "Audio controls setup successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up audio controls", e);
        }
    }

    /**
     * Navigate back to ListeningTopicActivity
     */
    private void navigateBack() {
        try {
            if (getActivity() != null) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                    Log.d(TAG, "Navigated back to ListeningTopicActivity");
                } else {
                    Log.w(TAG, "No back stack entries to pop");
                }
            } else {
                Log.e(TAG, "Activity is null, cannot navigate back");
                showError("Cannot go back");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error navigating back", e);
            showError("Failed to go back");
        }
    }


    /**
     * Reset exercise
     */
    private void resetExercise() {
        try {
            if (radioGroup1 != null) {
                radioGroup1.clearCheck();
            }
            if (radioGroup2 != null) {
                radioGroup2.clearCheck();
            }
            showMessage("Exercise reset");
            Log.d(TAG, "Exercise reset successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error resetting exercise", e);
            showError("Failed to reset exercise");
        }
    }

    /**
     * Replay audio
     */
    private void replayAudio() {
        try {
            // TODO: Implement audio replay logic
            showMessage("Replaying audio...");
            Log.d(TAG, "Audio replay requested");
        } catch (Exception e) {
            Log.e(TAG, "Error replaying audio", e);
            showError("Failed to replay audio");
        }
    }

    /**
     * Submit answers
     */
    private void submitAnswers() {
        try {
            int selectedAnswer1 = radioGroup1 != null ? radioGroup1.getCheckedRadioButtonId() : -1;
            int selectedAnswer2 = radioGroup2 != null ? radioGroup2.getCheckedRadioButtonId() : -1;

            if (selectedAnswer1 == -1 || selectedAnswer2 == -1) {
                showError("Please answer all questions");
                return;
            }

            // TODO: Implement answer checking logic
            showMessage("Answers submitted!");
            Log.d(TAG, "Answers submitted - Q1: " + selectedAnswer1 + ", Q2: " + selectedAnswer2);

        } catch (Exception e) {
            Log.e(TAG, "Error submitting answers", e);
            showError("Failed to submit answers");
        }
    }

    /**
     * Toggle play/pause
     */
    private void togglePlayPause() {
        try {
            // TODO: Implement play/pause logic
            showMessage("Play/Pause toggled");
            Log.d(TAG, "Play/Pause toggled");
        } catch (Exception e) {
            Log.e(TAG, "Error toggling play/pause", e);
            showError("Failed to toggle play/pause");
        }
    }

    /**
     * Stop audio
     */
    private void stopAudio() {
        try {
            // TODO: Implement stop audio logic
            showMessage("Audio stopped");
            Log.d(TAG, "Audio stopped");
        } catch (Exception e) {
            Log.e(TAG, "Error stopping audio", e);
            showError("Failed to stop audio");
        }
    }

    /**
     * Show error message to user
     */
    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Show info message to user
     */
    private void showMessage(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
package com.example.englishapp.ui.speaking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.englishapp.R;
import com.example.englishapp.data.api.OpenAICallBack;
import com.example.englishapp.data.model.SpeakingQuestion;
import com.example.englishapp.data.repository.SpeakingRepository;
import com.example.englishapp.utils.SpeechToTextHelper;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * SpeakingTestFragment - Màn hình test speaking với navigation
 */
public class SpeakingTestFragment extends Fragment {

    private static final String TAG = "SpeakingTestFragment";
    private static final String ARG_TOPIC_ID = "TOPIC_ID";
    private ActivityResultLauncher<Intent> speechLauncher;

    // UI Components
    private TextView tvPageNumber;
    private ImageView icPrev;
    private ImageView icNext;
    private ImageView imgAvatar;
    private ImageView icBookmark;
    private ImageView icMicro;
    private ImageView icEars;
    private TextView tvQuestion, tvPage;
    private ImageView btnNext, btnPrev, btnMic;
    private TextView tvFeedback;

    // Data
    private String topicName;
    private int currentPage = 1;
    private int totalPages = 5;
    private final SpeakingRepository speakingRepository = new SpeakingRepository();

    public static SpeakingTestFragment newInstance(String topicId) {
        SpeakingTestFragment fragment = new SpeakingTestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOPIC_ID, topicId);
        fragment.setArguments(args);
        return fragment;
    }

    // bậm mic no
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        speechLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        ArrayList<String> res = data.getStringArrayListExtra(
                                RecognizerIntent.EXTRA_RESULTS);
                        if (res != null && !res.isEmpty()) {
                            String spokenText = res.get(0);
                            sendToAI(spokenText);
                        }
                    }
                });

        if (getArguments() != null) {
            topicName = getArguments().getString(ARG_TOPIC_ID, "Business English");
        }
        Log.d(TAG, "SpeakingTestFragment created for topic: " + topicName);
    }

    private void startSpeech() {
        speechLauncher.launch(SpeechToTextHelper.create());
    }

    private void sendToAI(String answer) {
        if (tvQuestion == null || tvFeedback == null) {
            return;
        }
        String question = tvQuestion.getText().toString().trim();
        if (question.isEmpty()) {
            showError("Khong co cau hoi de cham diem");
            return;
        }
        tvFeedback.setText("Grading...");
        speakingRepository.evaluateSpeaking(
                question,
                answer,
                new OpenAICallBack() {
                    @Override
                    public void onSuccess(String reply) {
                        if (getActivity() == null)
                            return;
                        getActivity().runOnUiThread(() -> {
                            tvFeedback.setText(formatFeedback(reply));
                            maybeShowScoreToast(reply);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        if (getActivity() == null)
                            return;
                        getActivity().runOnUiThread(() -> showError(error));
                    }
                });
    }

    private String formatFeedback(String reply) {
        String cleaned = reply == null ? "" : reply.trim();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceFirst("^```[a-zA-Z]*\\s*", "");
            cleaned = cleaned.replaceFirst("\\s*```$", "");
        }
        try {
            JSONObject json = new JSONObject(cleaned);
            int score = json.optInt("score", -1);
            int pronunciation = json.optInt("pronunciation", -1);
            boolean matches = json.optBoolean("matches_question", false);
            String shortFeedback = json.optString("short_feedback", "").trim();

            StringBuilder sb = new StringBuilder();
            if (score >= 0) {
                sb.append("\r\n" + //
                        "Pronunciation point: ").append(score).append("/10\n");
            }
            if (pronunciation >= 0) {
                sb.append("Pronun: ").append(pronunciation).append("/10\n");
            }
            sb.append("Dung cau hoi: ").append(matches ? "Co" : "Khong").append("\n");
            if (!shortFeedback.isEmpty()) {
                sb.append(shortFeedback).append("\n");
            }

            String issue = firstOfArray(json.optJSONArray("pronunciation_issues"));
            if (!issue.isEmpty()) {
                sb.append("\r\n" + //
                        "Pronunciation error: ").append(issue).append("\n");
            }

            String tip = firstOfArray(json.optJSONArray("pronunciation_tips"));
            if (!tip.isEmpty()) {
                sb.append("pronunciation suggestions: ").append(tip);
            }
            return sb.toString().trim();
        } catch (JSONException e) {
            return cleaned;
        }
    }

    private void maybeShowScoreToast(String reply) {
        String cleaned = reply == null ? "" : reply.trim();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceFirst("^```[a-zA-Z]*\\s*", "");
            cleaned = cleaned.replaceFirst("\\s*```$", "");
        }
        try {
            JSONObject json = new JSONObject(cleaned);
            int score = json.optInt("score", -1);
            if (score >= 0) {
                showMessage("Diem: " + score + "/10");
                updateMascotForScore(score);
            }
        } catch (JSONException ignored) {
        }
    }

    private void updateMascotForScore(int score) {
        if (imgAvatar == null) return;
        int resId = score >= 8 ? R.drawable.penguin_happy : R.drawable.penguin_sad;
        imgAvatar.setImageResource(resId);
        if (score >= 8) {
            playHappyAnimation();
        } else {
            playSadAnimation();
        }
    }

    private void playHappyAnimation() {
        imgAvatar.clearAnimation();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imgAvatar, View.SCALE_X, 1f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imgAvatar, View.SCALE_Y, 1f, 1.1f, 1f);
        ObjectAnimator rotate = ObjectAnimator.ofFloat(imgAvatar, View.ROTATION, -6f, 6f, 0f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY, rotate);
        set.setDuration(700);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }

    private void playSadAnimation() {
        imgAvatar.clearAnimation();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(imgAvatar, View.ALPHA, 1f, 0.7f, 1f);
        ObjectAnimator down = ObjectAnimator.ofFloat(imgAvatar, View.TRANSLATION_Y, 0f, 8f, 0f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(alpha, down);
        set.setDuration(700);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }

    private String valueOrDash(int value) {
        return value >= 0 ? String.valueOf(value) : "-";
    }

    private String firstOfArray(JSONArray array) {
        if (array == null || array.length() == 0)
            return "";
        String val = array.optString(0, "").trim();
        return val;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.speaking_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvQuestion = view.findViewById(R.id.textView11);
        tvPage = view.findViewById(R.id.tv_page_number);
        btnNext = view.findViewById(R.id.ic_next);
        btnPrev = view.findViewById(R.id.ic_prev);
        btnMic = view.findViewById(R.id.imageView22);
        imgAvatar = view.findViewById(R.id.imageView19);
        tvFeedback = view.findViewById(R.id.textView16);
        String topicId = getArguments().getString("TOPIC_ID");
        SpeakingViewModel vm = new ViewModelProvider(this).get(SpeakingViewModel.class);
        vm.getQuestions().observe(getViewLifecycleOwner(), list -> {
            updateUI(vm, list);
        });

        vm.getCurrentIndex().observe(getViewLifecycleOwner(), i -> {
            updateUI(vm, vm.getQuestions().getValue());
        });

        btnNext.setOnClickListener(v -> vm.next());
        btnPrev.setOnClickListener(v -> vm.prev());

        vm.loadQuestions(topicId);

        btnMic.setOnClickListener(v -> startSpeech());
    }

    private void updateUI(SpeakingViewModel vm,
            List<SpeakingQuestion> list) {
        if (list == null || list.isEmpty())
            return;

        int i = vm.getCurrentIndex().getValue();
        SpeakingQuestion q = list.get(i);

        tvQuestion.setText(q.text);
        tvPage.setText((i + 1) + "/" + list.size());
    }

    /**
     * Initialize all views
     */
    // private void initViews(View view) {
    // try {
    // // Navigation bar
    // tvPageNumber = view.findViewById(R.id.tv_page_number);
    // icPrev = view.findViewById(R.id.ic_prev);
    // icNext = view.findViewById(R.id.ic_next);
    //
    // // Content
    // tvQuestion = view.findViewById(R.id.textView11);
    // imgAvatar = view.findViewById(R.id.imageView19);
    //
    // // Tool bar
    // icBookmark = view.findViewById(R.id.imageView24);
    // icMicro = view.findViewById(R.id.imageView22);
    // icEars = view.findViewById(R.id.imageView23);
    //
    // Log.d(TAG, "All views initialized successfully");
    // } catch (Exception e) {
    // Log.e(TAG, "Error initializing views", e);
    // }
    // }

    /**
     * Setup click listeners
     */
    // private void setupListeners() {
    // try {
    // // Previous button
    // if (icPrev != null) {
    // icPrev.setOnClickListener(v -> onPreviousClicked());
    // }
    //
    // // Next button
    // if (icNext != null) {
    // icNext.setOnClickListener(v -> onNextClicked());
    // }
    //
    // // Bookmark button
    // if (icBookmark != null) {
    // icBookmark.setOnClickListener(v -> onBookmarkClicked());
    // }
    //
    // // Microphone button
    // if (icMicro != null) {
    // icMicro.setOnClickListener(v -> onMicrophoneClicked());
    // }
    //
    // // Listen button
    // if (icEars != null) {
    // icEars.setOnClickListener(v -> onListenClicked());
    // }
    //
    // Log.d(TAG, "All listeners setup successfully");
    // } catch (Exception e) {
    // Log.e(TAG, "Error setting up listeners", e);
    // }
    // }

    /**
     * Handle previous page click
     */
    // private void onPreviousClicked() {
    // try {
    // if (currentPage > 1) {
    // currentPage--;
    // updatePageDisplay();
    // showMessage("Page " + currentPage);
    // Log.d(TAG, "Moved to page: " + currentPage);
    // } else {
    // showMessage("This is the first page");
    // }
    // } catch (Exception e) {
    // Log.e(TAG, "Error handling previous click", e);
    // }
    // }
    //
    // /**
    // * Handle next page click
    // */
    // private void onNextClicked() {
    // try {
    // if (currentPage < totalPages) {
    // currentPage++;
    // updatePageDisplay();
    // showMessage("Page " + currentPage);
    // Log.d(TAG, "Moved to page: " + currentPage);
    // } else {
    // showMessage("This is the last page");
    // }
    // } catch (Exception e) {
    // Log.e(TAG, "Error handling next click", e);
    // }
    // }

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
        Log.d(TAG, "SpeakingTestFragment destroyed");

        // Clean up references
        tvPageNumber = null;
        tvQuestion = null;
        icPrev = null;
        icNext = null;
        imgAvatar = null;
        icBookmark = null;
        icMicro = null;
        icEars = null;
        tvFeedback = null;
    }
}

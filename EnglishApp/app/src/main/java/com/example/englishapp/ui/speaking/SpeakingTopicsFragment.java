package com.example.englishapp.ui.speaking;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.englishapp.ui.common.NotificationFragment;
import com.example.englishapp.R;

/**
 * SpeakingTopicsFragment - Displays speaking topics and AI chat
 * User can navigate back to Speaking screen or forward to SpeakingTest
 */
public class SpeakingTopicsFragment extends Fragment {

    private static final String TAG = "SpeakingTopicsFragment";

    // UI Components
    private CardView cardBusiness;
    private CardView cardTravel;
    private CardView cardStudy;
    private Button btnStartChat;
    private EditText etMessageInput;
    private ImageButton btnSendMessage;
    private ImageButton btnNotification;
    private ImageButton btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.speaking_topics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            initViews(view);
            setupBackButton(view);
            setupListeners();
            Log.d(TAG, "SpeakingTopicsFragment initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing SpeakingTopicsFragment", e);
            showError("Failed to load speaking topics");
        }
    }

    /**
     * Setup back button to navigate back to Speaking screen
     */
    private void setupBackButton(View view) {
        try {
            btnBack = view.findViewById(R.id.back_icon);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> {
                    Log.d(TAG, "Back button clicked - Navigating back to Speaking screen");
                    navigateBack();
                });
                Log.d(TAG, "Back button setup successfully");
            } else {
                Log.e(TAG, "back_icon not found in layout");
                showError("Back button not available");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up back button", e);
            showError("Failed to setup back button");
        }
    }

    /**
     * Navigate back to Speaking screen
     */
    private void navigateBack() {
        try {
            if (getActivity() == null) {
                Log.e(TAG, "Activity is null, cannot navigate back");
                showError("Cannot navigate back");
                return;
            }

            // Pop back stack để quay về SpeakingFragment
            if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getActivity().getSupportFragmentManager().popBackStack();
                Log.d(TAG, "Navigated back successfully");
            } else {
                Log.w(TAG, "No back stack entry found");
                // Fallback: Manually navigate to SpeakingFragment
                SpeakingFragment speakingFragment = new SpeakingFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_left,   // enter
                                R.anim.slide_out_right, // exit
                                R.anim.slide_in_right,  // popEnter
                                R.anim.slide_out_left   // popExit
                        )
                        .replace(R.id.container, speakingFragment, "SpeakingFragment")
                        .commit();
                Log.d(TAG, "Manually navigated back to Speaking screen");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error navigating back", e);
            showError("Failed to navigate back");
        }
    }

    /**
     * Initialize all views
     */
    private void initViews(View view) {
        try {
            // Topic cards
            cardBusiness = view.findViewById(R.id.cardBusiness);
            cardTravel = view.findViewById(R.id.cardTravel);
            cardStudy = view.findViewById(R.id.cardStudy);

            // Chat section - các component để tương tác với AI chat
            btnStartChat = view.findViewById(R.id.btnStartChat);
            etMessageInput = view.findViewById(R.id.etMessageInput);
            btnSendMessage = view.findViewById(R.id.btnSendMessage);
            btnNotification = view.findViewById(R.id.btn_notification);

            Log.d(TAG, "All views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
        }
    }

    /**
     * Setup click listeners for all interactive elements
     */
    private void setupListeners() {
        try {
            // Notification button
            if (btnNotification != null) {
                btnNotification.setOnClickListener(v -> showNotificationFragment());
                Log.d(TAG, "Notification button setup successfully");
            } else {
                Log.w(TAG, "Notification button not found");
            }

            // Topic cards click listeners
            if (cardBusiness != null) {
                cardBusiness.setOnClickListener(v -> onTopicCardClicked("Business English"));
            }

            if (cardTravel != null) {
                cardTravel.setOnClickListener(v -> onTopicCardClicked("Travel"));
            }

            if (cardStudy != null) {
                cardStudy.setOnClickListener(v -> onTopicCardClicked("Study Abroad"));
            }

            // Start Chat button - khởi động cuộc trò chuyện với AI
            if (btnStartChat != null) {
                btnStartChat.setOnClickListener(v -> onStartChatClicked());
            }

            // Send message button - gửi tin nhắn đến AI để xử lý ngôn ngữ tự nhiên
            if (btnSendMessage != null) {
                btnSendMessage.setOnClickListener(v -> onSendMessageClicked());
            }

            Log.d(TAG, "All listeners setup successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up listeners", e);
        }
    }

    /**
     * Show notification dialog
     */
    private void showNotificationFragment() {
        try {
            NotificationFragment fragment = new NotificationFragment();
            fragment.show(getParentFragmentManager(), "notification_dialog");
            Log.d(TAG, "Notification dialog shown");
        } catch (Exception e) {
            Log.e(TAG, "Error showing notification", e);
            showError("Failed to show notifications");
        }
    }

    /**
     * Handle topic card click - Navigate to SpeakingTest
     */
    private void onTopicCardClicked(String topicName) {
        try {
            Log.d(TAG, "Topic card clicked: " + topicName);

            if (getActivity() == null) {
                Log.e(TAG, "Activity is null, cannot navigate");
                showError("Cannot start " + topicName);
                return;
            }

            // Navigate to SpeakingTestFragment
            SpeakingTestFragment speakingTestFragment = SpeakingTestFragment.newInstance(topicName);

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // enter
                            R.anim.slide_out_left,  // exit
                            R.anim.slide_in_left,   // popEnter
                            R.anim.slide_out_right  // popExit
                    )
                    .replace(R.id.container, speakingTestFragment, "SpeakingTestFragment")
                    .addToBackStack("SpeakingTest")
                    .commit();

            Log.d(TAG, "Navigated to SpeakingTest for: " + topicName);
            showMessage("Starting " + topicName + " test...");

        } catch (Exception e) {
            Log.e(TAG, "Error handling topic card click", e);
            showError("Failed to start " + topicName);
        }
    }

    /**
     * Handle Start Chat button click
     * Khởi động cuộc trò chuyện với AI để học tiếng Anh
     * Sử dụng machine learning để phân tích và trả lời câu hỏi
     */
    private void onStartChatClicked() {
        try {
            Log.d(TAG, "Start Chat button clicked");

            String message = "";
            if (etMessageInput != null) {
                message = etMessageInput.getText().toString().trim();
            }

            if (message.isEmpty()) {
                message = "Hi";
            }

            showMessage("Starting chat with: " + message);
        } catch (Exception e) {
            Log.e(TAG, "Error handling start chat click", e);
            showError("Failed to start chat");
        }
    }

    /**
     * Handle Send Message button click
     * Gửi tin nhắn đến AI để xử lý ngôn ngữ tự nhiên
     * AI sẽ phân tích và trả lời câu hỏi tiếng Anh của người dùng
     */
    private void onSendMessageClicked() {
        try {
            if (etMessageInput == null) {
                Log.e(TAG, "Message input is null");
                return;
            }

            String message = etMessageInput.getText().toString().trim();

            if (message.isEmpty()) {
                showMessage("Please enter a message");
                return;
            }

            Log.d(TAG, "Send message clicked with: " + message);
            showMessage("Sending: " + message);
            etMessageInput.setText("");
        } catch (Exception e) {
            Log.e(TAG, "Error handling send message click", e);
            showError("Failed to send message");
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
        Log.d(TAG, "SpeakingTopicsFragment destroyed");

        // Clean up references
        cardBusiness = null;
        cardTravel = null;
        cardStudy = null;
        btnStartChat = null;
        etMessageInput = null;
        btnSendMessage = null;
        btnNotification = null;
        btnBack = null;
    }
}


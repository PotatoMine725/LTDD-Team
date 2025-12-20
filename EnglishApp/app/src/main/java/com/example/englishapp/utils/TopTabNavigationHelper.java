package com.example.englishapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.englishapp.Fragment.LessonFragment;
import com.example.englishapp.HomeActivity;
import com.example.englishapp.ListeningActivity;
import com.example.englishapp.ui.quiz.QuizActivity;
import com.example.englishapp.R;
import com.example.englishapp.SpeakingActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TopTabNavigationHelper {

    private static final String TAG = "TopTabNavigationHelper";

    public enum TabType {
        VOCABULARY,
        QUIZ,
        LISTENING,
        SPEAKING
    }

    private View rootView;
    private Context context;
    private FragmentManager fragmentManager;
    private int containerId;
    private TabType currentTab = TabType.VOCABULARY;

    // Lưu quiz type hiện tại (Vocabulary hoặc Listening)
    private String currentQuizType = "Vocabulary";

    // Flag để biết có đang ở QuizActivity không
    private boolean isInQuizMode = false;

    // 🔧 FIX #2: Flag để tránh recursive navigation
    private boolean isNavigating = false;

    // Tab containers
    private LinearLayout tabVocabularyContainer;
    private LinearLayout tabListeningContainer;
    private LinearLayout tabSpeakingContainer;
    private View tabQuizContainer;

    // Tab indicators
    private View vocabularyIndicator;
    private View listeningIndicator;
    private View speakingIndicator;
    private View quizIndicator;

    // Tab texts
    private TextView vocabularyText;
    private TextView listeningText;
    private TextView speakingText;
    private TextView quizText;

    // Colors
    private int colorSelected;
    private int colorUnselected;
    private int colorIndicator;

    // Callback listeners
    private OnTabSelectedListener listener;
    private OnQuizTypeSelectedListener quizListener;

    public interface OnTabSelectedListener {
        void onTabSelected(TabType tabType);
    }

    public interface OnQuizTypeSelectedListener {
        void onQuizTypeSelected(String quizType);
    }

    public TopTabNavigationHelper(View rootView, Context context, FragmentManager fragmentManager, int containerId) {
        this.rootView = rootView;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;

        initColors();

        try {
            initViews();
            setupListeners();
            setCurrentTab(TabType.VOCABULARY);
            Log.d(TAG, "TopTabNavigationHelper initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing TopTabNavigationHelper", e);
        }
    }

    public TopTabNavigationHelper(View rootView, Context context, FragmentManager fragmentManager) {
        this(rootView, context, fragmentManager, R.id.container);
    }

    /**
     * Constructor đặc biệt cho QuizActivity - không cần FragmentManager
     */
    public TopTabNavigationHelper(View rootView, Context context) {
        this.rootView = rootView;
        this.context = context;
        this.fragmentManager = null;
        this.containerId = 0;
        this.isInQuizMode = true;

        initColors();

        try {
            initViews();
            setupListenersForQuizMode();
            Log.d(TAG, "TopTabNavigationHelper initialized for Quiz mode");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing TopTabNavigationHelper", e);
        }
    }

    private void initColors() {
        try {
            colorSelected = ContextCompat.getColor(context, R.color.profile_accent_blue);
            colorUnselected = ContextCompat.getColor(context, R.color.black);
            colorIndicator = ContextCompat.getColor(context, R.color.profile_accent_blue);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing colors", e);
        }
    }

    private void initViews() {
        try {
            tabVocabularyContainer = rootView.findViewById(R.id.tab_vocabulary_container);
            tabListeningContainer = rootView.findViewById(R.id.tab_listening_container);
            tabSpeakingContainer = rootView.findViewById(R.id.tab_speaking_container);
            tabQuizContainer = rootView.findViewById(R.id.tab_quiz_container);

            vocabularyIndicator = rootView.findViewById(R.id.tab_vocabulary_indicator);
            listeningIndicator = rootView.findViewById(R.id.tab_listening_indicator);
            speakingIndicator = rootView.findViewById(R.id.tab_speaking_indicator);
            quizIndicator = rootView.findViewById(R.id.tab_quiz_indicator);

            vocabularyText = rootView.findViewById(R.id.tab_vocabulary_text);
            listeningText = rootView.findViewById(R.id.tab_listening_text);
            speakingText = rootView.findViewById(R.id.tab_speaking_text);
            quizText = rootView.findViewById(R.id.tab_quiz_text);

            Log.d(TAG, "Views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
        }
    }

    private void setupListeners() {
        try {
            if (tabVocabularyContainer != null) {
                tabVocabularyContainer.setOnClickListener(v -> selectTab(TabType.VOCABULARY));
            }

            if (tabListeningContainer != null) {
                tabListeningContainer.setOnClickListener(v -> selectTab(TabType.LISTENING));
            }

            if (tabSpeakingContainer != null) {
                tabSpeakingContainer.setOnClickListener(v -> selectTab(TabType.SPEAKING));
            }

            if (tabQuizContainer != null) {
                tabQuizContainer.setOnClickListener(v -> showQuizPopupMenu());
            }

            Log.d(TAG, "Listeners setup successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up listeners", e);
        }
    }

    /**
     * Setup listeners cho Quiz mode - các tab sẽ navigate khác nhau
     */
    private void setupListenersForQuizMode() {
        try {
            if (tabVocabularyContainer != null) {
                tabVocabularyContainer.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onTabSelected(TabType.VOCABULARY);
                    }
                });
            }

            if (tabListeningContainer != null) {
                tabListeningContainer.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onTabSelected(TabType.LISTENING);
                    }
                });
            }

            if (tabSpeakingContainer != null) {
                tabSpeakingContainer.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onTabSelected(TabType.SPEAKING);
                    }
                });
            }

            if (tabQuizContainer != null) {
                tabQuizContainer.setOnClickListener(v -> showQuizPopupMenuInQuizMode());
            }

            Log.d(TAG, "Listeners for Quiz mode setup successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up quiz mode listeners", e);
        }
    }

    public void selectTab(TabType tabType) {
        try {
            // 🔧 FIX #2: Kiểm tra flag để tránh recursive call
            if (isNavigating) {
                Log.d(TAG, "Navigation already in progress, ignoring selectTab");
                return;
            }

            Log.d(TAG, "Selecting tab: " + tabType);

            updateTabUI(tabType);

            if (listener != null) {
                listener.onTabSelected(tabType);
            }

            handleNavigation(tabType);
            currentTab = tabType;
        } catch (Exception e) {
            Log.e(TAG, "Error selecting tab", e);
            showError("Failed to switch tab");
        }
    }

    private void updateTabUI(TabType selectedTab) {
        try {
            resetAllTabs();

            switch (selectedTab) {
                case VOCABULARY:
                    setTabSelected(vocabularyText, vocabularyIndicator, tabVocabularyContainer);
                    updateQuizTabText("Quiz/Test");
                    break;
                case LISTENING:
                    setTabSelected(listeningText, listeningIndicator, tabListeningContainer);
                    updateQuizTabText("Quiz/Test");
                    break;
                case SPEAKING:
                    setTabSelected(speakingText, speakingIndicator, tabSpeakingContainer);
                    updateQuizTabText("Quiz/Test");
                    break;
                case QUIZ:
                    // Quiz tab không highlight, giữ nguyên
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating tab UI", e);
        }
    }

    private void resetAllTabs() {
        setTabUnselected(vocabularyText, vocabularyIndicator, tabVocabularyContainer);
        setTabUnselected(listeningText, listeningIndicator, tabListeningContainer);
        setTabUnselected(speakingText, speakingIndicator, tabSpeakingContainer);
    }

    private void setTabSelected(TextView textView, View indicator, View container) {
        try {
            if (textView != null) {
                textView.setTextColor(colorSelected);
                textView.setTypeface(null, Typeface.BOLD);
            }

            if (indicator != null) {
                indicator.setVisibility(View.VISIBLE);
                indicator.setBackgroundColor(colorIndicator);
            }

            if (container != null) {
                container.setAlpha(1.0f);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting tab selected", e);
        }
    }

    private void setTabUnselected(TextView textView, View indicator, View container) {
        try {
            if (textView != null) {
                textView.setTextColor(colorUnselected);
                textView.setTypeface(null, Typeface.NORMAL);
            }

            if (indicator != null) {
                indicator.setVisibility(View.INVISIBLE);
                indicator.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            }

            if (container != null) {
                container.setAlpha(0.6f);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting tab unselected", e);
        }
    }

    /**
     * 🔧 FIX #1 & #2: Handle navigation với logic mới - KHÔNG dùng Thread.sleep()
     * Sử dụng post() để đợi bottom nav update xong
     */
    private void handleNavigation(TabType tabType) {
        try {
            // 🔧 FIX #2: Set flag để tránh recursive call
            if (isNavigating) {
                Log.d(TAG, "Already navigating, skip");
                return;
            }

            // Check nếu context là HomeActivity
            if (context instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) context;
                BottomNavigationView bottomNav = homeActivity.findViewById(R.id.bottom_navigation);

                if (bottomNav != null) {
                    int selectedItemId = bottomNav.getSelectedItemId();

                    // Nếu KHÔNG đang ở tab Lesson → trigger bottom nav
                    if (selectedItemId != R.id.nav_lesson) {
                        Log.d(TAG, "Not on Lesson tab, triggering bottom navigation to Lesson");

                        // 🔧 FIX #2: Set flag TRƯỚC KHI trigger bottom nav
                        isNavigating = true;

                        // Set listener một lần để biết khi nào bottom nav đã switch xong
                        bottomNav.setOnItemSelectedListener(item -> {
                            if (item.getItemId() == R.id.nav_lesson) {
                                // 🔧 FIX #1: Dùng post() thay vì Thread.sleep()
                                // post() sẽ chạy sau khi bottom nav đã update xong UI
                                bottomNav.post(() -> {
                                    performTabNavigation(tabType);
                                    // Reset flag sau khi navigation xong
                                    isNavigating = false;
                                });

                                // Restore lại listener cũ (từ HomeActivity)
                                homeActivity.setupBottomNavigation();
                                return true;
                            }
                            return false;
                        });

                        // Trigger bottom nav switch
                        bottomNav.setSelectedItemId(R.id.nav_lesson);
                        return;
                    }
                }
            }

            // Nếu đã ở tab Lesson hoặc không phải HomeActivity → navigate bình thường
            performTabNavigation(tabType);

        } catch (Exception e) {
            Log.e(TAG, "Error handling navigation", e);
            isNavigating = false; // Reset flag nếu có lỗi
            showError("Navigation failed");
        }
    }

    /**
     * Thực hiện navigation thực sự giữa các tab
     */
    private void performTabNavigation(TabType tabType) {
        try {
            switch (tabType) {
                case VOCABULARY:
                    navigateToVocabulary();
                    break;
                case LISTENING:
                    navigateToListening();
                    break;
                case SPEAKING:
                    navigateToSpeaking();
                    break;
                case QUIZ:
                    // Quiz không navigate
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error performing tab navigation", e);
        }
    }

    private void navigateToVocabulary() {
        try {
            if (currentTab == TabType.VOCABULARY) {
                Log.d(TAG, "Already on Vocabulary tab");
                return;
            }

            Log.d(TAG, "Navigating to Vocabulary");
            LessonFragment fragment = new LessonFragment();
            replaceFragmentWithoutBackStack(fragment, "LessonFragment");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to vocabulary", e);
            showError("Failed to navigate to vocabulary");
        }
    }

    private void navigateToListening() {
        try {
            if (currentTab == TabType.LISTENING) {
                Log.d(TAG, "Already on Listening tab");
                return;
            }

            Log.d(TAG, "Navigating to Listening");
            ListeningActivity fragment = new ListeningActivity();
            replaceFragmentWithoutBackStack(fragment, "ListeningActivity");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to listening", e);
            showError("Failed to navigate to listening");
        }
    }

    private void navigateToSpeaking() {
        try {
            if (currentTab == TabType.SPEAKING) {
                Log.d(TAG, "Already on Speaking tab");
                return;
            }

            Log.d(TAG, "Navigating to Speaking");
            SpeakingActivity fragment = new SpeakingActivity();
            replaceFragmentWithoutBackStack(fragment, "SpeakingActivity");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to speaking", e);
            showError("Failed to navigate to speaking");
        }
    }

    /**
     * Show popup menu trong normal mode (khi không ở QuizActivity)
     */
    /**
     * Show popup menu trong normal mode (khi không ở QuizActivity)
     */
    private void showQuizPopupMenu() {
        try {
            if (tabQuizContainer == null) {
                Log.w(TAG, "Quiz container is null");
                return;
            }

            PopupMenu popupMenu = new PopupMenu(context, tabQuizContainer, Gravity.BOTTOM);
            popupMenu.getMenu().add(0, 1, 0, "Vocabulary");
            popupMenu.getMenu().add(0, 2, 1, "Listening");

            popupMenu.setOnMenuItemClickListener(item -> {
                try {
                    String quizType = "";
                    switch (item.getItemId()) {
                        case 1:
                            quizType = "Vocabulary";
                            break;
                        case 2:
                            quizType = "Listening";
                            break;
                    }

                    if (!quizType.isEmpty()) {
                        currentQuizType = quizType;
                        updateQuizTabText(quizType);

                        if (quizListener != null) {
                            quizListener.onQuizTypeSelected(quizType);
                        }

                        handleQuizTypeSelection(quizType);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error handling quiz menu item", e);
                    showError("Failed to select quiz type");
                }
                return true;
            });

            // 🔧 FIX: Khi dismiss popup menu, reset text về "Quiz/Test"
            popupMenu.setOnDismissListener(menu -> {
                // Chỉ reset nếu KHÔNG navigate đến QuizActivity
                // (nếu navigate rồi thì sẽ finish activity này)
                if (context instanceof Activity && !((Activity) context).isFinishing()) {
                    updateQuizTabText("Quiz/Test");
                }
            });

            popupMenu.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing quiz popup menu", e);
            showError("Failed to show quiz menu");
        }
    }

    /**
     * Show popup menu TRONG QuizActivity
     */
    private void showQuizPopupMenuInQuizMode() {
        try {
            if (tabQuizContainer == null) {
                Log.w(TAG, "Quiz container is null");
                return;
            }

            PopupMenu popupMenu = new PopupMenu(context, tabQuizContainer, Gravity.BOTTOM);
            popupMenu.getMenu().add(0, 1, 0, "Vocabulary");
            popupMenu.getMenu().add(0, 2, 1, "Listening");

            popupMenu.setOnMenuItemClickListener(item -> {
                try {
                    String quizType = "";
                    switch (item.getItemId()) {
                        case 1:
                            quizType = "Vocabulary";
                            break;
                        case 2:
                            quizType = "Listening";
                            break;
                    }

                    if (!quizType.isEmpty()) {
                        currentQuizType = quizType;
                        resetAllTabs();
                        highlightQuizTab();

                        if (quizListener != null) {
                            quizListener.onQuizTypeSelected(quizType);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error handling quiz menu item in quiz mode", e);
                    showError("Failed to select quiz type");
                }
                return true;
            });

            popupMenu.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing quiz popup menu in quiz mode", e);
            showError("Failed to show quiz menu");
        }
    }

    private void updateQuizTabText(String text) {
        try {
            if (quizText != null) {
                quizText.setText(text);
                Log.d(TAG, "Quiz tab text updated to: " + text);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating quiz tab text", e);
        }
    }

    private void handleQuizTypeSelection(String quizType) {
        try {
            Log.d(TAG, "Quiz type selected: " + quizType);

            Intent intent = new Intent(context, QuizActivity.class);
            intent.putExtra("QUIZ_TYPE", quizType);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error handling quiz type selection", e);
            Toast.makeText(context, "Selected: " + quizType, Toast.LENGTH_SHORT).show();
        }
    }

    private void replaceFragmentWithoutBackStack(Fragment fragment, String tag) {
        try {
            if (fragmentManager != null) {
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    Log.d(TAG, "Clearing back stack before tab switch");
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(containerId, fragment, tag);
                transaction.commit();

                Log.d(TAG, "Fragment replaced: " + tag);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error replacing fragment", e);
            showError("Failed to switch screen");
        }
    }

    public void setCurrentTab(TabType tabType) {
        try {
            currentTab = tabType;
            updateTabUI(tabType);
            Log.d(TAG, "Current tab set to: " + tabType);
        } catch (Exception e) {
            Log.e(TAG, "Error setting current tab", e);
        }
    }

    public void setupForQuizMode(String quizType) {
        try {
            this.currentQuizType = quizType;
            this.isInQuizMode = true;
            resetAllTabs();
            updateQuizTabText(quizType);
            highlightQuizTab();
            Log.d(TAG, "Setup for quiz mode with type: " + quizType);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up quiz mode", e);
        }
    }

    private void highlightQuizTab() {
        try {
            if (quizText != null) {
                quizText.setTextColor(colorSelected);
                quizText.setTypeface(null, Typeface.BOLD);
            }

            if (quizIndicator != null) {
                quizIndicator.setVisibility(View.VISIBLE);
                quizIndicator.setBackgroundColor(colorIndicator);
            }

            if (tabQuizContainer != null) {
                tabQuizContainer.setAlpha(1.0f);
            }

            Log.d(TAG, "Quiz/Test tab highlighted");
        } catch (Exception e) {
            Log.e(TAG, "Error highlighting quiz tab", e);
        }
    }

    // Getters
    public TabType getCurrentTab() {
        return currentTab;
    }

    public String getCurrentQuizType() {
        return currentQuizType;
    }

    // Setters for listeners
    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        this.listener = listener;
    }

    public void setOnQuizTypeSelectedListener(OnQuizTypeSelectedListener listener) {
        this.quizListener = listener;
    }

    public void setTabColors(int selectedColor, int unselectedColor, int indicatorColor) {
        this.colorSelected = selectedColor;
        this.colorUnselected = unselectedColor;
        this.colorIndicator = indicatorColor;
        updateTabUI(currentTab);
    }

    /**
     * 🆕 Cleanup method để tránh memory leak
     */
    public void cleanup() {
        listener = null;
        quizListener = null;
        isNavigating = false;
        Log.d(TAG, "TopTabNavigationHelper cleaned up");
    }

    /**
     * ✅ Reset quiz tab text về "Quiz/Test" (dùng khi về normal mode)
     */
    public void resetQuizTabText() {
        try {
            updateQuizTabText("Quiz/Test");
            this.currentQuizType = ""; // Clear quiz type
            this.isInQuizMode = false;
            Log.d(TAG, "Quiz tab text reset to 'Quiz/Test'");
        } catch (Exception e) {
            Log.e(TAG, "Error resetting quiz tab text", e);
        }
    }

    /**
     * ✅ Setup for normal mode (không phải quiz mode)
     */
    public void setupForNormalMode() {
        try {
            this.isInQuizMode = false;
            this.currentQuizType = "";
            resetQuizTabText();

            // Reset listeners về normal mode
            setupListeners();

            Log.d(TAG, "Setup for normal mode completed");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up normal mode", e);
        }
    }


    private void showError(String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
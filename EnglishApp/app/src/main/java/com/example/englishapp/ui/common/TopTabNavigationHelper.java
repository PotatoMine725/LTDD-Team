package com.example.englishapp.ui.common;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.FragmentManager;

import com.example.englishapp.R;
import com.example.englishapp.ui.common.tabnavigation.TabType;
import com.example.englishapp.ui.common.tabnavigation.TabUIHandler;
import com.example.englishapp.ui.common.tabnavigation.TabNavigationHandler;
import com.example.englishapp.ui.common.tabnavigation.QuizMenuHandler;

/**
 * Facade class để điều phối tất cả các handler cho top tab navigation
 * Phân tách logic thành các module riêng biệt để dễ bảo trì
 */
public class TopTabNavigationHelper {

    private static final String TAG = "TopTabNavigationHelper";

    /**
     * Listener để lắng nghe sự kiện chọn tab
     */
    public interface OnTabSelectedListener {
        void onTabSelected(com.example.englishapp.ui.common.tabnavigation.TabType tabType);
    }

    /**
     * Listener để lắng nghe sự kiện chọn quiz type
     */
    public interface OnQuizTypeSelectedListener {
        void onQuizTypeSelected(String quizType);
    }

    /**
     * @deprecated Sử dụng com.example.englishapp.ui.common.tabnavigation.TabType thay thế
     * Giữ lại để tương thích ngược
     * Inner enum này sẽ map với TabType từ package tabnavigation
     */
    @Deprecated
    public enum TabType {
        VOCABULARY,
        QUIZ,
        LISTENING,
        SPEAKING;

        /**
         * Convert sang TabType mới
         */
        public com.example.englishapp.ui.common.tabnavigation.TabType toNewTabType() {
            switch (this) {
                case VOCABULARY:
                    return com.example.englishapp.ui.common.tabnavigation.TabType.VOCABULARY;
                case QUIZ:
                    return com.example.englishapp.ui.common.tabnavigation.TabType.QUIZ;
                case LISTENING:
                    return com.example.englishapp.ui.common.tabnavigation.TabType.LISTENING;
                case SPEAKING:
                    return com.example.englishapp.ui.common.tabnavigation.TabType.SPEAKING;
                default:
                    return com.example.englishapp.ui.common.tabnavigation.TabType.VOCABULARY;
            }
        }

        /**
         * Convert từ TabType mới
         */
        public static TabType fromNewTabType(com.example.englishapp.ui.common.tabnavigation.TabType newType) {
            switch (newType) {
                case VOCABULARY:
                    return VOCABULARY;
                case QUIZ:
                    return QUIZ;
                case LISTENING:
                    return LISTENING;
                case SPEAKING:
                    return SPEAKING;
                default:
                    return VOCABULARY;
            }
        }
    }

    private View rootView;
    private Context context;
    private FragmentManager fragmentManager;
    private int containerId;
    private boolean isInQuizMode = false;

    // Handlers
    private TabUIHandler uiHandler;
    private TabNavigationHandler navigationHandler;
    private QuizMenuHandler quizMenuHandler;

    // Listeners
    private OnTabSelectedListener listener;
    private OnQuizTypeSelectedListener quizListener;

    /**
     * Constructor chính cho normal mode
     */
    public TopTabNavigationHelper(View rootView, Context context, FragmentManager fragmentManager, int containerId) {
        this.rootView = rootView;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
        this.isInQuizMode = false;

        try {
            initHandlers();
            setupListeners();
            setCurrentTab(com.example.englishapp.ui.common.tabnavigation.TabType.VOCABULARY);
            Log.d(TAG, "TopTabNavigationHelper initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing TopTabNavigationHelper", e);
        }
    }

    /**
     * Constructor với containerId mặc định
     */
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

        try {
            initHandlers();
            setupListenersForQuizMode();
            Log.d(TAG, "TopTabNavigationHelper initialized for Quiz mode");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing TopTabNavigationHelper", e);
        }
    }

    /**
     * Khởi tạo các handlers
     */
    private void initHandlers() {
        // UI Handler
        uiHandler = new TabUIHandler(rootView, context);

        // Navigation Handler
        if (fragmentManager != null) {
            navigationHandler = new TabNavigationHandler(context, fragmentManager, containerId, uiHandler);
        } else {
            navigationHandler = new TabNavigationHandler(context, uiHandler);
        }

        // Quiz Menu Handler
        View quizContainer = uiHandler.getTabQuizContainer();
        quizMenuHandler = new QuizMenuHandler(context, quizContainer, uiHandler, isInQuizMode);
    }

    /**
     * Setup listeners cho normal mode
     */
    private void setupListeners() {
        try {
            View vocabularyContainer = uiHandler.getTabVocabularyContainer();
            View listeningContainer = uiHandler.getTabListeningContainer();
            View speakingContainer = uiHandler.getTabSpeakingContainer();
            View quizContainer = uiHandler.getTabQuizContainer();

            if (vocabularyContainer != null) {
                vocabularyContainer.setOnClickListener(v -> selectTab(com.example.englishapp.ui.common.tabnavigation.TabType.VOCABULARY));
            }

            if (listeningContainer != null) {
                listeningContainer.setOnClickListener(v -> selectTab(com.example.englishapp.ui.common.tabnavigation.TabType.LISTENING));
            }

            if (speakingContainer != null) {
                speakingContainer.setOnClickListener(v -> selectTab(com.example.englishapp.ui.common.tabnavigation.TabType.SPEAKING));
            }

            if (quizContainer != null) {
                quizContainer.setOnClickListener(v -> showQuizPopupMenu());
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
            View vocabularyContainer = uiHandler.getTabVocabularyContainer();
            View listeningContainer = uiHandler.getTabListeningContainer();
            View speakingContainer = uiHandler.getTabSpeakingContainer();
            View quizContainer = uiHandler.getTabQuizContainer();

            if (vocabularyContainer != null) {
                vocabularyContainer.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onTabSelected(com.example.englishapp.ui.common.tabnavigation.TabType.VOCABULARY);
                    }
                });
            }

            if (listeningContainer != null) {
                listeningContainer.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onTabSelected(com.example.englishapp.ui.common.tabnavigation.TabType.LISTENING);
                    }
                });
            }

            if (speakingContainer != null) {
                speakingContainer.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onTabSelected(com.example.englishapp.ui.common.tabnavigation.TabType.SPEAKING);
                    }
                });
            }

            if (quizContainer != null) {
                quizContainer.setOnClickListener(v -> showQuizPopupMenu());
            }

            Log.d(TAG, "Listeners for Quiz mode setup successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up quiz mode listeners", e);
        }
    }

    /**
     * Select tab - public API (sử dụng TabType cũ để tương thích)
     */
    public void selectTab(TabType tabType) {
        if (navigationHandler != null) {
            navigationHandler.selectTab(tabType.toNewTabType());
        }
    }

    /**
     * Select tab - public API (sử dụng TabType mới)
     */
    public void selectTab(com.example.englishapp.ui.common.tabnavigation.TabType tabType) {
        if (navigationHandler != null) {
            navigationHandler.selectTab(tabType);
        }
    }

    /**
     * Show quiz popup menu
     */
    public void showQuizPopupMenu() {
        if (quizMenuHandler != null) {
            quizMenuHandler.showQuizPopupMenu();
        }
    }

    /**
     * Set current tab (sử dụng TabType cũ để tương thích)
     */
    public void setCurrentTab(TabType tabType) {
        if (navigationHandler != null) {
            navigationHandler.setCurrentTab(tabType.toNewTabType());
        }
    }

    /**
     * Set current tab (sử dụng TabType mới)
     */
    public void setCurrentTab(com.example.englishapp.ui.common.tabnavigation.TabType tabType) {
        if (navigationHandler != null) {
            navigationHandler.setCurrentTab(tabType);
        }
    }

    /**
     * Get current tab (trả về TabType cũ để tương thích)
     */
    public TabType getCurrentTab() {
        if (navigationHandler != null) {
            return TabType.fromNewTabType(navigationHandler.getCurrentTab());
        }
        return TabType.VOCABULARY;
    }

    /**
     * Get current tab (trả về TabType mới)
     */
    public com.example.englishapp.ui.common.tabnavigation.TabType getCurrentTabNew() {
        if (navigationHandler != null) {
            return navigationHandler.getCurrentTab();
        }
        return com.example.englishapp.ui.common.tabnavigation.TabType.VOCABULARY;
    }

    /**
     * Setup for quiz mode
     */
    public void setupForQuizMode(String quizType) {
        try {
            this.isInQuizMode = true;
            if (quizMenuHandler != null) {
                quizMenuHandler.setCurrentQuizType(quizType);
                quizMenuHandler.setInQuizMode(true);
            }
            if (uiHandler != null) {
                uiHandler.resetAllTabs();
                uiHandler.updateQuizTabText(quizType);
                uiHandler.highlightQuizTab();
            }
            Log.d(TAG, "Setup for quiz mode with type: " + quizType);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up quiz mode", e);
        }
    }

    /**
     * Reset quiz tab text về "Quiz/Test"
     */
    public void resetQuizTabText() {
        try {
            if (uiHandler != null) {
                uiHandler.updateQuizTabText("Quiz/Test");
            }
            if (quizMenuHandler != null) {
                quizMenuHandler.setCurrentQuizType("");
            }
            this.isInQuizMode = false;
            Log.d(TAG, "Quiz tab text reset to 'Quiz/Test'");
        } catch (Exception e) {
            Log.e(TAG, "Error resetting quiz tab text", e);
        }
    }

    /**
     * Setup for normal mode
     */
    public void setupForNormalMode() {
        try {
            this.isInQuizMode = false;
            if (quizMenuHandler != null) {
                quizMenuHandler.setCurrentQuizType("");
                quizMenuHandler.setInQuizMode(false);
            }
            resetQuizTabText();
            setupListeners();
            Log.d(TAG, "Setup for normal mode completed");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up normal mode", e);
        }
    }

    /**
     * Get current quiz type
     */
    public String getCurrentQuizType() {
        if (quizMenuHandler != null) {
            return quizMenuHandler.getCurrentQuizType();
        }
        return "";
    }

    /**
     * Set tab colors
     */
    public void setTabColors(int selectedColor, int unselectedColor, int indicatorColor) {
        if (uiHandler != null) {
            uiHandler.setTabColors(selectedColor, unselectedColor, indicatorColor);
        }
    }

    /**
     * Set listeners
     */
    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        this.listener = listener;
        if (navigationHandler != null) {
            navigationHandler.setOnTabSelectedListener(listener);
        }
    }

    public void setOnQuizTypeSelectedListener(OnQuizTypeSelectedListener listener) {
        this.quizListener = listener;
        if (quizMenuHandler != null) {
            quizMenuHandler.setOnQuizTypeSelectedListener(listener);
        }
    }

    /**
     * Cleanup method để tránh memory leak
     */
    public void cleanup() {
        listener = null;
        quizListener = null;
        if (navigationHandler != null) {
            navigationHandler.resetNavigationFlag();
        }
        Log.d(TAG, "TopTabNavigationHelper cleaned up");
    }
}

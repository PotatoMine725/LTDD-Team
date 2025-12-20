package com.example.englishapp.ui.common.tabnavigation;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.englishapp.R;

/**
 * Handler quản lý UI của các tab (views, colors, indicators)
 */
public class TabUIHandler {
    private static final String TAG = "TabUIHandler";

    private View rootView;
    private Context context;

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

    public TabUIHandler(View rootView, Context context) {
        this.rootView = rootView;
        this.context = context;
        initColors();
        initViews();
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

    public void updateTabUI(TabType selectedTab) {
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

    public void resetAllTabs() {
        setTabUnselected(vocabularyText, vocabularyIndicator, tabVocabularyContainer);
        setTabUnselected(listeningText, listeningIndicator, tabListeningContainer);
        setTabUnselected(speakingText, speakingIndicator, tabSpeakingContainer);
    }

    public void highlightQuizTab() {
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

    public void updateQuizTabText(String text) {
        try {
            if (quizText != null) {
                quizText.setText(text);
                Log.d(TAG, "Quiz tab text updated to: " + text);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating quiz tab text", e);
        }
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

    // Getters for containers (used by other handlers)
    public LinearLayout getTabVocabularyContainer() {
        return tabVocabularyContainer;
    }

    public LinearLayout getTabListeningContainer() {
        return tabListeningContainer;
    }

    public LinearLayout getTabSpeakingContainer() {
        return tabSpeakingContainer;
    }

    public View getTabQuizContainer() {
        return tabQuizContainer;
    }

    public void setTabColors(int selectedColor, int unselectedColor, int indicatorColor) {
        this.colorSelected = selectedColor;
        this.colorUnselected = unselectedColor;
        this.colorIndicator = indicatorColor;
    }
}


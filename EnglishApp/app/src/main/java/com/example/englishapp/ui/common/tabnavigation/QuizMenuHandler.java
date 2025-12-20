package com.example.englishapp.ui.common.tabnavigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.englishapp.ui.quiz.QuizActivity;
import com.example.englishapp.ui.common.TopTabNavigationHelper;

/**
 * Handler xử lý Quiz popup menu
 */
public class QuizMenuHandler {
    private static final String TAG = "QuizMenuHandler";

    private Context context;
    private View quizContainer;
    private TabUIHandler uiHandler;
    private TopTabNavigationHelper.OnQuizTypeSelectedListener quizListener;
    private String currentQuizType = "Vocabulary";
    private boolean isInQuizMode;

    public QuizMenuHandler(Context context, View quizContainer, TabUIHandler uiHandler, boolean isInQuizMode) {
        this.context = context;
        this.quizContainer = quizContainer;
        this.uiHandler = uiHandler;
        this.isInQuizMode = isInQuizMode;
    }

    public void showQuizPopupMenu() {
        if (isInQuizMode) {
            showQuizPopupMenuInQuizMode();
        } else {
            showQuizPopupMenuNormalMode();
        }
    }

    /**
     * Show popup menu trong normal mode (khi không ở QuizActivity)
     */
    private void showQuizPopupMenuNormalMode() {
        try {
            if (quizContainer == null) {
                Log.w(TAG, "Quiz container is null");
                return;
            }

            PopupMenu popupMenu = new PopupMenu(context, quizContainer, Gravity.BOTTOM);
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
                        uiHandler.updateQuizTabText(quizType);

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

            // Khi dismiss popup menu, reset text về "Quiz/Test"
            popupMenu.setOnDismissListener(menu -> {
                // Chỉ reset nếu KHÔNG navigate đến QuizActivity
                if (context instanceof Activity && !((Activity) context).isFinishing()) {
                    uiHandler.updateQuizTabText("Quiz/Test");
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
            if (quizContainer == null) {
                Log.w(TAG, "Quiz container is null");
                return;
            }

            PopupMenu popupMenu = new PopupMenu(context, quizContainer, Gravity.BOTTOM);
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
                        uiHandler.resetAllTabs();
                        uiHandler.highlightQuizTab();

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

    public void setOnQuizTypeSelectedListener(TopTabNavigationHelper.OnQuizTypeSelectedListener listener) {
        this.quizListener = listener;
    }

    public String getCurrentQuizType() {
        return currentQuizType;
    }

    public void setCurrentQuizType(String quizType) {
        this.currentQuizType = quizType;
    }

    public void setInQuizMode(boolean inQuizMode) {
        this.isInQuizMode = inQuizMode;
    }

    private void showError(String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}


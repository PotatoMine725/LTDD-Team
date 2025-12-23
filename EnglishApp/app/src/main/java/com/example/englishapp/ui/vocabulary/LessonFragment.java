package com.example.englishapp.ui.vocabulary;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.englishapp.R;
import com.example.englishapp.ui.common.NotificationFragment;
import com.example.englishapp.ui.common.TopTabNavigationHelper;

public class LessonFragment extends Fragment {

    private static final String TAG = "LessonFragment";
    private TopTabNavigationHelper tabNavigationHelper;

    public LessonFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            return inflater.inflate(R.layout.vocabulary_layout, container, false);
        } catch (Exception e) {
            Log.e(TAG, "Error inflating layout", e);
            showError("Failed to load lesson screen");
            return null;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            setupTabNavigation(view);
            setupNotificationButton(view);
            setupCardViewListeners(view);
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated", e);
            showError("Failed to initialize lesson screen");
        }
    }

    /**
     * Setup tab navigation helper
     */
    private void setupTabNavigation(View view) {
        try {
            tabNavigationHelper = new TopTabNavigationHelper(
                    view,
                    requireContext(),
                    getParentFragmentManager()
            );

            //Reset quiz tab text về "Quiz/Test"
            tabNavigationHelper.resetQuizTabText();

            // Set tab hiện tại là Vocabulary
            if (tabNavigationHelper.getCurrentTab() == null) {
                tabNavigationHelper.setCurrentTab(TopTabNavigationHelper.TabType.VOCABULARY);
            }

            // Lắng nghe sự kiện chọn tab
            tabNavigationHelper.setOnTabSelectedListener(tabType -> {
                Log.d(TAG, "Tab selected: " + tabType);
            });

            Log.d(TAG, "Tab navigation setup successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up tab navigation", e);
        }
    }

    /**
     * Setup notification button
     */
    private void setupNotificationButton(View view) {
        try {
            ImageView btnNotification = view.findViewById(R.id.btn_notification);
            if (btnNotification != null) {
                btnNotification.setOnClickListener(v -> showNotificationFragment());
                Log.d(TAG, "Notification button setup successfully");
            } else {
                Log.w(TAG, "Notification button not found");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up notification button", e);
        }
    }

    /**
     * Setup CardView listeners for vocabulary topics
     */
    private void setupCardViewListeners(View view) {
        try {
            View scrollView = view.findViewById(R.id.scrollView2);
            if (scrollView == null) {
                Log.e(TAG, "ScrollView not found!");
                return;
            }

            assignCardClickListeners(scrollView);
            Log.d(TAG, "CardView listeners setup successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up card view listeners", e);
        }
    }

    /**
     * Recursively assign click listeners to all CardViews
     */
    private void assignCardClickListeners(View parent) {
        if (!(parent instanceof ViewGroup)) return;

        ViewGroup viewGroup = (ViewGroup) parent;

        try {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);

                if (child instanceof CardView) {
                    CardView cardView = (CardView) child;
                    String topicTitle = getTopicTitleFromCard(cardView);

                    cardView.setOnClickListener(v -> {
                        Log.d(TAG, "CardView clicked: " + topicTitle);
                        navigateToFlashcard(topicTitle);
                    });

                    Log.d(TAG, "Assigned click listener for CardView: " + topicTitle);
                } else if (child instanceof ViewGroup) {
                    // Tìm cardview trong viewgroup
                    assignCardClickListeners(child);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error assigning card click listeners", e);
        }
    }

    /**
     * Get topic title from CardView
     */
    private String getTopicTitleFromCard(CardView cardView) {
        try {
            String title = findTextInView(cardView);
            return (title != null && !title.isEmpty()) ? title : "Unknown Topic";
        } catch (Exception e) {
            Log.e(TAG, "Error getting topic title", e);
            return "Unknown Topic";
        }
    }

    /**
     * Recursively find text in view hierarchy
     */
    private String findTextInView(View view) {
        try {
            if (view instanceof android.widget.TextView) {
                String text = ((android.widget.TextView) view).getText().toString();
                if (!text.isEmpty() && !text.matches("\\d+.*")) { // Skip numbers
                    return text;
                }
            }

            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    String result = findTextInView(viewGroup.getChildAt(i));
                    if (result != null && !result.isEmpty()) {
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding text in view", e);
        }

        return "Unknown Topic";
    }

    /**
     * Navigate to Flashcard fragment
     */
    private void navigateToFlashcard(String topicTitle) {
        try {
            Log.d(TAG, "Navigating to flashcard: " + topicTitle);

            if (getActivity() == null) {
                Log.e(TAG, "Activity is null, cannot navigate");
                showError("Cannot open flashcard");
                return;
            }

            FlashcardFragment flashcardFragment = FlashcardFragment.newInstance(topicTitle);

            // Use Activity's FragmentManager for navigation
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // enter
                            R.anim.slide_out_left,  // exit
                            R.anim.slide_in_left,   // popEnter
                            R.anim.slide_out_right  // popExit
                    )
                    .replace(R.id.container, flashcardFragment, "FlashcardFragment")
                    .addToBackStack("VocabularyToFlashcard")
                    .commit();

            Log.d(TAG, "Fragment transaction committed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to flashcard", e);
            showError("Failed to open flashcard");
        }
    }

    /**
     * Show notification dialog
     */
    private void showNotificationFragment() {
        try {
            NotificationFragment fragment = new NotificationFragment();
            FragmentManager fragmentManager = getParentFragmentManager();
            fragment.show(fragmentManager, "notification_dialog");
            Log.d(TAG, "Notification dialog shown");
        } catch (Exception e) {
            Log.e(TAG, "Error showing notification", e);
            showError("Failed to show notifications");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "LessonFragment destroyed");

        // Clean up references
        tabNavigationHelper = null;
    }
}
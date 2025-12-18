package com.example.englishapp.ui.listening;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.data.model.ListeningTopic;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ListeningTopicActivity extends Fragment {

    private static final String TAG = "ListeningTopicActivity";
    private RecyclerView topicsRecyclerView;
    private ListeningTopicAdapter topicAdapter;
    private BottomNavigationView bottomNavigationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            return inflater.inflate(R.layout.listening_topic, container, false);
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
            setupRecyclerView(view);
            setupBottomNavigation(view);
            loadTopicsData();
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewCreated", e);
            showError("Failed to initialize screen");
        }
    }

    /**
     * Setup back button để quay về ListeningActivity
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
                showError("Back button not available");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up back button", e);
            showError("Failed to setup back button");
        }
    }

    /**
     * Setup refresh button
     */
    private void setupRefreshButton(View view) {
        try {
            ImageView refreshIcon = view.findViewById(R.id.refresh_icon);
            if (refreshIcon != null) {
                refreshIcon.setOnClickListener(v -> {
                    Log.d(TAG, "Refresh button clicked");
                    refreshTopics();
                });
                Log.d(TAG, "Refresh button setup successfully");
            } else {
                Log.e(TAG, "refresh_icon not found in layout");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up refresh button", e);
        }
    }

    /**
     * Setup RecyclerView for topics
     */
    private void setupRecyclerView(View view) {
        try {
            topicsRecyclerView = view.findViewById(R.id.topics_recycler_view);
            if (topicsRecyclerView != null) {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
                topicsRecyclerView.setLayoutManager(gridLayoutManager);

                topicAdapter = new ListeningTopicAdapter(new ArrayList<>());
                topicsRecyclerView.setAdapter(topicAdapter);

                topicAdapter.setOnTopicClickListener((topic, position) -> {
                    Log.d(TAG, "Topic clicked: " + topic.getTopicName() + " at position " + position);
                    navigateToTopicLessons(topic);
                });

                // Click listener riêng cho progress text để chuyển sang exercise
                topicAdapter.setOnProgressClickListener((topic, position) -> {
                    Log.d(TAG, "Progress clicked: " + topic.getTopicName() + " at position " + position);
                    navigateToExercise(topic);
                });

                Log.d(TAG, "RecyclerView setup completed");
            } else {
                Log.e(TAG, "topics_recycler_view not found in layout");
                showError("Failed to load topics list");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView", e);
            showError("Failed to initialize topics list");
        }
    }

    /**
     * Load topics data
     */
    private void loadTopicsData() {
        try {
            List<ListeningTopic> topics = getSampleTopics();

            if (topicAdapter != null) {
                topicAdapter.updateData(topics);
                Log.d(TAG, "Loaded " + topics.size() + " topics");
            } else {
                Log.e(TAG, "Topic adapter is null");
                showError("Failed to load topics");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading topics data", e);
            showError("Failed to load topics");
        }
    }

    /**
     * Get sample topics data
     */
    private List<ListeningTopic> getSampleTopics() {
        List<ListeningTopic> topics = new ArrayList<>();

        try {
            int defaultImage = R.drawable.topic_technology;

            topics.add(new ListeningTopic("Electric Vehicles",
                    getImageResourceOrDefault("topic_electric_vehicles", defaultImage), 0, 5));
            topics.add(new ListeningTopic("Technology",
                    getImageResourceOrDefault("topic_technology", defaultImage), 2, 5));
            topics.add(new ListeningTopic("Mental Health",
                    getImageResourceOrDefault("topic_mental_health", defaultImage), 1, 5));
            topics.add(new ListeningTopic("Energy",
                    getImageResourceOrDefault("topic_energy", defaultImage), 0, 5));
            topics.add(new ListeningTopic("Environment",
                    getImageResourceOrDefault("topic_environment", defaultImage), 3, 5));
            topics.add(new ListeningTopic("Education",
                    getImageResourceOrDefault("topic_education", defaultImage), 0, 5));
            topics.add(new ListeningTopic("Business",
                    getImageResourceOrDefault("topic_business", defaultImage), 1, 5));
            topics.add(new ListeningTopic("Travel",
                    getImageResourceOrDefault("topic_travel", defaultImage), 0, 5));
            topics.add(new ListeningTopic("Food & Culture",
                    getImageResourceOrDefault("topic_food", defaultImage), 2, 5));
            topics.add(new ListeningTopic("Sports",
                    getImageResourceOrDefault("topic_sports", defaultImage), 0, 5));
        } catch (Exception e) {
            Log.e(TAG, "Error creating sample topics", e);
        }

        return topics;
    }

    /**
     * Get image resource ID or return default
     */
    private int getImageResourceOrDefault(String resourceName, int defaultResource) {
        try {
            int resourceId = getResources().getIdentifier(
                    resourceName, "drawable", requireContext().getPackageName());
            return resourceId != 0 ? resourceId : defaultResource;
        } catch (Exception e) {
            Log.w(TAG, "Image resource not found: " + resourceName, e);
            return defaultResource;
        }
    }

    /**
     * Setup Bottom Navigation
     */
    private void setupBottomNavigation(View view) {
        try {
            bottomNavigationView = view.findViewById(R.id.bottom_navigation);

            if (bottomNavigationView != null) {
                bottomNavigationView.setSelectedItemId(R.id.nav_lesson);

                bottomNavigationView.setOnItemSelectedListener(item -> {
                    try {
                        int itemId = item.getItemId();

                        if (itemId == R.id.nav_home) {
                            navigateToHome();
                            return true;
                        } else if (itemId == R.id.nav_lesson) {
                            navigateBack();
                            return true;
                        } else if (itemId == R.id.nav_statistics) {
                            navigateToStatistics();
                            return true;
                        } else if (itemId == R.id.nav_profile) {
                            navigateToProfile();
                            return true;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error handling navigation item click", e);
                        showError("Navigation failed");
                    }
                    return false;
                });

                Log.d(TAG, "Bottom navigation setup completed");
            } else {
                Log.e(TAG, "bottom_navigation not found in layout");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up bottom navigation", e);
        }
    }

    /**
     * Navigate back to ListeningActivity
     */
    private void navigateBack() {
        try {
            if (getActivity() != null) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                    Log.d(TAG, "Navigated back to ListeningActivity");
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
     * Refresh topics list
     */
    private void refreshTopics() {
        try {
            Log.d(TAG, "Refreshing topics...");
            loadTopicsData();
            showMessage("Topics refreshed");
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing topics", e);
            showError("Failed to refresh topics");
        }
    }

    /**
     * Navigate to topic lessons
     */
    private void navigateToTopicLessons(ListeningTopic topic) {
        try {
            if (getActivity() == null) {
                Log.e(TAG, "Activity is null, cannot navigate");
                showError("Cannot open topic");
                return;
            }

            Log.d(TAG, "Navigating to lessons for topic: " + topic.getTopicName());
            showMessage("Opening " + topic.getTopicName());

            // TODO: Implement navigation to lessons fragment

        } catch (Exception e) {
            Log.e(TAG, "Error navigating to topic lessons", e);
            showError("Failed to open topic");
        }
    }

    /**
     * Navigate to Home
     */
    private void navigateToHome() {
        try {
            if (getActivity() == null) {
                showError("Cannot navigate to home");
                return;
            }

            getActivity().getSupportFragmentManager().popBackStack(null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);

            Log.d(TAG, "Navigated to Home");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to Home", e);
            showError("Failed to navigate to home");
        }
    }

    /**
     * Navigate to Statistics
     */
    private void navigateToStatistics() {
        try {
            if (getActivity() == null) {
                showError("Cannot navigate to statistics");
                return;
            }

            // TODO: Implement statistics fragment
            showMessage("Statistics - Coming soon");
            Log.d(TAG, "Statistics navigation requested");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to Statistics", e);
            showError("Failed to navigate to statistics");
        }
    }

    /**
     * Navigate to Profile
     */
    private void navigateToProfile() {
        try {
            if (getActivity() == null) {
                showError("Cannot navigate to profile");
                return;
            }

            // TODO: Implement profile fragment
            showMessage("Profile - Coming soon");
            Log.d(TAG, "Profile navigation requested");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to Profile", e);
            showError("Failed to navigate to profile");
        }
    }

    /**
     * Navigate to exercise screen (khi click vào progress text)
     */
    private void navigateToExercise(ListeningTopic topic) {
        try {
            if (getActivity() == null) {
                Log.e(TAG, "Activity is null, cannot navigate to exercise");
                showError("Cannot open exercise");
                return;
            }

            Log.d(TAG, "Navigating to exercise for topic: " + topic.getTopicName());

            // Tạo ListeningExerciseActivity fragment với topic name
            ListeningExerciseActivity exerciseFragment =
                    ListeningExerciseActivity.newInstance(topic.getTopicName());

            // Navigate với animation
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // enter
                            R.anim.slide_out_left,  // exit
                            R.anim.slide_in_left,   // popEnter
                            R.anim.slide_out_right  // popExit
                    )
                    .replace(R.id.container, exerciseFragment, "ListeningExerciseActivity")
                    .addToBackStack("TopicToExercise")
                    .commit();

            Log.d(TAG, "Fragment transaction to ListeningExerciseActivity committed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to exercise", e);
            showError("Failed to open exercise");
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
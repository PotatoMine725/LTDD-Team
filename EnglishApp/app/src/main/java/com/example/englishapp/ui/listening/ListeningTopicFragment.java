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
import com.example.englishapp.adapter.ListeningTopicAdapter;
import com.example.englishapp.model.ListeningTopic;
import com.example.englishapp.service.FirebaseService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListeningTopicFragment extends Fragment {

    private static final String TAG = "ListeningTopicFragment";
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
     * Setup back button để quay về ListeningFragment
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
     * Load topics data từ Firebase
     */
    private void loadTopicsData() {
        try {
            Log.d(TAG, "Loading topics from Firebase...");
            
            FirebaseService firebaseService = FirebaseService.getInstance();
            
            // Debug: Kiểm tra Firebase reference
            Log.d(TAG, "Firebase reference: " + firebaseService.getListeningTopicsRef().toString());
            
            firebaseService.getListeningTopicsRef().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(TAG, "Firebase onDataChange called. Snapshot exists: " + snapshot.exists());
                    Log.d(TAG, "Snapshot children count: " + snapshot.getChildrenCount());
                    
                    List<ListeningTopic> topics = new ArrayList<>();
                    
                    if (snapshot.exists()) {
                        for (DataSnapshot topicSnapshot : snapshot.getChildren()) {
                            try {
                                String topicId = topicSnapshot.getKey();
                                
                                // Debug: In ra tất cả các field có trong topic
                                Log.d(TAG, "=== Topic Debug: " + topicId + " ===");
                                for (DataSnapshot child : topicSnapshot.getChildren()) {
                                    Log.d(TAG, "Field: " + child.getKey() + " = " + child.getValue());
                                }
                                Log.d(TAG, "=== End Topic Debug ===");
                                
                                String topicName = topicSnapshot.child("name").getValue(String.class);
                                
                                // Thử nhiều tên field khác nhau cho image URL
                                String imageUrl = topicSnapshot.child("image_url").getValue(String.class);
                                if (imageUrl == null) {
                                    imageUrl = topicSnapshot.child("imageUrl").getValue(String.class);
                                }
                                if (imageUrl == null) {
                                    imageUrl = topicSnapshot.child("imageURL").getValue(String.class);
                                }
                                if (imageUrl == null) {
                                    imageUrl = topicSnapshot.child("image").getValue(String.class);
                                }
                                
                                Integer totalLessons = topicSnapshot.child("total_lessons").getValue(Integer.class);
                                
                                Log.d(TAG, "Processing topic: " + topicId + ", name: " + topicName + ", imageUrl: '" + imageUrl + "'");
                                Log.d(TAG, "ImageUrl is null: " + (imageUrl == null));
                                Log.d(TAG, "ImageUrl is empty: " + (imageUrl != null && imageUrl.trim().isEmpty()));
                                
                                if (topicId != null && topicName != null) {
                                    // Đảm bảo imageUrl không null và không rỗng
                                    String finalImageUrl = (imageUrl != null && !imageUrl.trim().isEmpty()) ? imageUrl.trim() : "";
                                    
                                    // Nếu không có imageUrl từ Firebase, sử dụng URL mặc định dựa trên topicId
                                    if (finalImageUrl.isEmpty()) {
                                        Log.w(TAG, "No image_url found for topic: " + topicId + ", using default URL");
                                        finalImageUrl = getDefaultImageUrl(topicId);
                                    }
                                    
                                    ListeningTopic topic = new ListeningTopic(
                                        topicId,
                                        topicName,
                                        finalImageUrl,
                                        0, // currentProgress
                                        totalLessons != null ? totalLessons : 0
                                    );
                                    topics.add(topic);
                                    Log.d(TAG, "Added topic: " + topicName + " with final image: " + finalImageUrl);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing topic data", e);
                            }
                        }
                    } else {
                        Log.w(TAG, "No topics found in Firebase");
                        // Fallback to sample data
                        topics = getSampleTopics();
                    }

                    if (topicAdapter != null) {
                        topicAdapter.updateData(topics);
                        Log.d(TAG, "Updated adapter with " + topics.size() + " topics from Firebase");
                    } else {
                        Log.e(TAG, "Topic adapter is null");
                        showError("Failed to load topics");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Firebase Error loading topics: " + error.getMessage());
                    Log.e(TAG, "Firebase Error details: " + error.getDetails());
                    Log.e(TAG, "Firebase Error code: " + error.getCode());
                    
                    // Fallback to sample data
                    List<ListeningTopic> topics = getSampleTopics();
                    if (topicAdapter != null) {
                        topicAdapter.updateData(topics);
                        Log.d(TAG, "Using fallback sample topics");
                    }
                    showError("Lỗi tải dữ liệu từ Firebase, sử dụng dữ liệu mẫu");
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading topics data", e);
            showError("Failed to load topics");
        }
    }

    /**
     * Get default image URL based on topic ID
     */
    private String getDefaultImageUrl(String topicId) {
        switch (topicId) {
            case "lt_daily":
                return "https://images.unsplash.com/photo-1506784983877-45594efa4cbe";
            case "lt_technology":
                // Sử dụng URL ảnh technology đáng tin cậy
                return "https://cdn.pixabay.com/photo/2018/05/08/08/44/artificial-intelligence-3382507_1280.jpg";
            default:
                return "https://images.unsplash.com/photo-1506784983877-45594efa4cbe";
        }
    }

    /**
     * Get sample topics data - fallback nếu Firebase không hoạt động
     */
    private List<ListeningTopic> getSampleTopics() {
        List<ListeningTopic> topics = new ArrayList<>();

        try {
            // Sử dụng imageUrl thay vì resource ID
            topics.add(new ListeningTopic("lt_daily", "Daily Life",
                    "https://images.unsplash.com/photo-1506784983877-45594efa4cbe", 0, 5));
            topics.add(new ListeningTopic("lt_technology", "Technology",
                    "https://images.unsplash.com/photo-1518709268805-4e9042af2176", 0, 5));
                    
        } catch (Exception e) {
            Log.e(TAG, "Error creating sample topics", e);
        }

        return topics;
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
     * Navigate back to ListeningFragment
     */
    private void navigateBack() {
        try {
            if (getActivity() != null) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                    Log.d(TAG, "Navigated back to ListeningFragment");
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
            
            // Lấy topicId từ topic object
            String topicId = topic.getTopicId();
            if (topicId == null || topicId.isEmpty()) {
                // Fallback: tạo topicId từ tên topic
                topicId = "lt_" + topic.getTopicName().toLowerCase().replace(" ", "_");
                Log.w(TAG, "Topic ID not found, using generated ID: " + topicId);
            }

            // Navigate đến ListeningLessonsFragment
            ListeningLessonsFragment lessonsFragment =
                    ListeningLessonsFragment.newInstance(topicId, topic.getTopicName());

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.container, lessonsFragment, "ListeningLessonsFragment")
                    .addToBackStack("TopicToLessons")
                    .commit();

            Log.d(TAG, "Navigated to lessons for topic: " + topicId);
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

            // Sử dụng topicId thực từ topic object
            String topicId = topic.getTopicId();
            String firstLessonId = getFirstLessonId(topicId);

            // Tạo ListeningExerciseFragment fragment với topic ID thực
            ListeningExerciseFragment exerciseFragment =
                    ListeningExerciseFragment.newInstance(topicId, firstLessonId);

            // Navigate với animation
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // enter
                            R.anim.slide_out_left,  // exit
                            R.anim.slide_in_left,   // popEnter
                            R.anim.slide_out_right  // popExit
                    )
                    .replace(R.id.container, exerciseFragment, "ListeningExerciseFragment")
                    .addToBackStack("TopicToExercise")
                    .commit();

            Log.d(TAG, "Fragment transaction to ListeningExerciseFragment committed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to exercise", e);
            showError("Failed to open exercise");
        }
    }
    
    /**
     * Lấy lesson ID đầu tiên của topic
     */
    private String getFirstLessonId(String topicId) {
        switch (topicId) {
            case "lt_daily":
                return "ls_daily_01";
            case "lt_technology":
                return "ls_tech_01";
            default:
                return "ls_daily_01"; // fallback
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


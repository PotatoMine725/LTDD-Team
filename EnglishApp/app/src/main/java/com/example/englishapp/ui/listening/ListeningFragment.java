package com.example.englishapp.ui.listening;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.englishapp.ui.common.NotificationFragment;
import com.example.englishapp.R;
import com.example.englishapp.ui.common.TopTabNavigationHelper;
import com.example.englishapp.service.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ListeningFragment extends Fragment {

    private static final String TAG = "ListeningFragment";
    private TopTabNavigationHelper tabNavigationHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.listening_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo Tab Navigation Helper
        tabNavigationHelper = new TopTabNavigationHelper(
                view,
                requireContext(),
                getParentFragmentManager()
        );

        tabNavigationHelper.resetQuizTabText();

        // Set tab hiện tại là Listening
        tabNavigationHelper.setCurrentTab(TopTabNavigationHelper.TabType.LISTENING);

        // Tìm button notification
        ImageView btnNotification = view.findViewById(R.id.btn_notification);
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> showNotificationFragment());
        } else {
            Log.e(TAG, "btn_notification not found in layout");
        }

        // Setup click listener cho "All topics" link
        setupAllTopicsLink(view);

        // Setup click listener cho "All records" link
        setupAllRecordsLink(view);
        
        // Setup click listeners cho topic cards
        setupTopicCards(view);
        
        // Load images from Firebase
        loadTopicImages(view);
    }

    /**
     * Load topic images from Firebase
     */
    private void loadTopicImages(View view) {
        FirebaseService firebaseService = FirebaseService.getInstance();
        
        // Load Daily Life image
        firebaseService.getListeningTopicsRef().child("lt_daily").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imageUrl = snapshot.child("image_url").getValue(String.class);
                Log.d(TAG, "Daily Life image URL from Firebase: " + imageUrl);
                
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    // Find Daily Life ImageView
                    View dailyLifeCard = view.findViewById(R.id.daily_life_topic_card);
                    if (dailyLifeCard != null) {
                        ImageView dailyLifeImage = dailyLifeCard.findViewById(R.id.daily_life_image);
                        if (dailyLifeImage != null) {
                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.topic_placeholder)
                                    .error(R.drawable.topic_placeholder)
                                    .centerCrop()
                                    .into(dailyLifeImage);
                            Log.d(TAG, "Successfully loaded Daily Life image from Firebase");
                        } else {
                            Log.e(TAG, "Daily Life ImageView not found");
                        }
                    } else {
                        Log.e(TAG, "Daily Life CardView not found");
                    }
                } else {
                    Log.w(TAG, "No image URL found for Daily Life topic");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading Daily Life image: " + error.getMessage());
            }
        });
        
        // Load Technology image
        firebaseService.getListeningTopicsRef().child("lt_technology").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imageUrl = snapshot.child("image_url").getValue(String.class);
                Log.d(TAG, "Technology image URL from Firebase: " + imageUrl);
                
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    // Find Technology ImageView
                    View technologyCard = view.findViewById(R.id.technology_topic_card);
                    if (technologyCard != null) {
                        ImageView technologyImage = technologyCard.findViewById(R.id.technology_image);
                        if (technologyImage != null) {
                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.topic_technology)
                                    .error(R.drawable.topic_technology)
                                    .centerCrop()
                                    .into(technologyImage);
                            Log.d(TAG, "Successfully loaded Technology image from Firebase");
                        } else {
                            Log.e(TAG, "Technology ImageView not found");
                        }
                    } else {
                        Log.e(TAG, "Technology CardView not found");
                    }
                } else {
                    Log.w(TAG, "No image URL found for Technology topic, using default");
                    // Fallback to default image URL
                    String defaultUrl = "https://cdn.pixabay.com/photo/2018/05/08/08/44/artificial-intelligence-3382507_1280.jpg";
                    View technologyCard = view.findViewById(R.id.technology_topic_card);
                    if (technologyCard != null) {
                        ImageView technologyImage = technologyCard.findViewById(R.id.technology_image);
                        if (technologyImage != null) {
                            Glide.with(requireContext())
                                    .load(defaultUrl)
                                    .placeholder(R.drawable.topic_technology)
                                    .error(R.drawable.topic_technology)
                                    .centerCrop()
                                    .into(technologyImage);
                            Log.d(TAG, "Loaded Technology image using default URL");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading Technology image: " + error.getMessage());
                // Fallback to default image URL on error
                String defaultUrl = "https://cdn.pixabay.com/photo/2018/05/08/08/44/artificial-intelligence-3382507_1280.jpg";
                View technologyCard = view.findViewById(R.id.technology_topic_card);
                if (technologyCard != null) {
                    ImageView technologyImage = technologyCard.findViewById(R.id.technology_image);
                    if (technologyImage != null) {
                        Glide.with(requireContext())
                                .load(defaultUrl)
                                .placeholder(R.drawable.topic_technology)
                                .error(R.drawable.topic_technology)
                                .centerCrop()
                                .into(technologyImage);
                        Log.d(TAG, "Loaded Technology image using default URL after Firebase error");
                    }
                }
            }
        });
    }

    /**
     * Setup click listeners cho topic cards
     */
    private void setupTopicCards(View view) {
        // Daily Life topic card
        View dailyLifeCard = view.findViewById(R.id.daily_life_topic_card);
        if (dailyLifeCard != null) {
            dailyLifeCard.setOnClickListener(v -> {
                Log.d(TAG, "Daily Life topic clicked - navigating to lessons");
                navigateToTopicLessons("lt_daily", "Daily Life");
            });
        } else {
            Log.e(TAG, "daily_life_topic_card not found in layout");
        }

        // Technology topic card
        View technologyCard = view.findViewById(R.id.technology_topic_card);
        if (technologyCard != null) {
            technologyCard.setOnClickListener(v -> {
                Log.d(TAG, "Technology topic clicked - navigating to lessons");
                navigateToTopicLessons("lt_technology", "Technology");
            });
        } else {
            Log.e(TAG, "technology_topic_card not found in layout");
        }
    }

    /**
     * Navigate to topic lessons
     */
    private void navigateToTopicLessons(String topicId, String topicName) {
        if (getActivity() == null) {
            Log.e(TAG, "Activity is null, cannot navigate");
            return;
        }

        try {
            Log.d(TAG, "Navigating to lessons for topic: " + topicName + " (ID: " + topicId + ")");
            
            // Tạo ListeningLessonsFragment
            ListeningLessonsFragment lessonsFragment = 
                    ListeningLessonsFragment.newInstance(topicId, topicName);

            // Navigate với animation
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // enter
                            R.anim.slide_out_left,  // exit
                            R.anim.slide_in_left,   // popEnter
                            R.anim.slide_out_right  // popExit
                    )
                    .replace(R.id.container, lessonsFragment, "ListeningLessonsFragment")
                    .addToBackStack("ListeningToLessons")
                    .commit();

            Log.d(TAG, "Fragment transaction to ListeningLessonsFragment committed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to topic lessons", e);
            e.printStackTrace();
        }
    }

    /**
     * Setup click listener cho "All topics" link
     */
    private void setupAllTopicsLink(View view) {
        TextView allTopicsLink = view.findViewById(R.id.all_topics_link);
        if (allTopicsLink != null) {
            allTopicsLink.setOnClickListener(v -> {
                Log.d(TAG, "All topics clicked - navigating to ListeningTopicFragment");
                navigateToListeningTopic();
            });
        } else {
            Log.e(TAG, "all_topics_link not found in layout");
        }
    }

    /**
     * Setup click listener cho "All records" link
     */
    private void setupAllRecordsLink(View view) {
        TextView allRecordsLink = view.findViewById(R.id.all_records_link);
        if (allRecordsLink != null) {
            allRecordsLink.setOnClickListener(v -> {
                Log.d(TAG, "All records clicked - navigating to ListeningTopicFragment");
                navigateToListeningTopic();
            });
        } else {
            Log.e(TAG, "all_records_link not found in layout");
        }
    }

    /**
     * Navigate to ListeningTopicFragment
     */
    private void navigateToListeningTopic() {
        if (getActivity() == null) {
            Log.e(TAG, "Activity is null, cannot navigate");
            return;
        }

        try {
            ListeningTopicFragment listeningTopicFragment = new ListeningTopicFragment();

            // Sử dụng getSupportFragmentManager() của Activity
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // enter
                            R.anim.slide_out_left,  // exit
                            R.anim.slide_in_left,   // popEnter
                            R.anim.slide_out_right  // popExit
                    )
                    .replace(R.id.container, listeningTopicFragment, "ListeningTopicFragment")
                    .addToBackStack("ListeningToTopic")
                    .commit();

            Log.d(TAG, "Fragment transaction to ListeningTopicFragment committed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to ListeningTopicFragment", e);
            e.printStackTrace();
        }
    }

    /**
     * Show notification fragment
     */
    private void showNotificationFragment() {
        NotificationFragment fragment = new NotificationFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        fragment.show(fragmentManager, "notification_dialog");
    }
}


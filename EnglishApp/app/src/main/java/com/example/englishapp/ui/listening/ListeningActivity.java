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

import com.example.englishapp.ui.common.NotificationFragment;
import com.example.englishapp.R;
import com.example.englishapp.ui.common.TopTabNavigationHelper;

public class ListeningActivity extends Fragment {

    private static final String TAG = "ListeningActivity";
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
    }

    /**
     * Setup click listener cho "All topics" link
     */
    private void setupAllTopicsLink(View view) {
        TextView allTopicsLink = view.findViewById(R.id.all_topics_link);
        if (allTopicsLink != null) {
            allTopicsLink.setOnClickListener(v -> {
                Log.d(TAG, "All topics clicked - navigating to ListeningTopicActivity");
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
                Log.d(TAG, "All records clicked - navigating to ListeningTopicActivity");
                navigateToListeningTopic();
            });
        } else {
            Log.e(TAG, "all_records_link not found in layout");
        }
    }

    /**
     * Navigate to ListeningTopicActivity
     */
    private void navigateToListeningTopic() {
        if (getActivity() == null) {
            Log.e(TAG, "Activity is null, cannot navigate");
            return;
        }

        try {
            ListeningTopicActivity listeningTopicFragment = new ListeningTopicActivity();

            // Sử dụng getSupportFragmentManager() của Activity
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // enter
                            R.anim.slide_out_left,  // exit
                            R.anim.slide_in_left,   // popEnter
                            R.anim.slide_out_right  // popExit
                    )
                    .replace(R.id.container, listeningTopicFragment, "ListeningTopicActivity")
                    .addToBackStack("ListeningToTopic")
                    .commit();

            Log.d(TAG, "Fragment transaction to ListeningTopicActivity committed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to ListeningTopicActivity", e);
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
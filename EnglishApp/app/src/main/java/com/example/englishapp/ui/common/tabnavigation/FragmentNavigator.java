package com.example.englishapp.ui.common.tabnavigation;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.englishapp.ui.vocabulary.LessonFragment;
import com.example.englishapp.ui.listening.ListeningFragment;
import com.example.englishapp.ui.speaking.SpeakingFragment;

/**
 * Handler xử lý navigation giữa các Fragment
 */
public class FragmentNavigator {
    private static final String TAG = "FragmentNavigator";

    private FragmentManager fragmentManager;
    private int containerId;

    public FragmentNavigator(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    public void navigateToTab(TabType tabType) {
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
                    // Quiz không navigate fragment
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to tab", e);
        }
    }

    private void navigateToVocabulary() {
        try {
            Log.d(TAG, "Navigating to Vocabulary");
            LessonFragment fragment = new LessonFragment();
            replaceFragmentWithoutBackStack(fragment, "LessonFragment");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to vocabulary", e);
        }
    }

    private void navigateToListening() {
        try {
            Log.d(TAG, "Navigating to Listening");
            ListeningFragment fragment = new ListeningFragment();
            replaceFragmentWithoutBackStack(fragment, "ListeningFragment");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to listening", e);
        }
    }

    private void navigateToSpeaking() {
        try {
            Log.d(TAG, "Navigating to Speaking");
            SpeakingFragment fragment = new SpeakingFragment();
            replaceFragmentWithoutBackStack(fragment, "SpeakingFragment");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to speaking", e);
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
        }
    }
}


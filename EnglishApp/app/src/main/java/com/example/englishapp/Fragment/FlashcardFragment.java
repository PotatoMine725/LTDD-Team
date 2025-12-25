package com.example.englishapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.englishapp.R;

public class FlashcardFragment extends Fragment {

    private static final String ARG_TOPIC_TITLE = "tv_title";
    private String topicTitle;

    public FlashcardFragment() {}

    public static FlashcardFragment newInstance(String topicTitle) {
        FlashcardFragment fragment = new FlashcardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOPIC_TITLE, topicTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topicTitle = getArguments().getString(ARG_TOPIC_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.flashcard_vocabulary_layout_v1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Thiết lập nút back
        ImageView ivBackArrow = view.findViewById(R.id.iv_back_arrow);
        if (ivBackArrow != null) {
            ivBackArrow.setOnClickListener(v -> {
                // Quay lại Vocabulary Fragment
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
    }
}
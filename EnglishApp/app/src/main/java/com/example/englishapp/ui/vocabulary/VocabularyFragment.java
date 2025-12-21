package com.example.englishapp.ui.vocabulary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.englishapp.ui.common.NotificationFragment;
import com.example.englishapp.R;

public class VocabularyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.vocabulary_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tìm button notification trong vocabulary_layout
        ImageView btnNotification = view.findViewById(R.id.btn_notification);

        // Thiết lập click listener
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> showNotificationFragment());
        } else {
            // Log lỗi nếu không tìm thấy button
            android.util.Log.e("VocabularyFragment", "btn_notification not found in layout");
        }
    }

    // Mở notification (DialogFragment)
    private void showNotificationFragment() {
        NotificationFragment fragment = new NotificationFragment();

        // Lấy FragmentManager từ Activity chứa Fragment này
        FragmentManager fragmentManager = getParentFragmentManager();

        // Hiển thị DialogFragment
        fragment.show(fragmentManager, "notification_dialog");
    }
}


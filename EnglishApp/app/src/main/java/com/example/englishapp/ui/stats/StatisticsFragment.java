package com.example.englishapp.ui.stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.englishapp.R;
import com.example.englishapp.ui.common.NotificationFragment;

public class StatisticsFragment extends Fragment {

    public StatisticsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tìm button notification trong statistics layout
        ImageView btnNotification = view.findViewById(R.id.btn_notification);

        // Thiết lập click listener
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> showNotificationFragment());
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
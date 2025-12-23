package com.example.englishapp.ui.common;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.englishapp.R;
import com.example.englishapp.databinding.FragmentNotificationBinding;
import com.example.englishapp.model.NotificationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationFragment extends DialogFragment {

    private static final String TAG = "NOTI";

    private FragmentNotificationBinding binding;
    private DatabaseReference notiRef;

    // ===== FILTER TYPE =====
    private enum FilterType {
        ALL,
        UNREAD
    }

    private FilterType currentFilter = FilterType.ALL;

    // ===== CACHE DATA =====
    private final List<NotificationModel> allNotifications = new ArrayList<>();
    private final List<String> allKeys = new ArrayList<>();

    // ===================== onCreateView =====================
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // ===================== onViewCreated =====================
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ===== BACK BUTTON =====
        binding.backIcon.setOnClickListener(v -> dismiss());

        // ===== FILTER BUTTONS =====
        setupFilterButtons();

        // ===== GET USER UID =====
        String uid = Objects.requireNonNull(
                FirebaseAuth.getInstance().getCurrentUser()
        ).getUid();

        // ===== DATABASE REF =====
        notiRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("notifications");

        // ===== LISTEN FIREBASE =====
        listenNotifications();
    }

    // ===================== FILTER BUTTONS =====================
    private void setupFilterButtons() {

        // Default = ALL
        currentFilter = FilterType.ALL;
        binding.btnAll.setAlpha(1f);
        binding.tvUnread.setAlpha(0.5f);

        binding.btnAll.setOnClickListener(v -> {
            currentFilter = FilterType.ALL;
            binding.btnAll.setAlpha(1f);
            binding.tvUnread.setAlpha(0.5f);
            renderNotifications();
        });

        binding.tvUnread.setOnClickListener(v -> {
            currentFilter = FilterType.UNREAD;
            binding.btnAll.setAlpha(0.5f);
            binding.tvUnread.setAlpha(1f);
            renderNotifications();
        });
    }

    // ===================== FIREBASE LISTENER =====================
    private void listenNotifications() {

        notiRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                allNotifications.clear();
                allKeys.clear();

                for (DataSnapshot child : snapshot.getChildren()) {

                    NotificationModel noti =
                            child.getValue(NotificationModel.class);

                    if (noti == null) continue;

                    allNotifications.add(noti);
                    allKeys.add(child.getKey());
                }

                renderNotifications();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase error: " + error.getMessage());
            }
        });
    }

    // ===================== RENDER UI =====================
    private void renderNotifications() {

        if (binding == null) return;

        binding.containerNotifications.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (int i = 0; i < allNotifications.size(); i++) {

            NotificationModel noti = allNotifications.get(i);
            String key = allKeys.get(i);

            // ===== FILTER LOGIC =====
            if (currentFilter == FilterType.UNREAD && noti.is_read) {
                continue;
            }

            boolean isRead = noti.is_read;

            // ===== UI LOGIC (THEO YÊU CẦU) =====
            // ĐÃ ĐỌC  -> TRẮNG
            // CHƯA ĐỌC -> XÁM
            int layoutId = isRead
                    ? R.layout.item_notification_default   // trắng
                    : R.layout.item_notification_grey;     // xám

            View itemView = inflater.inflate(
                    layoutId,
                    binding.containerNotifications,
                    false
            );

            TextView tvContent = itemView.findViewById(
                    isRead
                            ? R.id.tv_notification_content
                            : R.id.tv_notification_content_grey
            );

            TextView tvTime = itemView.findViewById(
                    isRead
                            ? R.id.tv_notification_time
                            : R.id.tv_notification_time_grey
            );

            if (tvContent == null || tvTime == null) continue;

            tvContent.setText(noti.message);
            tvTime.setText(noti.getTimeAgo());

            // ===== CLICK → MARK AS READ =====
            itemView.setOnClickListener(v -> {
                if (noti.is_read) return;

                notiRef.child(key)
                        .child("is_read")
                        .setValue(true);
            });

            binding.containerNotifications.addView(itemView);
        }
    }

    // ===================== onStart =====================
    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            getDialog().getWindow()
                    .setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    // ===================== onDestroyView =====================
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

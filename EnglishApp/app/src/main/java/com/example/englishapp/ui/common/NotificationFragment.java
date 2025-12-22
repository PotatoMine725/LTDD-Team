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

import java.util.Objects;

public class NotificationFragment extends DialogFragment {

    private static final String TAG = "NOTI";

    private FragmentNotificationBinding binding;

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

        // Back button
        binding.backIcon.setOnClickListener(v -> dismiss());

        // ===== LẤY UID USER =====
        String uid = Objects.requireNonNull(
                FirebaseAuth.getInstance().getCurrentUser()
        ).getUid();

        Log.e(TAG, "LOGIN UID = " + uid);

        // ===== DATABASE PATH =====
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("notifications");

        Log.e(TAG, "REF PATH = users/" + uid + "/notifications");

        LayoutInflater inflater = LayoutInflater.from(requireContext());

        // ===== LISTENER =====
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.e(TAG, "snapshot.exists = " + snapshot.exists());
                Log.e(TAG, "children count = " + snapshot.getChildrenCount());

                binding.containerNotifications.removeAllViews();

                for (DataSnapshot child : snapshot.getChildren()) {

                    Log.e(TAG, "Raw child = " + child.getValue());

                    NotificationModel noti =
                            child.getValue(NotificationModel.class);

                    if (noti == null) {
                        Log.e(TAG, "NotificationModel = NULL");
                        continue;
                    }

                    Log.e(TAG, "Parsed noti => title="
                            + noti.title
                            + " | message="
                            + noti.message
                            + " | is_read="
                            + noti.is_read
                            + " | timestamp="
                            + noti.timestamp
                    );

                    boolean isRead = noti.is_read;

                    int layoutId = isRead
                            ? R.layout.item_notification_grey
                            : R.layout.item_notification_default;

                    View itemView = inflater.inflate(
                            layoutId,
                            binding.containerNotifications,
                            false
                    );

                    TextView tvContent = itemView.findViewById(
                            isRead
                                    ? R.id.tv_notification_content_grey
                                    : R.id.tv_notification_content
                    );

                    TextView tvTime = itemView.findViewById(
                            isRead
                                    ? R.id.tv_notification_time_grey
                                    : R.id.tv_notification_time
                    );

                    if (tvContent == null || tvTime == null) {
                        Log.e(TAG, "TextView NULL – CHECK LAYOUT ID");
                        continue;
                    }

                    tvContent.setText(noti.message);
                    tvTime.setText(noti.getTimeAgo());

                    binding.containerNotifications.addView(itemView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase error: " + error.getMessage());
            }
        });
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

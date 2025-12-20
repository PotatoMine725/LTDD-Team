package com.example.englishapp.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.englishapp.R;
import com.example.englishapp.ui.auth.LoginActivity;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView ivExit = view.findViewById(R.id.iv_exit);

        ivExit.setOnClickListener(v -> {
            // Mở LoginActivity
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);

            // Đóng activity hiện tại để không quay lại bằng nút BACK
            requireActivity().finish();
        });
    }
}

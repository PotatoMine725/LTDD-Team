package com.example.englishapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.englishapp.Fragment.NotificationFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home); // Giả định tên layout là activity_home.xml

        ImageView btnNotification = findViewById(R.id.btn_notification);

        // 1. Xử lý sự kiện click cho nút thông báo
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi hàm để hiển thị NotificationFragment
                showNotificationFragment();
            }
        });

        // ... các khởi tạo khác ...
    }
    /**
     * Hàm thực hiện giao dịch Fragment để hiển thị NotificationFragment.
     */
    private void showNotificationFragment() {
        // Tạo một thể hiện (instance) của NotificationFragment
        NotificationFragment fragment = new NotificationFragment();

        // Lấy FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Bắt đầu giao dịch Fragment
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Thêm Fragment vào container (hoặc thay thế Fragment hiện tại)
        // R.id.fragment_container là ID của ViewGroup (như FrameLayout) trong layout của Activity
        // nơi bạn muốn hiển thị Fragment.
        fragmentTransaction.replace(R.id.fragment_container, fragment);

        // Thêm giao dịch vào Back Stack (để người dùng có thể nhấn nút Back để quay lại)
        fragmentTransaction.addToBackStack(null);

        // Commit giao dịch
        fragmentTransaction.commit();
    }
}
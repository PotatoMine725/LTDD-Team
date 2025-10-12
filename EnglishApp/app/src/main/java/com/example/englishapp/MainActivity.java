package com.example.englishapp;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.speaking_topics);
        View mainView = findViewById(R.id.main);
        // Lưu lại padding ban đầu từ XML
        final int initialPaddingLeft = mainView.getPaddingLeft();
        final int initialPaddingTop = mainView.getPaddingTop();
        final int initialPaddingRight = mainView.getPaddingRight();
        final int initialPaddingBottom = mainView.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            v.setPadding(
                    initialPaddingLeft + systemBars.left,
                    initialPaddingTop + systemBars.top,
                    initialPaddingRight + systemBars.right,
                    initialPaddingBottom + systemBars.bottom
            );
            return insets;
        });
    }



    // lỗi ghi padding, làm sát lề
    //Vấn đề này xảy ra do Window Insets (hay còn gọi là Edge-to-Edge display).
    // Kể từ các phiên bản Android gần đây, các ứng dụng được khuyến khích hiển
    // thị "tràn viền" (edge-to-edge), nghĩa là giao diện sẽ được vẽ bên dưới cả thanh trạng thái (status bar)
    // và thanh điều hướng (navigation bar).Khi bạn kích hoạt tính năng này (thường được bật mặc định trong các theme Material3 mới),
    // hệ thống sẽ bỏ qua thuộc tính padding của layout gốc và áp dụng các "insets" (phần đệm an toàn) của riêng nó để đảm bảo nội dung
    // không bị các thanh hệ thống che khuất.
}
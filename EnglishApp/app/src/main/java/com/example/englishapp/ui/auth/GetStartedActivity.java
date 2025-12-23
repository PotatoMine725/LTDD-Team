package com.example.englishapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishapp.R;

public class GetStartedActivity extends AppCompatActivity {

    private Button btnGetStarted, btnAlreadyAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyt_getstarted);

        btnGetStarted = findViewById(R.id.btn_getstarted);
        btnAlreadyAcc = findViewById(R.id.btn_alreadyacc);

        // Nút GET STARTED → mở trang đăng ký
        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        // Nút ALREADY HAVE AN ACCOUNT → mở trang đăng nhập
        btnAlreadyAcc.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }
}

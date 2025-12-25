package com.example.englishapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

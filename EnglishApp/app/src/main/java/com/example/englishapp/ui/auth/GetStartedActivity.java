package com.example.englishapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class GetStartedActivity extends AppCompatActivity {

    private Button btnGetStarted;
    private Button btnAlreadyAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lyt_getstarted);

        btnGetStarted = findViewById(R.id.btn_getstarted);
        btnAlreadyAcc = findViewById(R.id.btn_alreadyacc);

        // Chuyển sang đăng ký
        btnGetStarted.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        // Chuyển sang đăng nhập
        btnAlreadyAcc.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }
}

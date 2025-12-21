package com.example.englishapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.englishapp.R;
import com.example.englishapp.ui.home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvRegister;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyt_login);

        // luôn yêu cầu login mỗi lần mở app
        mAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_sign_up);

        btnLogin.setOnClickListener(v -> loginUser());

        if (tvRegister != null) {
            tvRegister.setOnClickListener(v ->
                    startActivity(new Intent(this, RegisterActivity.class))
            );
        }
    }

    // ================= LOGIN =================

    private void loginUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // ===== VALIDATE INPUT =====
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập Email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Vui lòng nhập Mật khẩu");
            return;
        }

        // Disable button tránh spam
        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    // Bật lại nút login
                    btnLogin.setEnabled(true);

                    // đăng nhập thất bại
                    if (!task.isSuccessful()) {

                        String errorMessage = "Đăng nhập thất bại";

                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }

                        Toast.makeText(
                                LoginActivity.this,
                                errorMessage,
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    //  đang nhập thành công
                    Toast.makeText(
                            LoginActivity.this,
                            "Đăng nhập thành công",
                            Toast.LENGTH_SHORT
                    ).show();

                    String uid = mAuth.getCurrentUser().getUid();

                    checkOrCreateProfile(uid);
                });
    }


    // ================= CHECK PROFILE =================

    private void checkOrCreateProfile(String uid) {
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("profile")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            navigateToHome();
                        } else {
                            createDefaultProfile(uid);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this,
                                error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ================= CREATE PROFILE =================

    private void createDefaultProfile(String uid) {

        String email = mAuth.getCurrentUser().getEmail();
        String displayName = email != null ? email.split("@")[0] : "User";

        Map<String, Object> profile = new HashMap<>();
        profile.put("uid", uid);
        profile.put("email", mAuth.getCurrentUser().getEmail());
        profile.put("display_name", displayName);
        profile.put("avatar_url", "default");
        profile.put("created_at", System.currentTimeMillis());

        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("profile")
                .setValue(profile)
                .addOnSuccessListener(unused -> navigateToHome())
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }

    // ================= NAVIGATION =================

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );
        startActivity(intent);
        finish();
    }
}

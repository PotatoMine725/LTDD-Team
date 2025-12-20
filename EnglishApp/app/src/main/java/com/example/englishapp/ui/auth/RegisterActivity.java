package com.example.englishapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword, edtConfirmPassword;
    private CheckBox cbTerms;
    private Button btnRegister;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyt_register);

        auth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        cbTerms = findViewById(R.id.cb_terms);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> register());
    }

    private void register() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Validate
        if (TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Bạn phải đồng ý Terms & Conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    saveProfile(uid, email);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void saveProfile(String uid, String email) {

        Map<String, Object> profile = new HashMap<>();
        profile.put("uid", uid);
        profile.put("email", email);
        profile.put("display_name", email); // KHÔNG có field name → dùng email
        profile.put("avatar_url", "default");
        profile.put("created_at", System.currentTimeMillis());

        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("profile")
                .setValue(profile)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Đăng ký thành công. Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();

                    auth.signOut(); // ÉP logout
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}

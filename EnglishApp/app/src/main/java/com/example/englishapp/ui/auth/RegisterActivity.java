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

    private EditText edtName, edtEmail, edtPassword, edtConfirmPassword;
    private CheckBox cbTerms;
    private Button btnRegister;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyt_register);

        auth = FirebaseAuth.getInstance();

        // Bind view
        edtName = findViewById(R.id.edt_name);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        cbTerms = findViewById(R.id.cb_terms);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> register());
    }

    // ================= REGISTER =================

    private void register() {

        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // ===== VALIDATE =====

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Vui lòng xác nhận mật khẩu", Toast.LENGTH_SHORT).show();
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

        // ===== FIREBASE AUTH =====

        btnRegister.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    saveProfile(uid, email, name); // ✅ TRUYỀN ĐỦ 3 THAM SỐ
                })
                .addOnFailureListener(e -> {
                    btnRegister.setEnabled(true);
                    Toast.makeText(this,
                            e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    // ================= SAVE PROFILE =================

    private void saveProfile(String uid, String email, String name) {

        Map<String, Object> profile = new HashMap<>();
        profile.put("uid", uid);
        profile.put("email", email);
        profile.put("display_name", name);   // ✅ LƯU TÊN NGƯỜI DÙNG
        profile.put("avatar_url", "default");
        profile.put("created_at", System.currentTimeMillis());

        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("profile")
                .setValue(profile)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(this,
                            "Đăng ký thành công. Vui lòng đăng nhập!",
                            Toast.LENGTH_SHORT).show();

                    auth.signOut(); // ÉP logout sau khi đăng ký

                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnRegister.setEnabled(true);
                    Toast.makeText(this,
                            e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}

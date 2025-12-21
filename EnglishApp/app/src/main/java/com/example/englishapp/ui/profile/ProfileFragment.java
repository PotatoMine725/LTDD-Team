package com.example.englishapp.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.englishapp.R;
import com.example.englishapp.ui.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private ImageView ivExit;
    private TextView tvUserName, tvUserEmail;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    public ProfileFragment() {}

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.profile, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        ivExit = view.findViewById(R.id.iv_exit);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);

        auth = FirebaseAuth.getInstance();

        // Lắng nghe trạng thái đăng nhập (CỐT LÕI)
        authListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                loadProfile(user);
            }
        };

        auth.addAuthStateListener(authListener);

        ivExit.setOnClickListener(v -> logout());
    }

    // ================= LOAD PROFILE =================

    private void loadProfile(@NonNull FirebaseUser user) {

        String uid = user.getUid();
        String emailAuth = user.getEmail();

        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("profile")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) {
                            // FALLBACK nếu chưa có profile
                            tvUserEmail.setText(emailAuth);
                            tvUserName.setText(
                                    emailAuth != null
                                            ? emailAuth.split("@")[0]
                                            : "User"
                            );
                            return;
                        }

                        String name = snapshot.child("display_name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);

                        tvUserName.setText(
                                (name != null && !name.isEmpty())
                                        ? name
                                        : "User"
                        );

                        tvUserEmail.setText(
                                (email != null && !email.isEmpty())
                                        ? email
                                        : emailAuth
                        );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(),
                                error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ================= LOGOUT =================

    private void logout() {
        auth.signOut();

        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    // ================= CLEAN LISTENER =================

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}

package com.example.englishapp.ui.chat;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.utils.SpeechToTextHelper;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private ChatViewModel viewModel;
    private ChatAdapter adapter;
    private EditText etMessage;
    private ActivityResultLauncher<Intent> speechLauncher;
    private ActivityResultLauncher<String> audioPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        RecyclerView rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        ImageButton btnSend = findViewById(R.id.btnSend);
        ImageButton btnMic = findViewById(R.id.btnMic);

        adapter = new ChatAdapter();
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        speechLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != RESULT_OK || result.getData() == null) {
                        return;
                    }
                    ArrayList<String> results =
                            result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (results == null || results.isEmpty()) {
                        return;
                    }
                    sendMessage(results.get(0));
                }
        );

        audioPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        launchSpeech();
                    } else {
                        Toast.makeText(this, "Cấp quyền cho micro", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        viewModel.getMessages().observe(this, messages -> {
            adapter.submit(messages);
            if (messages != null && !messages.isEmpty()) {
                rvChat.scrollToPosition(messages.size() - 1);
            }
        });

        btnSend.setOnClickListener(v -> sendMessage(etMessage.getText().toString()));

        btnMic.setOnClickListener(v -> {
            animateMicPress(v);
            startSpeechToText();
        });
    }

    private void sendMessage(String text) {
        String message = text == null ? "" : text.trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        etMessage.setText("");
        viewModel.sendMessage(message);
    }

    private void startSpeechToText() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            launchSpeech();
            return;
        }
        audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
    }

    private void launchSpeech() {
        Intent intent = SpeechToTextHelper.create();
        try {
            speechLauncher.launch(intent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "Thiết bị của bạn không hỗ trợ giọng nói.", Toast.LENGTH_SHORT).show();
        }
    }

    private void animateMicPress(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.12f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.12f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(220);
        set.start();
    }
}

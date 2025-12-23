package com.example.englishapp.ui.vocabulary;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.englishapp.R;
import com.example.englishapp.model.WordModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlashcardFragment extends Fragment {

    private static final String TAG = "FlashcardFragment";
    private List<WordModel> wordList = new ArrayList<>();
    private int currentIndex = 0;
    private boolean isFrontVisible = true;
    private String topicId = "vt_01";

    // Các thành phần UI
    private CardView cardFlashcard;
    private LinearLayout llFrontSide, llBackSide;
    private TextView tvIdiom, tvPhonetic, tvIdiomBack, tvVietnamese, tvEngDef, tvExample, tvExTrans, tvCurrentProgress, tvTitle;
    private ImageView ivIdiomImage, ivNext, ivPrev, ivAudioBack, ivBackArrow;
    private View btnListen;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.flashcard_vocabulary_layout_v1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Nhận topicId từ màn hình LessonFragment gửi sang
        if (getArguments() != null) {
            topicId = getArguments().getString("topic_id", "vt_01");
        }

        initViews(view);
        setupFlipLogic();
        setupNavigation();
        loadDataFromFirebase();
    }

    private void initViews(View view) {
        cardFlashcard = view.findViewById(R.id.card_flashcard);
        llFrontSide = view.findViewById(R.id.ll_front_side);
        llBackSide = view.findViewById(R.id.ll_back_side);

        tvIdiom = view.findViewById(R.id.tv_idiom);
        tvPhonetic = view.findViewById(R.id.tv_phonetic);
        ivIdiomImage = view.findViewById(R.id.iv_idiom_image);

        tvIdiomBack = view.findViewById(R.id.tv_idiom_back);
        tvVietnamese = view.findViewById(R.id.tv_vietnamese_meaning);
        tvEngDef = view.findViewById(R.id.tv_english_definition);
        tvExample = view.findViewById(R.id.tv_example);
        tvExTrans = view.findViewById(R.id.tv_example_translation);

        tvCurrentProgress = view.findViewById(R.id.tv_current_progress);
        ivNext = view.findViewById(R.id.iv_next_arrow);
        ivPrev = view.findViewById(R.id.iv_previous_arrow);
        btnListen = view.findViewById(R.id.btn_listen);
        ivAudioBack = view.findViewById(R.id.iv_audio_back);
        ivBackArrow = view.findViewById(R.id.iv_back_arrow);
        tvTitle = view.findViewById(R.id.tv_title);

        // Nút quay lại Activity chính
        ivBackArrow.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void setupFlipLogic() {
        // Logic lật bài khi chạm vào bất kỳ đâu trong CardView
        cardFlashcard.setOnClickListener(v -> {
            if (isFrontVisible) {
                llFrontSide.setVisibility(View.GONE);
                llBackSide.setVisibility(View.VISIBLE);
            } else {
                llFrontSide.setVisibility(View.VISIBLE);
                llBackSide.setVisibility(View.GONE);
            }
            isFrontVisible = !isFrontVisible;
        });
    }

    private void loadDataFromFirebase() {
        // Đường dẫn chính xác: topics -> vocabulary -> {topicId} -> words
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("topics")
                .child("vocabulary")
                .child(topicId)
                .child("words");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                wordList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    WordModel word = data.getValue(WordModel.class);
                    if (word != null) {
                        wordList.add(word);
                    }
                }

                if (!wordList.isEmpty()) {
                    updateUI();
                } else {
                    Toast.makeText(getContext(), "Chủ đề này chưa có dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase Error: " + error.getMessage());
            }
        });
    }

    private void updateUI() {
        if (currentIndex < 0 || currentIndex >= wordList.size()) return;

        WordModel word = wordList.get(currentIndex);

        // Hiển thị mặt trước
        tvIdiom.setText(word.getEnglish());
        tvPhonetic.setText("(" + (word.type != null ? word.type : "n") + ") " + word.getPronunciation());

        // Sử dụng Glide để load ảnh minh họa từ URL
        Glide.with(this)
                .load(word.image_url)
                .placeholder(R.drawable.ic_idiom_animals) // Ảnh chờ khi đang tải
                .error(R.drawable.ic_idiom_animals)       // Ảnh khi lỗi
                .into(ivIdiomImage);

        // Hiển thị mặt sau
        tvIdiomBack.setText(word.getEnglish());
        tvVietnamese.setText(word.getVietnamese());
        tvEngDef.setText("= " + (word.english_definition != null ? word.english_definition : "N/A"));
        tvExample.setText(word.example);
        tvExTrans.setText("(=Dịch: " + (word.example_translation != null ? word.example_translation : "...") + ")");

        // Cập nhật tiến độ học
        tvCurrentProgress.setText((currentIndex + 1) + "/" + wordList.size());

        // Mỗi khi sang từ mới, luôn bắt đầu bằng mặt trước
        llFrontSide.setVisibility(View.VISIBLE);
        llBackSide.setVisibility(View.GONE);
        isFrontVisible = true;

        // Gán sự kiện phát âm thanh
        View.OnClickListener audioListener = v -> playAudio(word.audio_url);
        btnListen.setOnClickListener(audioListener);
        ivAudioBack.setOnClickListener(audioListener);
    }

    private void setupNavigation() {
        // Nút chuyển sang từ vựng tiếp theo
        ivNext.setOnClickListener(v -> {
            if (currentIndex < wordList.size() - 1) {
                currentIndex++;
                updateUI();
            } else {
                Toast.makeText(getContext(), "Bạn đã hoàn thành chủ đề này!", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút quay lại từ vựng phía trước
        ivPrev.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                updateUI();
            }
        });
    }

    private void playAudio(String url) {
        if (url == null || url.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy file âm thanh cho từ này!", Toast.LENGTH_SHORT).show();
            return;
        }

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build());
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.setOnCompletionListener(MediaPlayer::release); // Giải phóng bộ nhớ sau khi phát xong
        } catch (IOException e) {
            Log.e(TAG, "MediaPlayer error: " + e.getMessage());
        }
    }
}
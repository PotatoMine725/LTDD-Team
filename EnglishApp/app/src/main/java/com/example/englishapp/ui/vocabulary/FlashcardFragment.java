package com.example.englishapp.ui.vocabulary;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import java.util.Locale;

public class FlashcardFragment extends Fragment {

    private static final String TAG = "FlashcardFragment";
    private List<WordModel> wordList = new ArrayList<>();
    private int currentIndex = 0;
    private boolean isFrontVisible = true;
    private String topicId = "vt_01";
    private String topicName = "";
    private String initialWordEnglish = null;
    private TextToSpeech textToSpeech;

    // Các thành phần UI
    private CardView cardFlashcard;
    private LinearLayout llFrontSide, llBackSide;
    private TextView tvIdiom, tvPhonetic, tvPhoneticBack, tvIdiomBack, tvVietnamese, tvEngDef, tvExample, tvExTrans, tvCurrentProgress, tvTitle, tvSavedCount;
    private ImageView ivIdiomImage, ivNext, ivPrev, ivAudioBack, ivBackArrow, ivExampleAudio;
    private View btnListen, btnSave;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topicTitle = getArguments().getString(ARG_TOPIC_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.flashcard_vocabulary_layout_v1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Nhận topicId và từ cần focus (nếu có) từ màn hình LessonFragment gửi sang
        if (getArguments() != null) {
            topicId = getArguments().getString("topic_id", "vt_01");
            initialWordEnglish = getArguments().getString("target_word", null);
        }

        initViews(view);
        setupFlipLogic();
        setupNavigation();
        loadTopicInfo();
    }

    private void initViews(View view) {
        cardFlashcard = view.findViewById(R.id.card_flashcard);
        llFrontSide = view.findViewById(R.id.ll_front_side);
        llBackSide = view.findViewById(R.id.ll_back_side);

        tvIdiom = view.findViewById(R.id.tv_idiom);
        tvPhonetic = view.findViewById(R.id.tv_phonetic);
        ivIdiomImage = view.findViewById(R.id.iv_idiom_image);

        tvIdiomBack = view.findViewById(R.id.tv_idiom_back);
        tvPhoneticBack = view.findViewById(R.id.tv_phonetic_back);
        tvVietnamese = view.findViewById(R.id.tv_vietnamese_meaning);
        tvEngDef = view.findViewById(R.id.tv_english_definition);
        tvExample = view.findViewById(R.id.tv_example);
        tvExTrans = view.findViewById(R.id.tv_example_translation);

        tvCurrentProgress = view.findViewById(R.id.tv_current_progress);
        tvSavedCount = view.findViewById(R.id.tv_saved_count);
        ivNext = view.findViewById(R.id.iv_next_arrow);
        ivPrev = view.findViewById(R.id.iv_previous_arrow);
        btnListen = view.findViewById(R.id.btn_listen);
        btnSave = view.findViewById(R.id.btn_save);
        ivAudioBack = view.findViewById(R.id.iv_audio_back);
        ivExampleAudio = view.findViewById(R.id.iv_example_audio);
        ivBackArrow = view.findViewById(R.id.iv_back_arrow);
        tvTitle = view.findViewById(R.id.tv_title);

        // Nút quay lại Activity chính
        ivBackArrow.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // Khởi tạo TTS
        textToSpeech = new TextToSpeech(requireContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
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

    private void loadTopicInfo() {
        // Lấy thông tin topic (name) rồi load danh sách từ
        DatabaseReference topicRef = FirebaseDatabase.getInstance()
                .getReference("topics")
                .child("vocabulary")
                .child(topicId);

        topicRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                topicName = snapshot.child("name").getValue(String.class);
                if (tvTitle != null && topicName != null) {
                    tvTitle.setText(topicName);
                }
                loadDataFromFirebase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase Error topic info: " + error.getMessage());
                loadDataFromFirebase();
            }
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
                    // Nếu có yêu cầu focus vào một từ cụ thể, set currentIndex tương ứng
                    if (initialWordEnglish != null) {
                        for (int i = 0; i < wordList.size(); i++) {
                            WordModel w = wordList.get(i);
                            if (w != null && w.getEnglish().equalsIgnoreCase(initialWordEnglish)) {
                                currentIndex = i;
                                break;
                            }
                        }
                    }
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
        tvPhoneticBack.setText("(" + (word.type != null ? word.type : "n") + ") " + word.getPronunciation());
        tvVietnamese.setText(word.getVietnamese());
        tvEngDef.setText("= " + (word.english_definition != null ? word.english_definition : "N/A"));
        tvExample.setText(word.example);
        tvExTrans.setText("(=Dịch: " + (word.example_translation != null ? word.example_translation : "...") + ")");

        // Cập nhật tiến độ học
        tvCurrentProgress.setText((currentIndex + 1) + "/" + wordList.size());
        updateSavedCount();

        // Mỗi khi sang từ mới, luôn bắt đầu bằng mặt trước
        llFrontSide.setVisibility(View.VISIBLE);
        llBackSide.setVisibility(View.GONE);
        isFrontVisible = true;

        // Gán sự kiện phát âm thanh
        View.OnClickListener frontAudioListener = v -> {
            if (!playAudio(word.audio_url)) {
                speakEnglish(word.getEnglish());
            }
        };

        View.OnClickListener mainBackAudioListener = v -> {
            if ("idiom".equalsIgnoreCase(word.type)) {
                speakIdiomDefinition(word);
            } else if (!playAudio(word.audio_url)) {
                speakEnglish(word.getEnglish());
            }
        };

        View.OnClickListener exampleAudioListener = v -> {
            if (!playAudio(word.example_audio_url)) {
                speakDefinitionAndExample(word);
            }
        };

        btnListen.setOnClickListener(frontAudioListener);
        ivAudioBack.setOnClickListener(mainBackAudioListener);
        ivExampleAudio.setOnClickListener(exampleAudioListener);
        btnSave.setOnClickListener(v -> saveCurrentWord());
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

    private boolean playAudio(String url) {
        if (url == null || url.isEmpty()) {
            return false;
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
            return false;
        }
        return true;
    }

    private void speakEnglish(String text) {
        if (textToSpeech != null && text != null && !text.isEmpty()) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_word");
        }
    }

    private void speakIdiomDefinition(WordModel word) {
        if (textToSpeech == null || word == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append(word.getEnglish());
        if (word.english_definition != null && !word.english_definition.isEmpty()) {
            sb.append(". ").append(word.english_definition);
        }

        textToSpeech.speak(sb.toString(), TextToSpeech.QUEUE_FLUSH, null, "tts_idiom_def");
    }

    private void speakDefinitionAndExample(WordModel word) {
        if (textToSpeech == null || word == null) return;

        StringBuilder sb = new StringBuilder();
        if (word.english_definition != null && !word.english_definition.isEmpty()) {
            sb.append("Definition: ").append(word.english_definition);
        }
        if (word.example != null && !word.example.isEmpty()) {
            if (sb.length() > 0) sb.append(". ");
            sb.append("Example: ").append(word.example);
        }

        textToSpeech.speak(sb.toString(), TextToSpeech.QUEUE_FLUSH, null, "tts_example");
    }

    private void saveCurrentWord() {
        if (getContext() == null || wordList.isEmpty()) return;
        WordModel word = wordList.get(currentIndex);
        if (word == null) return;
        VocabProgressStore.addWord(requireContext(), topicId, word.getEnglish());
        updateSavedCount();
        Toast.makeText(getContext(), "Saved " + word.getEnglish(), Toast.LENGTH_SHORT).show();
    }

    private void updateSavedCount() {
        if (getContext() == null) return;
        int saved = VocabProgressStore.getSavedWords(requireContext(), topicId).size();
        tvSavedCount.setText(saved + " saved");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
    }
}
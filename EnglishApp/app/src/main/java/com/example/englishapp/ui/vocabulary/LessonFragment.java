package com.example.englishapp.ui.vocabulary;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.englishapp.R;
import com.example.englishapp.model.WordModel;
import com.example.englishapp.ui.common.NotificationFragment;
import com.example.englishapp.ui.common.TopTabNavigationHelper;
import com.example.englishapp.ui.vocabulary.VocabProgressFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LessonFragment extends Fragment {

    private static final String TAG = "LessonFragment";
    private TopTabNavigationHelper tabNavigationHelper;

    public LessonFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            // Sử dụng layout vocabulary_layout.xml cho màn hình danh sách bài học
            return inflater.inflate(R.layout.vocabulary_layout, container, false);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi nạp layout", e);
            showError("Không thể tải màn hình bài học");
            return null;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            // Thiết lập các tab điều hướng phía trên (Vocabulary, Listening, Speaking, etc.)
            tabNavigationHelper = new TopTabNavigationHelper(view, requireContext(), getParentFragmentManager());
            tabNavigationHelper.setCurrentTab(TopTabNavigationHelper.TabType.VOCABULARY);

            // Thiết lập nút thông báo
            ImageView ivNotification = view.findViewById(R.id.btn_notification);
            if (ivNotification != null) {
                ivNotification.setOnClickListener(v -> showNotificationFragment());
            }

            View listIcon = view.findViewById(R.id.list_icon);
            if (listIcon != null) {
                listIcon.setOnClickListener(v -> openProgressFragment());
            }

            setupTopicCards(view);
            setupSearch(view);

        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi khởi tạo giao diện", e);
        }
    }

    private void navigateToFlashcard(String topicId) {
        navigateToFlashcard(topicId, null);
    }

    /**
     * Phương thức thực hiện chuyển hướng Fragment sang FlashcardFragment.
     * @param topicId ID của chủ đề từ vựng cần tải (ví dụ: vt_01)
     * @param targetWordEnglish nếu khác null sẽ dùng để focus đúng từ trong flashcard
     */
    private void navigateToFlashcard(String topicId, @Nullable String targetWordEnglish) {
        try {
            FlashcardFragment flashcardFragment = new FlashcardFragment();

            // Đóng gói dữ liệu topic_id để Fragment đích biết cần load từ vựng nào từ Firebase
            Bundle args = new Bundle();
            args.putString("topic_id", topicId);
            if (targetWordEnglish != null) {
                args.putString("target_word", targetWordEnglish);
            }
            flashcardFragment.setArguments(args);

            // Sử dụng FragmentManager từ Activity để thay thế nội dung trong container chính
            // LƯU Ý: R.id.main_container phải là ID của container chính trong Activity của bạn
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Thiết lập hiệu ứng chuyển cảnh mượt mà
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,  // enter
                    R.anim.slide_out_left,   // exit
                    R.anim.slide_in_left,   // popEnter
                    R.anim.slide_out_right  // popExit
            );

            // Thay thế bằng FlashcardFragment
            transaction.replace(R.id.container, flashcardFragment, "FlashcardFragment");

            // Thêm vào BackStack để khi nhấn nút Back có thể quay lại danh sách bài học
            transaction.addToBackStack("VocabularyToFlashcard");

            transaction.commit();

            Log.d(TAG, "Đã chuyển sang FlashcardFragment với chủ đề: " + topicId);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi điều hướng Fragment", e);
            showError("Không thể mở Flashcard");
        }
    }

    private void setupTopicCards(View root) {
        int[] cardIds = {
                R.id.cv_topic_vt_01,
                R.id.cv_topic_vt_02,
                R.id.cv_topic_vt_03,
                R.id.cv_topic_vt_04,
                R.id.cv_topic_vt_05
        };

        String[] topicIds = {"vt_01", "vt_02", "vt_03", "vt_04", "vt_05"};

        for (int i = 0; i < cardIds.length; i++) {
            CardView card = root.findViewById(cardIds[i]);
            String topicId = topicIds[i];
            if (card != null) {
                card.setOnClickListener(v -> navigateToFlashcard(topicId));
            }
        }
    }

    private void setupSearch(View root) {
        EditText etSearch = root.findViewById(R.id.et_search_content);
        if (etSearch == null) return;

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = v.getText().toString().trim();
                performSearch(query);
                return true;
            }
            return false;
        });
    }

    private void performSearch(String query) {
        if (TextUtils.isEmpty(query)) {
            showError("Vui lòng nhập từ khóa cần tìm");
            return;
        }

        String lowerQuery = query.toLowerCase();

        DatabaseReference vocabRef = FirebaseDatabase.getInstance()
                .getReference("topics")
                .child("vocabulary");

        vocabRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean[] found = {false};

                for (DataSnapshot topicSnap : snapshot.getChildren()) {
                    String topicId = topicSnap.getKey();
                    if (topicId == null) continue;

                    String topicName = topicSnap.child("name").getValue(String.class);
                    if (topicName != null && topicName.toLowerCase().contains(lowerQuery)) {
                        found[0] = true;
                        navigateToFlashcard(topicId);
                        break;
                    }

                    DataSnapshot wordsSnap = topicSnap.child("words");
                    for (DataSnapshot wordSnap : wordsSnap.getChildren()) {
                        WordModel word = wordSnap.getValue(WordModel.class);
                        if (word == null) continue;

                        String english = word.getEnglish().toLowerCase();
                        String vietnamese = word.getVietnamese().toLowerCase();

                        if (english.contains(lowerQuery) || vietnamese.contains(lowerQuery)) {
                            found[0] = true;
                            navigateToFlashcard(topicId, word.getEnglish());
                            break;
                        }
                    }

                    if (found[0]) break;
                }

                if (!found[0]) {
                    showError("Không tìm thấy kết quả phù hợp");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Lỗi tìm kiếm Firebase: " + error.getMessage());
                showError("Không thể tìm kiếm, vui lòng thử lại");
            }
        });
    }

    private void openProgressFragment() {
        try {
            VocabProgressFragment fragment = new VocabProgressFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.container, fragment, "VocabProgressFragment")
                    .addToBackStack("VocabProgress")
                    .commit();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi mở danh sách tiến độ", e);
            showError("Không thể mở danh sách tiến độ");
        }
    }

    /**
     * Hiển thị Dialog thông báo.
     */
    private void showNotificationFragment() {
        try {
            NotificationFragment fragment = new NotificationFragment();
            fragment.show(getParentFragmentManager(), "notification_dialog");
        } catch (Exception e) {
            Log.e(TAG, "Lỗi hiển thị thông báo", e);
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Dọn dẹp helper khi fragment bị hủy
        tabNavigationHelper = null;
    }
}
package com.example.englishapp.ui.vocabulary;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.englishapp.R;
import com.example.englishapp.ui.common.NotificationFragment;
import com.example.englishapp.ui.common.TopTabNavigationHelper;

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

            // --- XỬ LÝ SỰ KIỆN CLICK ĐỂ CHUYỂN SANG FLASHCARD ---
            CardView cvVocabularyLesson = view.findViewById(R.id.cv_vocabulary_lesson);
            if (cvVocabularyLesson != null) {
                cvVocabularyLesson.setOnClickListener(v -> {
                    // Khi click vào bài học, điều hướng sang FlashcardFragment với topic Animals (vt_01)
                    navigateToFlashcard("vt_01");
                });
            }

        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi khởi tạo giao diện", e);
        }
    }

    /**
     * Phương thức thực hiện chuyển hướng Fragment sang FlashcardFragment.
     * @param topicId ID của chủ đề từ vựng cần tải (ví dụ: vt_01)
     */
    private void navigateToFlashcard(String topicId) {
        try {
            FlashcardFragment flashcardFragment = new FlashcardFragment();

            // Đóng gói dữ liệu topic_id để Fragment đích biết cần load từ vựng nào từ Firebase
            Bundle args = new Bundle();
            args.putString("topic_id", topicId);
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
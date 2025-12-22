package com.example.englishapp.ui.speaking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.englishapp.data.model.SpeakingQuestion;
import com.example.englishapp.data.model.SpeakingTopic;
import com.example.englishapp.data.repository.SpeakingRepository;

import java.util.Map;
// cần đọc lại file này :>>>>>
public class SpeakingViewModel extends ViewModel {
    private final SpeakingRepository speakingRepository = new SpeakingRepository();
    private String currentTopicId;
    private int currentOrder = 1;
    private int totalQuestion = 0;
    private SpeakingTopic currentTopic;
    private final MutableLiveData<SpeakingQuestion> currentQuestion = new MutableLiveData<>();

    private final MutableLiveData<String> progressText = new MutableLiveData<>();
    private final MutableLiveData<String> spokenText = new MutableLiveData<>();

    public LiveData<SpeakingQuestion> getCurrentQuestion() {
        return currentQuestion;
    }

    public LiveData<String> getProgressText() {
        return progressText;
    }

    public LiveData<String> getSpokenText() {
        return spokenText;
    }

    // khi user chọn topic
    public void startTopic(
            String uid,
            SpeakingTopic topic,
            int savedOrder // lấy từ speaking_state nếu có, không có thì = 1
    ) {
        this.currentTopic = topic;
        this.currentTopicId = topic.id;
        this.totalQuestion = topic.total_question;
        this.currentOrder = savedOrder;

        // lưu trạng thái vào Firebase
        speakingRepository.startSpeaking(
                uid,
                topic.id,
                topic.name,
                topic.total_question);

    }
    private void updateUI() {
        if (currentTopic == null) return;

        // tìm question có order == currentOrder
        for (Map.Entry<String, SpeakingQuestion> entry
                : currentTopic.questions.entrySet()) {

            SpeakingQuestion q = entry.getValue();
            if (q.order == currentOrder) {
                currentQuestion.setValue(q);
                break;
            }
        }

        progressText.setValue(
                currentOrder + " / " + totalQuestion
        );
    }
    // hàm nhận text sau khi nói từ mic
    public void onSpokenText(String text) {
        spokenText.setValue(text);
    }
    // bấm nút next
    public boolean nextQuestion(String uid) {
        if (currentOrder < totalQuestion) {
            currentOrder++;

            speakingRepository.updateCurrentOrder(
                    uid,
                    currentTopicId,
                    currentOrder
            );
            updateUI();
            return true;
        }
        return false; // đã hết câu
    }
}
















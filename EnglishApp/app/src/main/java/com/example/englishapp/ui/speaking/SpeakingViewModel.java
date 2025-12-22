package com.example.englishapp.ui.speaking;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.englishapp.data.model.SpeakingQuestion;
import com.example.englishapp.data.model.SpeakingTopic;
import com.example.englishapp.data.repository.SpeakingRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
    private final MutableLiveData<List<SpeakingTopic>> topics = new MutableLiveData<>();
    public final MutableLiveData<List<SpeakingQuestion>> questions = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentIndex =
            new MutableLiveData<>(0);

    public LiveData<List<SpeakingQuestion>> getQuestions() {
        return questions;
    }

    public LiveData<Integer> getCurrentIndex() {
        return currentIndex;
    }
    public LiveData<SpeakingQuestion> getCurrentQuestion() {
        return currentQuestion;
    }

    public LiveData<String> getProgressText() {
        return progressText;
    }

    public LiveData<String> getSpokenText() {
        return spokenText;
    }
    public LiveData<List<SpeakingTopic>> getTopics() {
        return topics;
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
    // load speaking topic
    public void loadSpeakingTopics(){
        speakingRepository.loadTopics(new SpeakingRepository.Callback() {
            @Override
            public void onSuccess(List<SpeakingTopic> list) {
                topics.postValue(list);
            }

            @Override
            public void onError(String message) {
                // xử lý lỗi
            }
        });
    }
    public void loadQuestions(String topicId) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("topics")
                .child("speaking")
                .child(topicId)
                .child("questions");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<SpeakingQuestion> list = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    SpeakingQuestion q = snap.getValue(SpeakingQuestion.class);
                    if (q != null) {
                        q.id = snap.getKey();
                        list.add(q);
                    }
                }
                list.sort(Comparator.comparingInt(a -> a.order));
                questions.setValue(list);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    public void next() {
        if (questions.getValue() == null) return;
        int i = currentIndex.getValue();
        if (i < questions.getValue().size() - 1)
            currentIndex.setValue(i + 1);
    }

    public void prev() {
        int i = currentIndex.getValue();
        if (i > 0) currentIndex.setValue(i - 1);
    }
}















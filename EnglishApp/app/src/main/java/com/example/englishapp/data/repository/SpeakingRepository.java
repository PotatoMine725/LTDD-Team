package com.example.englishapp.data.repository;

import com.example.englishapp.data.api.GeminiService;
import com.example.englishapp.service.FirebaseService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SpeakingRepository {
    private  final GeminiService geminiService = new GeminiService();
    private final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    public void startSpeaking(String uid, String topicId, String topicName, int totalQuestion){
        // hàm reset lại speaking người dùng bấm vào topcics
       // set lại id topics
        rootRef.child("users")
                .child(uid)
                .child("speaking_state")
                .child("topic_id")
                .setValue(topicId);
        // set lại topics name
        rootRef.child("users")
                .child(uid)
                .child("speaking_state")
                .child("topic_name")
                .setValue(topicName);
        // set lại số lượng câu hỏi
        rootRef.child("users")
                .child(uid)
                .child("speaking_state")
                .child("total_question")
                .setValue(totalQuestion);
        // set lại số câu hỏi đã trả lời
        rootRef.child("users")
                .child(uid)
                .child("speaking_state")
                .child("current_order")
                .setValue(1);

    }
    // update số câu đang làm
    public void updateCurrentOrder(String uid, int order){
        rootRef.child("users")
                .child(uid)
                .child("speaking_state")
                .child("current_order")
                .setValue(order);
    }

}


















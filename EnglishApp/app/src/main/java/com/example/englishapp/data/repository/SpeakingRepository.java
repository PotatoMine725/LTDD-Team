package com.example.englishapp.data.repository;

import androidx.annotation.NonNull;

import com.example.englishapp.data.api.GeminiService;
import com.example.englishapp.data.api.OpenAICallBack;
import com.example.englishapp.data.model.SpeakingTopic;
import com.example.englishapp.service.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SpeakingRepository {
    private  final GeminiService geminiService = new GeminiService();
    private final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    public void startSpeaking(String uid, String topicId, String topicName, int totalQuestion){
// mỗi khi user bấm vào topic thfi cần lấy được topic hiệntaijd dang làm
        // để có theer update được progress
        DatabaseReference stateRef = rootRef.child("users")
                        .child(uid)
                        .child("speaking_state");
        // set topic đang đứng
        stateRef.child("current_topic").setValue(topicId);
        // set lại speaking state theo topic đang đứng, ví dụ khi từ câu 2 đến câu 3
        // phải tìm được state tương ứng với topic đang làm

        DatabaseReference topicRef = stateRef.child("topics_progress").child(topicId);
        // lưu lại state
        topicRef.child("topic_name").setValue(topicName);
        topicRef.child("total_question").setValue(totalQuestion);

        // nếu chưa có current order (sô câu đã làm theo người dùng)
        topicRef.child("current_order")// get lấy về node
                        .get().addOnSuccessListener(snapshot -> {
                            //“Lấy dữ liệu current_order từ server
                            //Khi nào lấy xong thì chạy code bên trong”
                            //hàm bất đồng bộ
                            //snapshot = kết quả trả về từ Firebase
                            if(!snapshot.exists()){//exist: kiểm tra node có tồn taại trong db hay không
                                topicRef.child("current_order").setValue(1);
                            }
                        });
    }
    // update số câu đang làm
    public void updateCurrentOrder(String uid, String topicId, int nextOrder){
        rootRef.child("users")
                .child(uid)
                .child("speaking_state")
                .child("topics_progress")
                .child(topicId)
                .child("current_order")
                .setValue(nextOrder);
    }
    // load topic từ firebase
    public DatabaseReference topics = rootRef.child("topics").child("speaking");
    public interface Callback{
        void onSuccess(List<SpeakingTopic> topics);
        void onError(String message);
    }// tạo interface để xử lý bất đồng bộ
    public void loadTopics(Callback callback){
        topics.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<SpeakingTopic> list = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    SpeakingTopic topic = dataSnapshot.getValue(SpeakingTopic.class);
                    if(topic != null){
                        topic.id = dataSnapshot.getKey(); // firebase khoong tự động map cái id tự sinh vào được (st_01)
                        list.add(topic);
                    }
                    }
                callback.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }

        });
    }
    public void evaluateSpeaking(String question, String answer, OpenAICallBack callback) {
        geminiService.evaluateSpeaking(question, answer, callback);
    }
}















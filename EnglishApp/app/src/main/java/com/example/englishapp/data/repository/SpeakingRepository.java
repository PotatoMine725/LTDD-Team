package com.example.englishapp.data.repository;

import com.example.englishapp.data.api.GeminiService;
import com.example.englishapp.service.FirebaseService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
}


















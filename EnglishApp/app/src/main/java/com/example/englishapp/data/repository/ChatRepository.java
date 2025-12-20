package com.example.englishapp.data.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.englishapp.data.api.GeminiService;
import com.example.englishapp.data.api.OpenAICallBack;
import com.example.englishapp.data.api.OpenAIService;
import com.example.englishapp.data.model.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ChatRepository {
    private final GeminiService geminiService = new GeminiService();
    private final FirebaseAuth ath = FirebaseAuth.getInstance(); // xác thực tài khoản
//     kết nối real time database
    private final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser getUser(){
        return ath.getCurrentUser(); // lấy người dùng hiện tại
    }
    // load lịch sử chat
    public void loadHistory(MutableLiveData<List<ChatMessage>> liveData){
        FirebaseUser user = getUser();
//        nêu chưa đăng nhập return
        if(user == null) return;
        DatabaseReference chatRef = rootRef
                .child("users")
                .child(user.getUid())
                .child("chats");
        chatRef.orderByChild("timestamp")
                .addListenerForSingleValueEvent(new ValueEventListener(){// lắng nghe sự kiện lấy dữ liêu 1 lần
                    // firebase sẽ tự đi tìm dữ liệu và sắp xêp
                    // khi lấy dữ liệu thành công tự động đóng gói dữ liệu trả về datasnapshot
                    // gọi hàm ondatachange với
                    // datashnap shot chính là dữ liệu được lấy ở trên
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<ChatMessage> chatMessages = new ArrayList<>();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            ChatMessage msg = dataSnapshot.getValue(ChatMessage.class); // tự động convert từ json sang message
                            chatMessages.add(msg);
                        }
                        liveData.setValue(chatMessages);
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
    }
    public void sendMessage(String message, MutableLiveData<List<ChatMessage>> liveData){
        FirebaseUser user = getUser();
        if(user == null)
            return;
        long now = System.currentTimeMillis();// lấy thời gian hiện tại mili giây
        // lưu message của user lên firebase
        ChatMessage usermsg = new ChatMessage(message, "user", now);
        pushMessage(user.getUid(), usermsg);
        // thêm tin nhắn vào Mutiltable ra UI
        addToLiveData(liveData, usermsg);


        // gọi AI để laasy respone
        geminiService.sendMessage(message, new OpenAICallBack() {
            @Override
            public void onSuccess(String reply) {
                ChatMessage aiMs = new ChatMessage(reply, "ai", System.currentTimeMillis());
                // lưu câu trả lời của ai lên realtime
                pushMessage(user.getUid(), aiMs);
                addToLiveData(liveData, aiMs);
            }

            @Override
            public void onError(String error) {
                ChatMessage aiMs = new ChatMessage(error, "ai", System.currentTimeMillis());
                pushMessage(user.getUid(), aiMs);
                addToLiveData(liveData, aiMs);
            }
        });
    }
//    hàm lưu tin nhắn ở local vào firebase
    public void pushMessage(String userId, ChatMessage message){
        rootRef.child("users")
                .child(userId)
                .child("chats")
                .push()
                .setValue(message);// -> realtime trên firebase tự động convert từ obj ->json
    }
    // hàm cập nhật dữ liệu cho UI thông qua MutableLiveData
    public void addToLiveData(MutableLiveData<List<ChatMessage>> liveData, ChatMessage message){
        List<ChatMessage> currentList = liveData.getValue();
        if(currentList == null){
            currentList = new ArrayList<>();
        }
        currentList.add(message);
        liveData.postValue(currentList);
    }
}













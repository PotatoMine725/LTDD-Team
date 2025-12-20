package com.example.englishapp.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.englishapp.data.model.ChatMessage;
import com.example.englishapp.data.repository.ChatRepository;

import java.util.List;

public class ChatViewModel extends ViewModel {
    private final ChatRepository repository = new ChatRepository();
    private final MutableLiveData<List<ChatMessage>> messages = new MutableLiveData<>();
    public void ChatViewModel(){
        repository.loadHistory(messages);
    }
    public LiveData<List<ChatMessage>> getMessages(){
        return messages;
    }
    public void sendMessage(String message){
        repository.sendMessage(message, messages);
    }
}

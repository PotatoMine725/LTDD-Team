package com.example.englishapp.data.model;

public class ChatMessage {
    public String message;
    public String sender;
    public long timestamp;

    public ChatMessage() {} // dành cho firebase

    public ChatMessage(String message, String sender, long timestamp) {
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
    }
}

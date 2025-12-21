package com.example.englishapp.model;

import java.io.Serializable;
import java.util.List;

public class QuizQuestion implements Serializable {
    private String id;
    private String question;
    private List<String> options;
    private int correctIndex;
    private String imageUrl;
    private String audioUrl; // Thêm trường Audio

    // Biến lưu trạng thái người dùng chọn/nhập
    private int userSelectedIndex = -1;
    private String userAnswerText = ""; // Lưu câu trả lời nhập tay cho bài nghe

    // 1. Constructor rỗng (Bắt buộc cho Firebase)
    public QuizQuestion() {
    }

    // 2. Constructor đầy đủ
    public QuizQuestion(String id, String question, List<String> options, int correctIndex) {
        this.id = id;
        this.question = question;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    // --- Getter & Setter ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public int getCorrectIndex() { return correctIndex; }
    public void setCorrectIndex(int correctIndex) { this.correctIndex = correctIndex; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

    public int getUserSelectedIndex() { return userSelectedIndex; }
    public void setUserSelectedIndex(int userSelectedIndex) { this.userSelectedIndex = userSelectedIndex; }

    public String getUserAnswerText() { return userAnswerText; }
    public void setUserAnswerText(String userAnswerText) { this.userAnswerText = userAnswerText; }
}
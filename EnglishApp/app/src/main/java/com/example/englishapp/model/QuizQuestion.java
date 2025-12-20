package com.example.englishapp.model;

import java.io.Serializable;
import java.util.List;

public class QuizQuestion implements Serializable {
    private String id;
    private String question;
    private List<String> options;
    private int correctIndex;
    private String imageUrl;

    // Biến lưu trạng thái người dùng chọn (mặc định là -1: chưa chọn)
    private int userSelectedIndex = -1;

    // 1. Constructor rỗng (Bắt buộc cho Firebase)
    public QuizQuestion() {
    }

    // 2. Constructor chính (Sửa để khớp với QuizActivity)
    public QuizQuestion(String id, String question, List<String> options, int correctIndex) {
        this.id = id;
        this.question = question;
        this.options = options;
        this.correctIndex = correctIndex;
        this.userSelectedIndex = -1; // Mặc định chưa chọn
    }

    // 3. Constructor đầy đủ (Nếu cần dùng sau này)
    public QuizQuestion(String id, String question, List<String> options, int correctIndex, String imageUrl) {
        this.id = id;
        this.question = question;
        this.options = options;
        this.correctIndex = correctIndex;
        this.imageUrl = imageUrl;
        this.userSelectedIndex = -1;
    }

    // --- Getter & Setter ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public void setCorrectIndex(int correctIndex) {
        this.correctIndex = correctIndex;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getUserSelectedIndex() {
        return userSelectedIndex;
    }

    public void setUserSelectedIndex(int userSelectedIndex) {
        this.userSelectedIndex = userSelectedIndex;
    }
}
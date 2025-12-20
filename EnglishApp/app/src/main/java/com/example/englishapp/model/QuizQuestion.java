package com.example.englishapp.model;

import java.util.List;
import java.io.Serializable;
public class QuizQuestion implements Serializable {
    private String id;
    private String question;
    private List<String> options;
    private int correctIndex;

    //constructor
    public QuizQuestion() {
    }
    // 3. Thêm trường lưu đáp án người dùng chọn
    private int userSelectedIndex = -1;
    public QuizQuestion(String id, String question, List<String> options, int correctIndex) {
        this.id = id;
        this.question = question;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    //getter
    public String getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public void setId(String id) {
        this.id = id;
    }

    //setter
    public void setQuestion(String question) {
        this.question = question;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void setCorrectIndex(int correctIndex) {
        this.correctIndex = correctIndex;
    }
    // 4. Getter & Setter cho userSelectedIndex
    public int getUserSelectedIndex() { return userSelectedIndex; }
    public void setUserSelectedIndex(int userSelectedIndex) { this.userSelectedIndex = userSelectedIndex; }
}

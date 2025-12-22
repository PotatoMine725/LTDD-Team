package com.example.englishapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class QuestionResult implements Parcelable {
    private int questionNumber;
    private String questionText;
    private String userAnswer;
    private String correctAnswer;
    private boolean isCorrect;

    public QuestionResult(int questionNumber, String questionText, String userAnswer, String correctAnswer, boolean isCorrect) {
        this.questionNumber = questionNumber;
        this.questionText = questionText;
        this.userAnswer = userAnswer;
        this.correctAnswer = correctAnswer;
        this.isCorrect = isCorrect;
    }

    protected QuestionResult(Parcel in) {
        questionNumber = in.readInt();
        questionText = in.readString();
        userAnswer = in.readString();
        correctAnswer = in.readString();
        isCorrect = in.readByte() != 0;
    }

    public static final Creator<QuestionResult> CREATOR = new Creator<QuestionResult>() {
        @Override
        public QuestionResult createFromParcel(Parcel in) {
            return new QuestionResult(in);
        }

        @Override
        public QuestionResult[] newArray(int size) {
            return new QuestionResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(questionNumber);
        dest.writeString(questionText);
        dest.writeString(userAnswer);
        dest.writeString(correctAnswer);
        dest.writeByte((byte) (isCorrect ? 1 : 0));
    }

    // Getters and setters
    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}
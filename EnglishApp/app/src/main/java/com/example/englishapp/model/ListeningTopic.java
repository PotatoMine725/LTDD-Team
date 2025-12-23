package com.example.englishapp.model;

public class ListeningTopic {
    private String topicName;
    private int imageResourceId;
    private int currentProgress;
    private int totalLessons;

    public ListeningTopic(String topicName, int imageResourceId, int currentProgress, int totalLessons) {
        this.topicName = topicName;
        this.imageResourceId = imageResourceId;
        this.currentProgress = currentProgress;
        this.totalLessons = totalLessons;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    public int getTotalLessons() {
        return totalLessons;
    }

    public void setTotalLessons(int totalLessons) {
        this.totalLessons = totalLessons;
    }

    public String getProgressText() {
        return currentProgress + "/" + totalLessons;
    }
}
package com.example.englishapp.model;

public class ListeningLesson {
    private String lessonId;
    private String title;
    private String imageUrl;
    private String audioUrl;
    private int duration;
    private boolean hasContent; // Có bài học hay không

    public ListeningLesson(String lessonId, String title, String imageUrl, String audioUrl, int duration, boolean hasContent) {
        this.lessonId = lessonId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.audioUrl = audioUrl;
        this.duration = duration;
        this.hasContent = hasContent;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean hasContent() {
        return hasContent;
    }

    public void setHasContent(boolean hasContent) {
        this.hasContent = hasContent;
    }
}


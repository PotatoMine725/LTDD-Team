package com.example.englishapp.model;

public class NotificationModel {

    public String title;
    public String message;
    public boolean is_read;
    public long timestamp;

    // Bắt buộc cho Firebase
    public NotificationModel() {}

    // Helper: format thời gian hiển thị
    public String getTimeAgo() {
        long diff = System.currentTimeMillis() - timestamp;

        long minutes = diff / (60 * 1000);
        if (minutes < 60) return minutes + " minutes ago";

        long hours = diff / (60 * 60 * 1000);
        if (hours < 24) return hours + " hours ago";

        long days = diff / (24 * 60 * 60 * 1000);
        return days + " days ago";
    }
}

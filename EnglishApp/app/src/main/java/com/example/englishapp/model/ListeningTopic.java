package com.example.englishapp.model;

public class ListeningTopic {
    private String topicId;
    private String topicName;
    private int imageResourceId;
    private String imageUrl; // Thêm field cho image URL
    private int currentProgress;
    private int totalLessons;

    public ListeningTopic(String topicName, int imageResourceId, int currentProgress, int totalLessons) {
        this.topicName = topicName;
        this.imageResourceId = imageResourceId;
        this.currentProgress = currentProgress;
        this.totalLessons = totalLessons;
        // Tự động tạo topicId từ topicName
        this.topicId = generateTopicId(topicName);
    }
    
    public ListeningTopic(String topicId, String topicName, int imageResourceId, int currentProgress, int totalLessons) {
        this.topicId = topicId;
        this.topicName = topicName;
        this.imageResourceId = imageResourceId;
        this.currentProgress = currentProgress;
        this.totalLessons = totalLessons;
    }
    
    // Constructor mới với imageUrl
    public ListeningTopic(String topicId, String topicName, String imageUrl, int currentProgress, int totalLessons) {
        this.topicId = topicId;
        this.topicName = topicName;
        this.imageUrl = imageUrl;
        this.imageResourceId = 0; // Không sử dụng resource ID
        this.currentProgress = currentProgress;
        this.totalLessons = totalLessons;
    }
    
    private String generateTopicId(String topicName) {
        if (topicName == null) return "lt_unknown";
        
        // Map specific topic names to Firebase IDs
        switch (topicName.toLowerCase()) {
            case "daily life":
                return "lt_daily";
            case "technology":
                return "lt_technology";
            default:
                return "lt_" + topicName.toLowerCase().replace(" ", "_").replace("&", "");
        }
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

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
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
    
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public boolean hasImageUrl() {
        return imageUrl != null && !imageUrl.isEmpty();
    }
}
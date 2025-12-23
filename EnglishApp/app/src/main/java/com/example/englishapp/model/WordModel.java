package com.example.englishapp.model;

import java.io.Serializable;

public class WordModel implements Serializable {
    public String english;
    public String vietnamese;
    public String type;
    public String pronunciation;
    public String english_definition;
    public String example;
    public String example_translation;
    public String image_url;
    public String audio_url;
    public String example_audio_url;

    // Constructor trống bắt buộc cho Firebase
    public WordModel() {
    }

    // Các getter cho an toàn dữ liệu
    public String getEnglish() { return english != null ? english : ""; }
    public String getVietnamese() { return vietnamese != null ? vietnamese : ""; }
    public String getPronunciation() { return pronunciation != null ? pronunciation : ""; }
}
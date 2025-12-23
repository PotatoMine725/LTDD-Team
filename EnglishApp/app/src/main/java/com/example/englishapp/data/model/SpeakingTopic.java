package com.example.englishapp.data.model;

import java.util.Map;

public class SpeakingTopic {
    public String id;
    public String name;
    public String image_res_name;
    public  int total_question;
    public Map<String, SpeakingQuestion> questions;
    public SpeakingTopic(){
    }
}

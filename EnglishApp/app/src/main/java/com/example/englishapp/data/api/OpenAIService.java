package com.example.englishapp.data.api;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.englishapp.BuildConfig;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class OpenAIService {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String OPENAI_API_KEY = BuildConfig.OPENAI_API_KEY;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();// để gửi request từ client
    public void sendMessage(String message, OpenAICallBack callback){
        try {
            JSONObject body = new JSONObject();
            body.put("model", "gpt-4o-mini");
            JSONArray messages = new JSONArray();// tạo mảng để tự tin nhắn giữa user và ai
            // ai
            messages.put(new JSONObject().put("role", "system")
                    .put("content", "You are an English tutor. Correct grammar and explain simply."));
            // user
            messages.put(new JSONObject().put("role", "user")
                    .put("content", message));
            // đưa tin nhắn vào body
            body.put("messages", messages);
            
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}

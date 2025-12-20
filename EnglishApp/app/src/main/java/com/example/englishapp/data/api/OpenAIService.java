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
    private final OkHttpClient client = new OkHttpClient();
    
    /**
     * Gửi message đến OpenAI API để xử lý ngôn ngữ tự nhiên
     * Sử dụng GPT model để phân tích và trả lời câu hỏi tiếng Anh
     */
    public void sendMessage(String message, OpenAICallBack callback){
        try {
            JSONObject body = new JSONObject();
            body.put("model", "gpt-4o-mini");
            JSONArray messages = new JSONArray();
            // System message để thiết lập vai trò của AI tutor
            messages.put(new JSONObject().put("role", "system")
                    .put("content", "You are an English tutor. Correct grammar and explain simply."));
            // User message chứa câu hỏi từ người dùng
            messages.put(new JSONObject().put("role", "user")
                    .put("content", message));
            body.put("messages", messages);
            
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}

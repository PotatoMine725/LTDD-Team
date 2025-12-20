package com.example.englishapp.data.api;


import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.example.englishapp.BuildConfig;

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
            // tạo request
            Request request = new Request.Builder().url(API_URL)
                    .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                    .post(RequestBody.create(body.toString(), JSON)).build();
            // gửi request
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onError(e.getMessage()); // nếu thất bại trả về message
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if(!response.isSuccessful()) {
                        callback.onError("Error code: " + response.code());
                        return;
                    }
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        // reply của ai
                        String reply = json.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        callback.onSuccess(reply); //phải gọi callback vì client(okhttp) là hàm bất đồng bộ
//                        cần phải gọi callback để trả về kết quả
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (JSONException e) {
            callback.onError(e.getMessage());
        }
    }
}

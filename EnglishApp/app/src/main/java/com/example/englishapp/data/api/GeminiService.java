package com.example.englishapp.data.api;

import androidx.annotation.NonNull;

import com.example.englishapp.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.*;

public class GeminiService {

    private static final String GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY;

    // các model phù hợp
    private static final String[] MODELS = {
            "models/gemini-flash-lite-latest",
            "models/gemini-flash-latest",
            "models/gemini-2.0-flash"
    };

    private static final String BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/%s:generateContent?key=%s";

    private static final MediaType JSON =
            MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private static final AtomicInteger index = new AtomicInteger(0);
// giống nhuw i++. lấy giá trị dungtruowcscs sau đó mới tang i
    // dùng trong đa luồng chạy song song
    public void sendMessage(String message, OpenAICallBack callback) {
        tryModel(message, callback, 0);
    }
    private  void tryModelSpeaking(String question, String answer, OpenAICallBack callback, int attempt) {
        if(attempt >= MODELS.length) {
            callback.onError("All Gemini models are busy. Please try again later.");
            return;
        }
        // nếu số lượt thứ mà gặp lỗi 403 vượt quá số lượng model -> báo lỗi
        String model = MODELS[index.getAndIncrement() % MODELS.length]; // tính chỉ số model
        String url = String.format(BASE_URL, model, GEMINI_API_KEY);
        JSONObject body = new JSONObject(); // tạo body khi gửi request
        try{
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            content.put("role", "user");
            JSONArray parts = new JSONArray();
            String prompt = "You are an English speaking tutor.\n\n" +
                    "Question: " + question + "\n" +
                    "User answer: " + answer + "\n\n" +
                    "Please return JSON with:\n" +
                    "- score (0-10)\n" +
                    "- grammar, fluency, pronunciation, content scores\n" +
                    "- strengths (array)\n" +
                    "- mistakes (array)\n" +
                    "- pronunciation_tips (array)\n" +
                    "- suggested_answer\n";
            parts.put(new JSONObject().put(
                    "text", prompt
            ));
            content.put("parts", parts);
            contents.put(content);
            body.put("contents", contents);
        } catch(JSONException e){
            callback.onError(e.getMessage());
        }
        // tạo request
        Request request = new Request.Builder().url(url)
                .post(RequestBody.create(body.toString(), JSON)).build();
        client.newCall(request).enqueue(new Callback(){

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(!response.isSuccessful()) {
                    if (response.code() == 429 || response.code() == 404) {// lỗi tràn request
                        tryModelSpeaking(question, answer, callback, attempt + 1);// gọi lại đệ qui để đổi sang model khác
                    }else {
                        callback.onError("Error " + response.code());
                    }
                return;
                }
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    String repy = json.getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text");
                    callback.onSuccess(repy);
                }catch (JSONException e){
                    callback.onError(e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError(e.getMessage());
            }
        });

    }
    private void tryModel(String message,
                          OpenAICallBack callback,
                          int attempt) {

        if (attempt >= MODELS.length) {
            callback.onError("All Gemini models are busy. Please try again later.");
            return;
        }// nếu thử các model trên nếu số laafn thử nhiều hơn các model thì request bị chặn -> lỗi

        String model = MODELS[index.getAndIncrement() % MODELS.length];
        String url = String.format(BASE_URL, model, GEMINI_API_KEY);

        try {
            JSONObject body = new JSONObject();
            JSONArray contents = new JSONArray();

            JSONObject content = new JSONObject();
            content.put("role", "user");

            JSONArray parts = new JSONArray();
            String prompt =
                    "You are a friendly English learning assistant.\n\n" +

                            "Your role:\n" +
                            "- Chat naturally like a real person\n" +
                            "- Help users learn English through conversation\n" +
                            "- Correct English mistakes gently when needed\n" +
                            "- Explain in simple terms\n" +
                            "- Give advice related to studying, daily life, or communication\n\n" +

                            "Language rules:\n" +
                            "- If the user writes in Vietnamese, reply in Vietnamese\n" +
                            "- If the user writes in English, reply in English\n" +
                            "- If the user mixes both, respond bilingually when helpful\n\n" +

                            "Style:\n" +
                            "- Be friendly, supportive, and interactive\n\n" +

                            "User message:\n" +
                            message;

            parts.put(new JSONObject().put(
                    "text", prompt
            ));

            content.put("parts", parts);
            contents.put(content);
            body.put("contents", contents);

            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(body.toString(), JSON))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call,
                                       @NonNull Response response) throws IOException {

                    if (!response.isSuccessful()) {
                        if (response.code() == 429 || response.code() == 404) {
                            tryModel(message, callback, attempt + 1);// gọi đệ qui nếu quá tại request
                        } else {
                            callback.onError("Error " + response.code());
                        }
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        String reply = json
                                .getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");

                        callback.onSuccess(reply);

                    } catch (JSONException e) {
                        callback.onError(e.getMessage());
                    }
                }
            });

        } catch (JSONException e) {
            callback.onError(e.getMessage());
        }
    }
}

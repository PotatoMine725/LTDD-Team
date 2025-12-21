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

    public void sendMessage(String message, OpenAICallBack callback) {
        tryModel(message, callback, 0);
    }

    private void tryModel(String message,
                          OpenAICallBack callback,
                          int attempt) {

        if (attempt >= MODELS.length) {
            callback.onError("All Gemini models are busy. Please try again later.");
            return;
        }

        String model = MODELS[index.getAndIncrement() % MODELS.length];
        String url = String.format(BASE_URL, model, GEMINI_API_KEY);

        try {
            JSONObject body = new JSONObject();
            JSONArray contents = new JSONArray();

            JSONObject content = new JSONObject();
            content.put("role", "user");

            JSONArray parts = new JSONArray();
            parts.put(new JSONObject().put(
                    "text",
                    "Correct this English sentence and explain simply:\n" + message
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
                            tryModel(message, callback, attempt + 1);
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

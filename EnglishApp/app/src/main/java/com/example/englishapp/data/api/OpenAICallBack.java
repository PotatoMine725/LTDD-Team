package com.example.englishapp.data.api;

public interface OpenAICallBack {
    void onSuccess (String reply);
    void onError (String error);
}

package com.example.englishapp.utils;

import android.content.Intent;
import android.speech.RecognizerIntent;

import java.util.Locale;
// thư viện hỗ trợ chuyển giọng nói thành văn bản
public class SpeechToTextHelper {
    public static Intent create(){
        //RecognizerIntent.ACTION_RECOGNIZE_SPEECH mở ra trang chuyển dọng nois thành văn bản
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, // cho phép nói tự do trong ngôn ngữ
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); // ngôn ngữ tự do
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, // ngôn ngữ
                Locale.ENGLISH);// tiếng anh
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, // thông báo trên màn hình
                "Speak now!");
        return intent;
    }
}

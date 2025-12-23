package com.example.englishapp.ui.vocabulary;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Lưu tiến độ học vocab theo topic bằng SharedPreferences.
 * Key: topicId -> set các từ tiếng Anh đã save.
 */
public class VocabProgressStore {
    private static final String PREF_NAME = "vocab_progress";

    public static Set<String> getSavedWords(Context ctx, String topicId) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return new HashSet<>(sp.getStringSet(topicId, new HashSet<>()));
    }
    public static void addWord(Context ctx, String topicId, String english) {
        if (english == null || english.isEmpty()) return;
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> set = new HashSet<>(sp.getStringSet(topicId, new HashSet<>()));
        set.add(english);
        sp.edit().putStringSet(topicId, set).apply();
    }
}


package com.example.englishapp.service;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Singleton service để quản lý Firebase Database connections
 */
public class FirebaseService {
    private static final String TAG = "FirebaseService";
    private static final String DATABASE_URL = "https://englishappdb-db02d-default-rtdb.asia-southeast1.firebasedatabase.app";
    
    private static FirebaseService instance;
    private FirebaseDatabase database;
    private DatabaseReference rootRef;
    
    private FirebaseService() {
        try {
            database = FirebaseDatabase.getInstance(DATABASE_URL);
            rootRef = database.getReference();
            
            // Enable offline persistence
            database.setPersistenceEnabled(true);
            
            Log.d(TAG, "Firebase service initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase service", e);
        }
    }
    
    public static synchronized FirebaseService getInstance() {
        if (instance == null) {
            instance = new FirebaseService();
        }
        return instance;
    }
    
    public DatabaseReference getDatabase() {
        return rootRef;
    }
    
    public DatabaseReference getTopicsRef() {
        return rootRef.child("topics");
    }
    
    public DatabaseReference getListeningTopicsRef() {
        return getTopicsRef().child("listening");
    }
    
    public DatabaseReference getTopicRef(String topicId) {
        return getListeningTopicsRef().child(topicId);
    }
    
    public DatabaseReference getLessonsRef(String topicId) {
        return getTopicRef(topicId).child("lessons");
    }
    
    public DatabaseReference getLessonRef(String topicId, String lessonId) {
        return getLessonsRef(topicId).child(lessonId);
    }
}
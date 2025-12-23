package com.example.englishapp;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class EnglishApplication extends Application {
    private static final String TAG = "EnglishApplication";
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        try {
            // Khởi tạo Firebase
            FirebaseApp.initializeApp(this);
            
            // Enable offline persistence
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            
            Log.d(TAG, "Firebase initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase", e);
        }
    }
}
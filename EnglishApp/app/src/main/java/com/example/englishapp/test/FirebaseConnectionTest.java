package com.example.englishapp.test;

import android.util.Log;

import com.example.englishapp.service.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Simple test class để kiểm tra Firebase connection
 */
public class FirebaseConnectionTest {
    private static final String TAG = "FirebaseTest";
    
    public static void testBasicConnection() {
        Log.d(TAG, "=== TESTING FIREBASE CONNECTION ===");
        
        FirebaseService firebaseService = FirebaseService.getInstance();
        
        // Test 1: Check root connection
        firebaseService.getDatabase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "✅ ROOT CONNECTION SUCCESS");
                Log.d(TAG, "Root exists: " + dataSnapshot.exists());
                Log.d(TAG, "Root children count: " + dataSnapshot.getChildrenCount());
                
                // Test 2: Check topics
                testTopicsConnection();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "❌ ROOT CONNECTION FAILED: " + databaseError.getMessage());
            }
        });
    }
    
    private static void testTopicsConnection() {
        Log.d(TAG, "=== TESTING TOPICS CONNECTION ===");
        
        FirebaseService firebaseService = FirebaseService.getInstance();
        
        firebaseService.getListeningTopicsRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "✅ TOPICS CONNECTION SUCCESS");
                Log.d(TAG, "Topics exists: " + dataSnapshot.exists());
                Log.d(TAG, "Topics children count: " + dataSnapshot.getChildrenCount());
                
                for (DataSnapshot topicSnapshot : dataSnapshot.getChildren()) {
                    String topicId = topicSnapshot.getKey();
                    String topicName = topicSnapshot.child("name").getValue(String.class);
                    String imageUrl = topicSnapshot.child("image_url").getValue(String.class);
                    Log.d(TAG, "Found topic: " + topicId + " - " + topicName);
                    Log.d(TAG, "  Image URL: " + imageUrl);
                    
                    // Test lessons for this topic
                    testLessonsForTopic(topicId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "❌ TOPICS CONNECTION FAILED: " + databaseError.getMessage());
            }
        });
    }
    
    private static void testLessonsForTopic(String topicId) {
        Log.d(TAG, "=== TESTING LESSONS FOR TOPIC: " + topicId + " ===");
        
        FirebaseService firebaseService = FirebaseService.getInstance();
        
        firebaseService.getLessonsRef(topicId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "✅ LESSONS CONNECTION SUCCESS for " + topicId);
                Log.d(TAG, "Lessons exists: " + dataSnapshot.exists());
                Log.d(TAG, "Lessons count: " + dataSnapshot.getChildrenCount());
                
                for (DataSnapshot lessonSnapshot : dataSnapshot.getChildren()) {
                    String lessonId = lessonSnapshot.getKey();
                    String lessonTitle = lessonSnapshot.child("title").getValue(String.class);
                    String imageUrl = lessonSnapshot.child("image_url").getValue(String.class);
                    
                    // Check if lesson has questions
                    DataSnapshot questionsSnapshot = lessonSnapshot.child("questions");
                    boolean hasQuestions = questionsSnapshot.exists() && questionsSnapshot.getChildrenCount() > 0;
                    
                    Log.d(TAG, "Lesson: " + lessonId + " - " + lessonTitle);
                    Log.d(TAG, "  Image URL: " + imageUrl);
                    Log.d(TAG, "  Has questions: " + hasQuestions + " (count: " + questionsSnapshot.getChildrenCount() + ")");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "❌ LESSONS CONNECTION FAILED for " + topicId + ": " + databaseError.getMessage());
            }
        });
    }
}
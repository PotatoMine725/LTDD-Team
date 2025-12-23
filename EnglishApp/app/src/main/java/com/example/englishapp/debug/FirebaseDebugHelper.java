package com.example.englishapp.debug;

import android.util.Log;

import com.example.englishapp.service.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Helper class để debug Firebase connections
 */
public class FirebaseDebugHelper {
    private static final String TAG = "FirebaseDebug";
    
    /**
     * Test Firebase connection và log structure
     */
    public static void testFirebaseConnection() {
        Log.d(TAG, "Testing Firebase connection...");
        
        FirebaseService firebaseService = FirebaseService.getInstance();
        
        // Test connection to root
        firebaseService.getDatabase().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "✅ Firebase connection successful!");
                Log.d(TAG, "Root exists: " + dataSnapshot.exists());
                
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "Root children count: " + dataSnapshot.getChildrenCount());
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Log.d(TAG, "Root child: " + child.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "❌ Firebase connection failed: " + databaseError.getMessage());
                Log.e(TAG, "Error details: " + databaseError.getDetails());
            }
        });
    }
    
    /**
     * Test specific topic data
     */
    public static void testTopicData(String topicId) {
        Log.d(TAG, "Testing topic data for: " + topicId);
        
        FirebaseService firebaseService = FirebaseService.getInstance();
        
        firebaseService.getTopicRef(topicId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "✅ Topic data loaded for: " + topicId);
                Log.d(TAG, "Topic exists: " + dataSnapshot.exists());
                
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "Topic children count: " + dataSnapshot.getChildrenCount());
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Log.d(TAG, "Topic child: " + child.getKey());
                    }
                    
                    // Check lessons
                    DataSnapshot lessonsSnapshot = dataSnapshot.child("lessons");
                    if (lessonsSnapshot.exists()) {
                        Log.d(TAG, "Lessons count: " + lessonsSnapshot.getChildrenCount());
                        for (DataSnapshot lesson : lessonsSnapshot.getChildren()) {
                            String title = lesson.child("title").getValue(String.class);
                            Log.d(TAG, "Lesson: " + lesson.getKey() + " - " + title);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "❌ Topic data failed for " + topicId + ": " + databaseError.getMessage());
            }
        });
    }
}
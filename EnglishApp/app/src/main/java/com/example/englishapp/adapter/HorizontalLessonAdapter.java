package com.example.englishapp.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.englishapp.R;
import com.example.englishapp.model.ListeningLesson;

import java.util.List;

public class HorizontalLessonAdapter extends RecyclerView.Adapter<HorizontalLessonAdapter.LessonViewHolder> {

    private static final String TAG = "HorizontalLessonAdapter";
    private List<ListeningLesson> lessonList;
    private OnLessonClickListener listener;

    public interface OnLessonClickListener {
        void onLessonClick(ListeningLesson lesson, int position);
    }

    public HorizontalLessonAdapter(List<ListeningLesson> lessonList) {
        this.lessonList = lessonList;
    }

    public void setOnLessonClickListener(OnLessonClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_listening_track, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        ListeningLesson lesson = lessonList.get(position);
        holder.bind(lesson, position);
    }

    @Override
    public int getItemCount() {
        return lessonList != null ? lessonList.size() : 0;
    }

    public void updateData(List<ListeningLesson> newLessonList) {
        this.lessonList = newLessonList;
        notifyDataSetChanged();
    }

    class LessonViewHolder extends RecyclerView.ViewHolder {
        private ImageView lessonImage;
        private TextView lessonTitle;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            // Tìm views trong list_item_listening_track.xml
            lessonImage = itemView.findViewById(R.id.track_image);
            lessonTitle = itemView.findViewById(R.id.track_title);
        }

        public void bind(ListeningLesson lesson, int position) {
            try {
                // Set lesson title
                if (lessonTitle != null) {
                    lessonTitle.setText(lesson.getTitle());
                }

                // Set lesson image từ URL
                if (lessonImage != null) {
                    if (lesson.getImageUrl() != null && !lesson.getImageUrl().isEmpty()) {
                        Log.d(TAG, "Loading lesson image from URL: " + lesson.getImageUrl());
                        Glide.with(itemView.getContext())
                                .load(lesson.getImageUrl())
                                .placeholder(R.drawable.ic_idiom_animals)
                                .error(R.drawable.ic_idiom_animals)
                                .centerCrop()
                                .into(lessonImage);
                    } else {
                        lessonImage.setImageResource(R.drawable.ic_idiom_animals);
                    }
                }

                // Click listener
                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        Log.d(TAG, "Lesson clicked: " + lesson.getTitle() + " (ID: " + lesson.getLessonId() + ")");
                        listener.onLessonClick(lesson, position);
                    }
                });

                Log.d(TAG, "Bound lesson: " + lesson.getTitle() + " at position " + position);
            } catch (Exception e) {
                Log.e(TAG, "Error binding lesson at position " + position, e);
            }
        }
    }
}
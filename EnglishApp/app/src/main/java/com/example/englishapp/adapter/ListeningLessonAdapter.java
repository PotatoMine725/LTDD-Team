package com.example.englishapp.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.englishapp.R;
import com.example.englishapp.model.ListeningLesson;

import java.util.List;

public class ListeningLessonAdapter extends RecyclerView.Adapter<ListeningLessonAdapter.LessonViewHolder> {

    private static final String TAG = "ListeningLessonAdapter";
    private List<ListeningLesson> lessonList;
    private OnLessonClickListener listener;

    public interface OnLessonClickListener {
        void onLessonClick(ListeningLesson lesson, int position);
    }

    public ListeningLessonAdapter(List<ListeningLesson> lessonList) {
        this.lessonList = lessonList;
    }

    public void setOnLessonClickListener(OnLessonClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_lesson, parent, false);
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
        private CardView cardView;
        private ImageView lessonImage;
        private TextView lessonTitle;
        private TextView lessonStatus;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            lessonImage = itemView.findViewById(R.id.lesson_image);
            lessonTitle = itemView.findViewById(R.id.lesson_title);
            lessonStatus = itemView.findViewById(R.id.lesson_status);
        }

        public void bind(ListeningLesson lesson, int position) {
            try {
                lessonTitle.setText(lesson.getTitle());

                // Load ảnh từ URL
                if (lesson.getImageUrl() != null && !lesson.getImageUrl().isEmpty()) {
                    Glide.with(itemView.getContext())
                            .load(lesson.getImageUrl())
                            .placeholder(R.drawable.topic_technology)
                            .error(R.drawable.topic_technology)
                            .into(lessonImage);
                } else {
                    lessonImage.setImageResource(R.drawable.topic_technology);
                }

                // Hiển thị trạng thái
                if (lesson.hasContent()) {
                    lessonStatus.setText("Có bài học");
                    lessonStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
                } else {
                    lessonStatus.setText("Chưa có bài học");
                    lessonStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray));
                }

                // Click listener
                cardView.setOnClickListener(v -> {
                    if (listener != null) {
                        Log.d(TAG, "Lesson clicked: " + lesson.getTitle());
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


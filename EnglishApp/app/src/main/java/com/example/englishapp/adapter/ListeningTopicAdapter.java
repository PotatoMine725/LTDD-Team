package com.example.englishapp.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.example.englishapp.R;
import com.example.englishapp.model.ListeningTopic;

import java.util.List;

public class ListeningTopicAdapter extends RecyclerView.Adapter<ListeningTopicAdapter.TopicViewHolder> {

    private static final String TAG = "ListeningTopicAdapter";
    private List<ListeningTopic> topicList;
    private OnTopicClickListener listener;
    private OnProgressClickListener progressListener;

    public interface OnTopicClickListener {
        void onTopicClick(ListeningTopic topic, int position);
    }

    public interface OnProgressClickListener {
        void onProgressClick(ListeningTopic topic, int position);
    }

    public ListeningTopicAdapter(List<ListeningTopic> topicList) {
        this.topicList = topicList;
    }

    public void setOnTopicClickListener(OnTopicClickListener listener) {
        this.listener = listener;
    }

    public void setOnProgressClickListener(OnProgressClickListener progressListener) {
        this.progressListener = progressListener;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        ListeningTopic topic = topicList.get(position);
        holder.bind(topic, position);
    }

    @Override
    public int getItemCount() {
        return topicList != null ? topicList.size() : 0;
    }

    public void updateData(List<ListeningTopic> newTopicList) {
        this.topicList = newTopicList;
        notifyDataSetChanged();
    }

    /**
     * Kiểm tra URL ảnh có hợp lệ không
     */
    private boolean isValidImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        // Kiểm tra URL có bắt đầu bằng http/https không
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return false;
        }
        
        // Cho phép tất cả domain để test
        return true;
        
        // Kiểm tra domain có đáng tin cậy không (tạm comment để test)
        /*
        return url.contains("unsplash.com") || 
               url.contains("images.unsplash.com") ||
               url.contains("cloudinary.com") ||
               url.contains("imgur.com") ||
               url.contains("atlasoftware.vn");
        */
    }

    class TopicViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView topicImage;
        private TextView topicTitleText;
        private TextView topicProgressText;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            topicImage = itemView.findViewById(R.id.iv_topic);
            topicTitleText = itemView.findViewById(R.id.topic_title_text);
            topicProgressText = itemView.findViewById(R.id.topic_progress_text);
        }

        public void bind(ListeningTopic topic, int position) {
            try {
                // Set topic title
                topicTitleText.setText(topic.getTopicName());

                // Set progress
                topicProgressText.setText(topic.getProgressText());

                // Log thông tin topic để debug
                Log.d(TAG, "=== BINDING TOPIC AT POSITION " + position + " ===");
                Log.d(TAG, "Topic ID: " + topic.getTopicId());
                Log.d(TAG, "Topic Name: " + topic.getTopicName());
                Log.d(TAG, "Has Image URL: " + topic.hasImageUrl());
                Log.d(TAG, "Image URL: '" + topic.getImageUrl() + "'");
                Log.d(TAG, "Image Resource ID: " + topic.getImageResourceId());

                // Clear previous image first
                topicImage.setImageDrawable(null);

                // Set image từ URL hoặc fallback resource
                if (topic.hasImageUrl()) {
                    Log.d(TAG, "Loading image from URL: " + topic.getImageUrl());
                    try {
                        String imageUrl = topic.getImageUrl();
                        Log.d(TAG, "Attempting to load image: " + imageUrl);
                        
                        Glide.with(itemView.getContext())
                                .load(imageUrl)
                                .placeholder(R.drawable.topic_technology)
                                .error(R.drawable.topic_technology)
                                .centerCrop()
                                .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                                        Log.e(TAG, "Glide FAILED to load image for position " + position + ": " + imageUrl, e);
                                        return false; // Let Glide handle the error drawable
                                    }

                                    @Override
                                    public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                        Log.d(TAG, "Glide SUCCESS loaded image for position " + position + ": " + imageUrl);
                                        return false; // Let Glide handle the resource
                                    }
                                })
                                .into(topicImage);
                    } catch (Exception glideException) {
                        Log.e(TAG, "Glide error loading image: " + topic.getImageUrl(), glideException);
                        topicImage.setImageResource(R.drawable.topic_technology);
                    }
                } else {
                    // Fallback to resource ID nếu không có URL
                    Log.d(TAG, "No image URL found for topic: " + topic.getTopicName() + ", using resource image");
                    topicImage.setImageResource(topic.getImageResourceId() != 0 ? 
                            topic.getImageResourceId() : R.drawable.topic_technology);
                }

                // Click listener cho toàn bộ card
                cardView.setOnClickListener(v -> {
                    if (listener != null) {
                        Log.d(TAG, "Card clicked: " + topic.getTopicName());
                        listener.onTopicClick(topic, position);
                    }
                });

                // Click listener riêng cho progress text để chuyển sang exercise
                topicProgressText.setOnClickListener(v -> {
                    if (progressListener != null) {
                        Log.d(TAG, "Progress clicked: " + topic.getTopicName() +
                                " - Progress: " + topic.getProgressText());
                        // Stop propagation để không trigger card click
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        progressListener.onProgressClick(topic, position);
                    }
                });

                Log.d(TAG, "Successfully bound topic: " + topic.getTopicName() + " at position " + position);
                Log.d(TAG, "=== END BINDING ===");
            } catch (Exception e) {
                Log.e(TAG, "Error binding topic at position " + position, e);
                // Fallback: set default image nếu có lỗi
                topicImage.setImageResource(R.drawable.topic_technology);
            }
        }
    }
}
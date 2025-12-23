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

                // Set image
                topicImage.setImageResource(topic.getImageResourceId());

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

                Log.d(TAG, "Bound topic: " + topic.getTopicName() + " at position " + position);
            } catch (Exception e) {
                Log.e(TAG, "Error binding topic at position " + position, e);
            }
        }
    }
}
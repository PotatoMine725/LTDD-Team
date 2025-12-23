package com.example.englishapp.ui.speaking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.data.model.SpeakingTopic;

import java.util.ArrayList;
import java.util.List;

public class SpeakingTopicAdapter extends RecyclerView.Adapter<SpeakingTopicAdapter.TopicViewHolder> {
    public interface OnTopicClickListener{
        void onTopicClick(SpeakingTopic topic);
    }
    private final List<SpeakingTopic> topics = new ArrayList<>();
    private final OnTopicClickListener listener;
    public SpeakingTopicAdapter(List<SpeakingTopic> topics,
            OnTopicClickListener listener){
        this.topics.addAll(topics);
        this.listener = listener;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_speaking_topic, parent, false);

        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpeakingTopicAdapter.TopicViewHolder holder, int position) {
        SpeakingTopic topic = topics.get(position);
        holder.tvName.setText(topic.name);
        holder.tvDesc.setText("Practice speaking with this topic");
        // load ảnh
        Context context = holder.itemView.getContext();
        int imgRes = context.getResources().getIdentifier(
                topic.image_res_name,
                "drawable",
                context.getPackageName()
        );
        if(imgRes != 0) {
            holder.img.setImageResource(imgRes);
        };
        holder.itemView.setOnClickListener(v -> listener.onTopicClick(topic)); //gọi call back
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc;
        ImageView img;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTopicName);
            tvDesc = itemView.findViewById(R.id.tvTopicDesc);
            img = itemView.findViewById(R.id.imgTopic);
        }
    }
}

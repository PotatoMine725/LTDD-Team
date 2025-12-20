package com.example.englishapp.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.data.model.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_USER = 0;
    public static final int TYPE_AI = 1;
    private final List<ChatMessage> messages = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    public void submit(List<ChatMessage> list) {
        messages.clear();
        if(list != null)
            messages.addAll(list);
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getViewType(); // lấy type (user hoặc là ai)
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_USER) {
            View view = inflater.inflate(
                    R.layout.item_message_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = inflater.inflate(
                    R.layout.item_message_ai, parent, false);
            return new AiViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

package com.example.englishapp.ui.vocabulary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Hiển thị danh sách tiến độ các topic vocab: đã save bao nhiêu / tổng.
 */
public class VocabProgressFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressAdapter adapter;
    private final List<TopicProgress> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vocab_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rv_progress);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ProgressAdapter(items);
        recyclerView.setAdapter(adapter);

        View backBtn = view.findViewById(R.id.btn_back_progress);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                fm.popBackStack();
            });
        }

        loadProgress();
    }

    private void loadProgress() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("topics").child("vocabulary");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                Map<String, Integer> totalMap = new HashMap<>();
                for (DataSnapshot topicSnap : snapshot.getChildren()) {
                    String topicId = topicSnap.getKey();
                    String name = topicSnap.child("name").getValue(String.class);
                    int total = (int) topicSnap.child("words").getChildrenCount();
                    totalMap.put(topicId, total);

                    if (topicId != null) {
                        items.add(new TopicProgress(topicId, name != null ? name : topicId, total));
                    }
                }

                // Cập nhật saved count
                for (TopicProgress tp : items) {
                    Set<String> saved = VocabProgressStore.getSavedWords(requireContext(), tp.topicId);
                    tp.saved = saved.size();
                    tp.completed = tp.saved >= tp.total && tp.total > 0;
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Không thể tải tiến độ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class TopicProgress {
        String topicId;
        String name;
        int total;
        int saved;
        boolean completed;

        TopicProgress(String topicId, String name, int total) {
            this.topicId = topicId;
            this.name = name;
            this.total = total;
        }
    }

    static class ProgressAdapter extends RecyclerView.Adapter<ProgressViewHolder> {
        private final List<TopicProgress> data;

        ProgressAdapter(List<TopicProgress> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vocab_progress, parent, false);
            return new ProgressViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
            holder.bind(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private final android.widget.TextView tvName;
        private final android.widget.TextView tvProgress;
        private final android.widget.ImageView ivStatus;

        ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_topic_name);
            tvProgress = itemView.findViewById(R.id.tv_topic_progress);
            ivStatus = itemView.findViewById(R.id.iv_status);
        }

        void bind(TopicProgress tp) {
            tvName.setText(tp.name);
            tvProgress.setText(tp.saved + " / " + tp.total);
            ivStatus.setVisibility(tp.completed ? View.VISIBLE : View.INVISIBLE);
        }
    }
}


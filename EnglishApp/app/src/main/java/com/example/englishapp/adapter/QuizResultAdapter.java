package com.example.englishapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.model.QuizQuestion;

import java.util.List;

public class QuizResultAdapter extends RecyclerView.Adapter<QuizResultAdapter.ViewHolder> {

    private List<QuizQuestion> questionList;

    // Constructor nhận 1 tham số (Khớp với Activity gọi ở trên)
    public QuizResultAdapter(List<QuizQuestion> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz_result_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizQuestion question = questionList.get(position);

        // Hiển thị câu hỏi
        holder.tvQuestion.setText("Q" + (position + 1) + ": " + question.getQuestion());

        int userIndex = question.getUserSelectedIndex();
        int correctIndex = question.getCorrectIndex();
        List<String> options = question.getOptions();

        // Kiểm tra an toàn dữ liệu
        if (options != null && !options.isEmpty()) {

            // Xử lý hiển thị đáp án người dùng chọn
            if (userIndex >= 0 && userIndex < options.size()) {
                String selectedText = options.get(userIndex);

                if (userIndex == correctIndex) {
                    // Đúng
                    holder.tvYourAnswer.setText("Your answer: " + selectedText);
                    holder.tvYourAnswer.setTextColor(Color.parseColor("#4CAF50")); // Màu xanh
                    holder.ivStatus.setImageResource(R.drawable.ic_check_circle); // Icon check
                    holder.ivStatus.setColorFilter(Color.parseColor("#4CAF50"));
                    holder.tvCorrectAnswer.setVisibility(View.GONE); // Ẩn đáp án đúng vì đã chọn đúng
                } else {
                    // Sai
                    holder.tvYourAnswer.setText("Your answer: " + selectedText);
                    holder.tvYourAnswer.setTextColor(Color.RED);
                    holder.ivStatus.setImageResource(android.R.drawable.ic_delete); // Dùng icon có sẵn hoặc R.drawable.ic_close
                    holder.ivStatus.setColorFilter(Color.RED);

                    // Hiện đáp án đúng
                    holder.tvCorrectAnswer.setVisibility(View.VISIBLE);
                    if (correctIndex >= 0 && correctIndex < options.size()) {
                        holder.tvCorrectAnswer.setText("Correct answer: " + options.get(correctIndex));
                    }
                }
            } else {
                // Không chọn (Bỏ qua)
                holder.tvYourAnswer.setText("Your answer: None");
                holder.tvYourAnswer.setTextColor(Color.GRAY);
                holder.ivStatus.setImageResource(android.R.drawable.ic_delete);
                holder.ivStatus.setColorFilter(Color.GRAY);

                holder.tvCorrectAnswer.setVisibility(View.VISIBLE);
                if (correctIndex >= 0 && correctIndex < options.size()) {
                    holder.tvCorrectAnswer.setText("Correct answer: " + options.get(correctIndex));
                }
        // 2. Xử lý hiển thị đáp án
        if (userIndex != -1 && userIndex < options.size()) {
            String userAnsText = options.get(userIndex);
            holder.tvYourAnswer.setText("Your answer: " + userAnsText);

            if (userIndex == correctIndex) {
                holder.tvYourAnswer.setTextColor(Color.parseColor("#4CAF50"));
                holder.tvCorrectAnswer.setVisibility(View.GONE);
                holder.ivStatus.setImageResource(R.drawable.ic_check_circle);
                holder.ivStatus.setColorFilter(Color.parseColor("#4CAF50"));
            } else {
                holder.tvYourAnswer.setTextColor(Color.RED);
                holder.tvCorrectAnswer.setVisibility(View.VISIBLE);
                holder.tvCorrectAnswer.setText("Correct answer: " + options.get(correctIndex));
                holder.ivStatus.setImageResource(R.drawable.ic_exit);
                holder.ivStatus.setColorFilter(Color.RED);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (questionList != null) ? questionList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvYourAnswer, tvCorrectAnswer;
        ImageView ivStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Các ID này phải khớp với file layout: item_quiz_result_review.xml
            tvQuestion = itemView.findViewById(R.id.tv_review_question);
            tvYourAnswer = itemView.findViewById(R.id.tv_your_answer);
            tvCorrectAnswer = itemView.findViewById(R.id.tv_correct_answer);
            ivStatus = itemView.findViewById(R.id.iv_result_status);
        }
    }
}
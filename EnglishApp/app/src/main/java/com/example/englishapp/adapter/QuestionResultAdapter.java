package com.example.englishapp.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.model.QuestionResult;

import java.util.List;

public class QuestionResultAdapter extends RecyclerView.Adapter<QuestionResultAdapter.ResultViewHolder> {

    private static final String TAG = "QuestionResultAdapter";
    private List<QuestionResult> results;

    public QuestionResultAdapter(List<QuestionResult> results) {
        this.results = results;
        Log.d(TAG, "Adapter created with " + (results != null ? results.size() : 0) + " items");
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_result, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        QuestionResult result = results.get(position);
        Log.d(TAG, "Binding item at position " + position + ": Q" + result.getQuestionNumber());
        holder.bind(result);
    }

    @Override
    public int getItemCount() {
        int count = results != null ? results.size() : 0;
        Log.d(TAG, "getItemCount() returning: " + count);
        return count;
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        private TextView questionNumber;
        private TextView questionText;
        private TextView userAnswer;
        private TextView correctAnswer;
        private ImageView resultIcon;
        private LinearLayout correctAnswerLayout;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            questionNumber = itemView.findViewById(R.id.question_number);
            questionText = itemView.findViewById(R.id.question_text);
            userAnswer = itemView.findViewById(R.id.user_answer);
            correctAnswer = itemView.findViewById(R.id.correct_answer);
            resultIcon = itemView.findViewById(R.id.result_icon);
            correctAnswerLayout = itemView.findViewById(R.id.correct_answer_layout);
        }

        public void bind(QuestionResult result) {
            Log.d(TAG, "Binding result: Q" + result.getQuestionNumber() + " - " + result.getQuestionText());
            
            questionNumber.setText("Question " + result.getQuestionNumber());
            questionText.setText(result.getQuestionText());
            userAnswer.setText(result.getUserAnswer());

            if (result.isCorrect()) {
                // Correct answer
                Log.d(TAG, "Question " + result.getQuestionNumber() + " is CORRECT");
                resultIcon.setImageResource(R.drawable.ic_check_green);
                userAnswer.setTextColor(itemView.getContext().getColor(android.R.color.holo_green_dark));
                correctAnswerLayout.setVisibility(View.GONE);
            } else {
                // Wrong answer
                Log.d(TAG, "Question " + result.getQuestionNumber() + " is WRONG");
                resultIcon.setImageResource(R.drawable.ic_exit);
                userAnswer.setTextColor(itemView.getContext().getColor(android.R.color.holo_red_dark));
                correctAnswerLayout.setVisibility(View.VISIBLE);
                correctAnswer.setText(result.getCorrectAnswer());
            }
        }
    }
}
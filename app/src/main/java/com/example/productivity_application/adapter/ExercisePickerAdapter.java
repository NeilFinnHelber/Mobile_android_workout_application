package com.example.productivity_application.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productivity_application.db.entity.workout_exercise;

import java.util.ArrayList;
import java.util.List;

public class ExercisePickerAdapter extends RecyclerView.Adapter<ExercisePickerAdapter.ViewHolder> {

    public interface OnExerciseClickListener {
        void onExerciseClick(workout_exercise exercise);
    }

    private List<workout_exercise> exercises = new ArrayList<>();
    private final OnExerciseClickListener listener;

    public ExercisePickerAdapter(OnExerciseClickListener listener) {
        this.listener = listener;
    }

    public void setExercises(List<workout_exercise> exercises) {
        this.exercises = exercises != null ? exercises : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        workout_exercise exercise = exercises.get(position);
        holder.textView.setText(exercise.description);
        holder.itemView.setOnClickListener(v -> listener.onExerciseClick(exercise));
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(View v) {
            super(v);
            textView = v.findViewById(android.R.id.text1);
        }
    }
}

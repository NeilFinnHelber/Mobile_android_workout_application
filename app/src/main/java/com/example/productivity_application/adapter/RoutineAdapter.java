package com.example.productivity_application.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productivity_application.R;
import com.example.productivity_application.db.entity.workout_sports_routine;
import com.example.productivity_application.db.relation.RoutineWithSessions;

import java.util.ArrayList;
import java.util.List;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.TaskViewHolder> {

    private List<RoutineWithSessions> routines = new ArrayList<>();
    private final OnRoutineClickListener listener;
    private int selectedRoutineId = -1;

    public interface OnRoutineClickListener {
        void onRoutineClicked(workout_sports_routine item);
    }

    public RoutineAdapter(OnRoutineClickListener listener) {
        this.listener = listener;
    }

    public void setSelectedRoutineId(int id) {
        this.selectedRoutineId = id;
        notifyDataSetChanged();
    }

    public void submitList(List<RoutineWithSessions> newList) {
        routines = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_routine, parent, false);
        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        RoutineWithSessions item = routines.get(position);
        if (item != null && item.routine != null) {
            boolean isSelected = item.routine.routine_id == selectedRoutineId;
            holder.bind(item.routine, listener, isSelected);
        }
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        private final TextView textviewTitle;

        TaskViewHolder(@NonNull View view) {
            super(view);
            textviewTitle = view.findViewById(R.id.routine_title);
        }

        void bind(workout_sports_routine item, OnRoutineClickListener listener, boolean isSelected) {
            textviewTitle.setText(item.name);
            
            // Visual feedback for selection
            if (isSelected) {
                // Highlighted color (Primary)
                textviewTitle.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(itemView.getContext(), com.google.android.material.R.color.design_default_color_primary)));
                textviewTitle.setAlpha(1.0f);
            } else {
                // Unselected color (Grayish)
                textviewTitle.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#444444")));
                textviewTitle.setAlpha(0.6f);
            }

            // Explicitly set click listener on the title view
            textviewTitle.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRoutineClicked(item);
                }
            });
            
            // Also set it on the whole item container for better touch target
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRoutineClicked(item);
                }
            });
        }
    }
}

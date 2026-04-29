package com.example.productivity_application.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productivity_application.R;
import com.example.productivity_application.db.entity.workout_session;
import com.example.productivity_application.db.entity.workout_session_log;
import com.example.productivity_application.db.entity.workout_sports_routine;
import com.example.productivity_application.db.relation.SessionWithLogs;

import java.util.ArrayList;
import java.util.List;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.TaskViewHolder> {

    public interface OnTaskClickListener {
        void onTaskClick(workout_session item);

        void onTaskChecked(workout_session item, boolean isChecked);

        void onRoutineClicked(workout_sports_routine item);
    }


    private List<SessionWithLogs> sessions = new ArrayList<>();
    private final OnTaskClickListener listener;

    public SessionAdapter(OnTaskClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<SessionWithLogs> newList) {
        sessions = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_task, parent, false);
        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        SessionWithLogs item = sessions.get(position);
        holder.bind(item.session, item.log, listener);
    }


    @Override
    public int getItemCount() {
        return sessions.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        private final View priorityBar;
        private final CheckBox checkBox;
        private final TextView textviewTitle;

        TaskViewHolder(@NonNull View view) {
            super(view);
            priorityBar = view.findViewById(R.id.session_priority_bar);
            checkBox = view.findViewById(R.id.session_checkbox);
            textviewTitle = view.findViewById(R.id.session_task_title);
        }

        void bind(workout_session item, workout_session_log logItem, OnTaskClickListener listener) {
            textviewTitle.setText(item.name);
            
            // Check if logItem is null to avoid NullPointerException
            boolean isCompleted = logItem != null && logItem.isCompleted;

            if (isCompleted) {
                textviewTitle.setPaintFlags(textviewTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                textviewTitle.setAlpha(0.5f);
            } else {
                textviewTitle.setPaintFlags(textviewTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                textviewTitle.setAlpha(1f);
            }

            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(isCompleted);
            checkBox.setOnCheckedChangeListener((btn, isChecked) -> {
                listener.onTaskChecked(item, isChecked);
            });


            itemView.setOnClickListener(v -> listener.onTaskClick(item));

        }
        private static int adjustAlpha(int color, float factor) {
            int alpha = Math.round(Color.alpha(color) * factor);
            return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
        }
    }
}

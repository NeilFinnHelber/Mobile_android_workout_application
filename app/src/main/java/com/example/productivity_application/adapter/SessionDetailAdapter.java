package com.example.productivity_application.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productivity_application.R;
import com.example.productivity_application.db.entity.workout_category;
import com.example.productivity_application.db.entity.workout_exercise;
import com.example.productivity_application.db.entity.workout_option_log;
import com.example.productivity_application.db.entity.workout_session;
import com.example.productivity_application.db.relation.OptionsWithLogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SessionDetailAdapter extends RecyclerView.Adapter<SessionDetailAdapter.DetailViewHolder> {

    private List<workout_exercise> exercises = new ArrayList<>();
    private workout_session currentSession;
    private List<workout_category> categories = new ArrayList<>();
    private List<OptionsWithLogs> optionsWithLogs = new ArrayList<>();

    public void setData(List<workout_exercise> exercises, workout_session session, 
                        List<workout_category> categories, List<OptionsWithLogs> optionsWithLogs) {
        this.exercises = exercises != null ? exercises : new ArrayList<>();
        this.currentSession = session;
        this.categories = categories != null ? categories : new ArrayList<>();
        this.optionsWithLogs = optionsWithLogs != null ? optionsWithLogs : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise_table, parent, false);
        return new DetailViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        workout_exercise exercise = exercises.get(position);
        holder.bind(exercise, categories, optionsWithLogs);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class DetailViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseName;
        TableLayout tableContent;

        DetailViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.tv_exercise_name);
            tableContent = itemView.findViewById(R.id.table_content);
        }

        void bind(workout_exercise exercise, List<workout_category> allCategories, List<OptionsWithLogs> sessionOptions) {
            tvExerciseName.setText(exercise.description);
            tableContent.removeAllViews();

            Context context = itemView.getContext();
            
            Map<Integer, String> categoryMap = allCategories.stream()
                    .collect(Collectors.toMap(c -> c.category_id, c -> c.name));

            // Find options for this specific exercise
            List<OptionsWithLogs> exerciseOptions = sessionOptions.stream()
                    .filter(o -> o.option.exercise_id == exercise.exercise_id)
                    .collect(Collectors.toList());

            if (exerciseOptions.isEmpty()) {
                itemView.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.height = 0;
                itemView.setLayoutParams(params);
                return;
            } else {
                itemView.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                itemView.setLayoutParams(params);
            }

            for (OptionsWithLogs optWithLogs : exerciseOptions) {
                TableRow row = new TableRow(context);
                row.setBackgroundColor(Color.parseColor("#1A1A1A"));
                
                // Column 1: Category Name
                String catName = categoryMap.getOrDefault(optWithLogs.option.category_id, "Unknown");
                row.addView(createCell(context, catName, true));

                // Column 2: Details (Name, Sets, Reps, Weight)
                StringBuilder details = new StringBuilder();
                details.append(optWithLogs.option.name).append(":\n");
                
                if (optWithLogs.logs != null && !optWithLogs.logs.isEmpty()) {
                    workout_option_log latestLog = optWithLogs.logs.get(optWithLogs.logs.size() - 1);
                    int setsCount = latestLog.sets != null ? latestLog.sets.size() : 0;
                    details.append(setsCount).append(" sets\n");
                    details.append(latestLog.reps).append(" reps\n");
                    details.append(latestLog.weight).append(" kg");
                } else {
                    details.append("0 sets\n0 reps\n— kg");
                }
                
                row.addView(createCell(context, details.toString(), false));
                
                // Column 3: Empty
                row.addView(createCell(context, "", false));

                tableContent.addView(row);
                
                // Add a small divider between rows
                View divider = new View(context);
                divider.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
                divider.setBackgroundColor(Color.parseColor("#444444"));
                tableContent.addView(divider);
            }
        }

        private TextView createCell(Context context, String text, boolean isBold) {
            TextView tv = new TextView(context);
            tv.setText(text);
            tv.setPadding(32, 32, 32, 32);
            tv.setGravity(Gravity.START | Gravity.TOP);
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(14);
            
            if (isBold) {
                tv.setTypeface(null, Typeface.BOLD);
            }
            
            // Add border/background to cell
            tv.setBackgroundResource(R.drawable.cell_border);
            
            TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
            tv.setLayoutParams(params);
            
            return tv;
        }
    }
}

package com.example.productivity_application.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productivity_application.OptionDetailActivity;
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

    public interface OnAddOptionClickListener {
        void onAddOptionClick(workout_exercise exercise);
        void onOptionDoneChanged(workout_option_log log, boolean isDone);
    }

    private List<workout_exercise> exercises = new ArrayList<>();
    private workout_session currentSession;
    private List<workout_category> categories = new ArrayList<>();
    private List<OptionsWithLogs> optionsWithLogs = new ArrayList<>();
    private final OnAddOptionClickListener listener;

    public SessionDetailAdapter(OnAddOptionClickListener listener) {
        this.listener = listener;
    }

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
        return new DetailViewHolder(v, listener);
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
        Button btnAddOption;
        private final OnAddOptionClickListener listener;

        DetailViewHolder(@NonNull View itemView, OnAddOptionClickListener listener) {
            super(itemView);
            this.listener = listener;
            tvExerciseName = itemView.findViewById(R.id.tv_exercise_name);
            tableContent = itemView.findViewById(R.id.table_content);
            btnAddOption = itemView.findViewById(R.id.add_option_btn);
        }

        void bind(workout_exercise exercise, List<workout_category> allCategories, List<OptionsWithLogs> sessionOptions) {
            tvExerciseName.setText(exercise.description);
            tableContent.removeAllViews();
            
            btnAddOption.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddOptionClick(exercise);
                }
            });

            Context context = itemView.getContext();
            
            Map<Integer, String> categoryMap = allCategories.stream()
                    .collect(Collectors.toMap(c -> c.category_id, c -> c.name));

            List<OptionsWithLogs> exerciseOptions = sessionOptions.stream()
                    .filter(o -> o.option.exercise_id == exercise.exercise_id)
                    .collect(Collectors.toList());

            itemView.setVisibility(View.VISIBLE);

            for (OptionsWithLogs optWithLogs : exerciseOptions) {
                TableRow row = new TableRow(context);
                row.setBackgroundColor(Color.parseColor("#1A1A1A"));
                row.setClickable(true);
                row.setFocusable(true);
                
                row.setOnClickListener(v -> {
                    Intent intent = new Intent(context, OptionDetailActivity.class);
                    intent.putExtra(OptionDetailActivity.EXTRA_OPTION_ID, optWithLogs.option.option_id);
                    context.startActivity(intent);
                });
                
                // Column 1: Category Name + Checkbox
                LinearLayout categoryContainer = new LinearLayout(context);
                categoryContainer.setOrientation(LinearLayout.HORIZONTAL);
                categoryContainer.setGravity(Gravity.CENTER_VERTICAL);
                categoryContainer.setPadding(16, 0, 0, 0);
                categoryContainer.setBackgroundResource(R.drawable.cell_border);
                
                workout_option_log latestLog = (optWithLogs.logs != null && !optWithLogs.logs.isEmpty()) 
                        ? optWithLogs.logs.get(optWithLogs.logs.size() - 1) : null;

                CheckBox cb = new CheckBox(context);
                cb.setButtonTintList(android.content.res.ColorStateList.valueOf(Color.WHITE));
                if (latestLog != null) {
                    cb.setChecked(latestLog.completed);
                    cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (listener != null) {
                            listener.onOptionDoneChanged(latestLog, isChecked);
                        }
                    });
                }
                
                String catName = categoryMap.getOrDefault(optWithLogs.option.category_id, "Unknown");
                TextView tvCat = createCell(context, catName, true);
                tvCat.setBackground(null); // Remove border from textview itself
                
                categoryContainer.addView(cb);
                categoryContainer.addView(tvCat);
                
                TableRow.LayoutParams lp1 = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.4f);
                categoryContainer.setLayoutParams(lp1);
                row.addView(categoryContainer);

                // Column 2: Details
                StringBuilder details = new StringBuilder();
                details.append(optWithLogs.option.name).append(":\n");
                
                if (latestLog != null) {
                    int setsCount = latestLog.sets != null ? latestLog.sets.size() : 0;
                    details.append(setsCount).append(" sets\n");
                    details.append(latestLog.reps).append(" reps\n");
                    details.append(latestLog.weight).append(" kg");
                } else {
                    details.append("0 sets\n0 reps\n— kg");
                }
                
                TextView tvDetails = createCell(context, details.toString(), false);
                TableRow.LayoutParams lp2 = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.6f);
                tvDetails.setLayoutParams(lp2);
                row.addView(tvDetails);

                tableContent.addView(row);
                
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
            
            tv.setBackgroundResource(R.drawable.cell_border);
            return tv;
        }
    }
}

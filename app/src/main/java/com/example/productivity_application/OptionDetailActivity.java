package com.example.productivity_application;

import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productivity_application.databinding.ActivityOptionDetailBinding;
import com.example.productivity_application.databinding.ItemSetTodoBinding;
import com.example.productivity_application.db.entity.workout_option;
import com.example.productivity_application.db.entity.workout_option_log;
import com.example.productivity_application.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Locale;

public class OptionDetailActivity extends AppCompatActivity {

    public static final String EXTRA_OPTION_ID = "extra_option_id";

    private ActivityOptionDetailBinding binding;
    private MainViewModel viewModel;
    private int optionId;
    private workout_option currentOption;
    private workout_option_log latestLog;
    private SetsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOptionDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        optionId = getIntent().getIntExtra(EXTRA_OPTION_ID, -1);
        if (optionId == -1) {
            finish();
            return;
        }

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        
        setupRecyclerView();
        observeData();
        setupFooter();
    }

    private void setupRecyclerView() {
        adapter = new SetsAdapter();
        binding.rvSets.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSets.setAdapter(adapter);
    }

    private void observeData() {
        viewModel.getOptionsWithExercise(optionId).observe(this, data -> {
            if (data != null) {
                currentOption = data.option;
                binding.optionTitle.setText(currentOption.name);
                if (getSupportActionBar() != null) getSupportActionBar().setTitle(currentOption.name);
                
                if (currentOption.imageUrl != null && !currentOption.imageUrl.isEmpty()) {
                    try {
                        Uri uri = Uri.parse(currentOption.imageUrl);
                        binding.optionImage.setImageURI(uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        viewModel.getOptionsWithLogs(optionId).observe(this, data -> {
            if (data != null && data.logs != null && !data.logs.isEmpty()) {
                latestLog = data.logs.get(data.logs.size() - 1);
                adapter.setLogData(latestLog);
                
                binding.cbMarkDone.setChecked(latestLog.completed);
            }
        });
    }

    private void setupFooter() {
        binding.cbMarkDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (latestLog != null) {
                latestLog.completed = isChecked;
                viewModel.updateOptionLog(latestLog);
            }
        });

        binding.btnSaveFinish.setOnClickListener(v -> {
            if (latestLog != null) {
                viewModel.updateOptionLog(latestLog);
                Toast.makeText(this, "Workout saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    class SetsAdapter extends RecyclerView.Adapter<SetsAdapter.SetViewHolder> {
        private workout_option_log log;

        void setLogData(workout_option_log log) {
            this.log = log;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public SetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemSetTodoBinding b = ItemSetTodoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new SetViewHolder(b);
        }

        @Override
        public void onBindViewHolder(@NonNull SetViewHolder holder, int position) {
            if (log != null && log.sets != null) {
                boolean isDone = log.sets.get(position);
                holder.bind(position + 1, log.reps, log.weight, isDone, position < log.sets.size() - 1);
            }
        }

        @Override
        public int getItemCount() {
            return (log != null && log.sets != null) ? log.sets.size() : 0;
        }

        class SetViewHolder extends RecyclerView.ViewHolder {
            ItemSetTodoBinding b;
            CountDownTimer restTimer;

            SetViewHolder(ItemSetTodoBinding b) {
                super(b.getRoot());
                this.b = b;
            }

            void bind(int setNum, int reps, int weight, boolean isDone, boolean showTimer) {
                b.tvSetNumber.setText("Set " + setNum);
                b.tvSetDetails.setText(reps + " reps | " + weight + " kg");
                
                b.cbSetDone.setOnCheckedChangeListener(null);
                b.cbSetDone.setChecked(isDone);
                b.cbSetDone.setOnCheckedChangeListener((btn, checked) -> {
                    log.sets.set(getAdapterPosition(), checked);
                    
                    // Automatically mark exercise as done if all sets are checked
                    boolean allDone = true;
                    for (Boolean s : log.sets) if (!s) { allDone = false; break; }
                    log.completed = allDone;

                    viewModel.updateOptionLog(log);

                    if (checked && showTimer) {
                        b.timerContainer.setVisibility(View.VISIBLE);
                    } else {
                        b.timerContainer.setVisibility(View.GONE);
                        if (restTimer != null) restTimer.cancel();
                    }
                });

                b.timerContainer.setOnClickListener(v -> startTimer());
            }

            private void startTimer() {
                if (restTimer != null) restTimer.cancel();
                
                restTimer = new CountDownTimer(120000, 1000) {
                    public void onTick(long ms) {
                        long sec = (ms / 1000) % 60;
                        long min = (ms / (1000 * 60)) % 60;
                        b.tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", min, sec));
                    }
                    public void onFinish() {
                        b.tvTimer.setText("DONE");
                        Toast.makeText(itemView.getContext(), "Rest Over!", Toast.LENGTH_SHORT).show();
                    }
                }.start();
            }
        }
    }
}

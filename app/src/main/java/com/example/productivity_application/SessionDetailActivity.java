package com.example.productivity_application;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.productivity_application.adapter.SessionDetailAdapter;
import com.example.productivity_application.databinding.ActivitySessionDetailBinding;
import com.example.productivity_application.db.entity.workout_category;
import com.example.productivity_application.db.entity.workout_exercise;
import com.example.productivity_application.db.entity.workout_option_log;
import com.example.productivity_application.db.entity.workout_session;
import com.example.productivity_application.db.relation.OptionsWithLogs;
import com.example.productivity_application.viewmodel.MainViewModel;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SessionDetailActivity extends AppCompatActivity implements SessionDetailAdapter.OnAddOptionClickListener {

    public static final String EXTRA_EXERCISE_ID = "extra_exercise_id";

    private ActivitySessionDetailBinding binding;
    private MainViewModel viewModel;
    private SessionDetailAdapter adapter;

    private List<workout_exercise> exercises;
    private workout_session currentSession;
    private List<workout_category> categories;
    private List<OptionsWithLogs> optionsWithLogs;

    private int sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySessionDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionId = getIntent().getIntExtra(MainActivity.EXTRA_SESSION_ID, -1);
        if (sessionId == -1) {
            finish();
            return;
        }

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Loading...");
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupRecyclerView();
        observeData();
        setupFab();
    }

    private void setupRecyclerView() {
        adapter = new SessionDetailAdapter(this);
        binding.rvSessionDetail.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSessionDetail.setAdapter(adapter);
    }

    private void observeData() {
        viewModel.getSessionWithLogs(sessionId).observe(this, data -> {
            if (data != null) {
                this.currentSession = data.session;
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(currentSession.name);
                }
                updateAdapter();
            }
        });

        viewModel.allExercises.observe(this, data -> {
            this.exercises = data;
            updateAdapter();
        });

        viewModel.allCategories.observe(this, data -> {
            this.categories = data;
            updateAdapter();
        });

        viewModel.optionsWithLogs.observe(this, data -> {
            this.optionsWithLogs = data;
            updateAdapter();
        });
    }

    private void updateAdapter() {
        if (exercises != null && currentSession != null && categories != null && optionsWithLogs != null) {
            List<OptionsWithLogs> sessionOptions = optionsWithLogs.stream()
                    .filter(o -> o.option.session_id == sessionId)
                    .collect(Collectors.toList());
            
            // Filter exercises to only those that have options in this session
            Set<Integer> exerciseIdsInSession = sessionOptions.stream()
                    .map(o -> o.option.exercise_id)
                    .collect(Collectors.toSet());

            List<workout_exercise> filteredExercises = exercises.stream()
                    .filter(e -> exerciseIdsInSession.contains(e.exercise_id))
                    .collect(Collectors.toList());
            
            adapter.setData(filteredExercises, currentSession, categories, sessionOptions);
        }
    }

    private void setupFab() {
        binding.fabAddExercise.setOnClickListener(v -> {
            // TODO: This should ideally open a picker for existing exercises OR allow creating a new one
            Intent intent = new Intent(this, AddExerciseActivity.class);
            intent.putExtra(MainActivity.EXTRA_SESSION_ID, sessionId);
            startActivity(intent);
        });
    }

    @Override
    public void onAddOptionClick(workout_exercise exercise) {
        Intent intent = new Intent(this, AddOptionActivity.class);
        intent.putExtra(MainActivity.EXTRA_SESSION_ID, sessionId);
        intent.putExtra(EXTRA_EXERCISE_ID, exercise.exercise_id);
        startActivity(intent);
    }

    @Override
    public void onOptionDoneChanged(workout_option_log log, boolean isDone) {
        log.completed = isDone;
        viewModel.updateOptionLog(log);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}

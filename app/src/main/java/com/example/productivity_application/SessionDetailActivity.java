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
import com.example.productivity_application.db.entity.workout_session;
import com.example.productivity_application.db.relation.OptionsWithLogs;
import com.example.productivity_application.viewmodel.MainViewModel;

import java.util.List;
import java.util.stream.Collectors;

public class SessionDetailActivity extends AppCompatActivity {

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
        adapter = new SessionDetailAdapter();
        binding.rvSessionDetail.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSessionDetail.setAdapter(adapter);
    }

    private void observeData() {
        // Observe current session to set the Title
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
            // Filter options to only those belonging to this session
            List<OptionsWithLogs> sessionOptions = optionsWithLogs.stream()
                    .filter(o -> o.option.session_id == sessionId)
                    .collect(Collectors.toList());
            
            adapter.setData(exercises, currentSession, categories, sessionOptions);
        }
    }

    private void setupFab() {
        binding.fabAddExercise.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddExerciseActivity.class);
            intent.putExtra(MainActivity.EXTRA_SESSION_ID, sessionId);
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

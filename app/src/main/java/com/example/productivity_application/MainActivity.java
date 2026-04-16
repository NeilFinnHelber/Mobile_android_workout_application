package com.example.productivity_application;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.productivity_application.adapter.TaskAdapter;
import com.example.productivity_application.databinding.ActivityMainBinding;
import com.example.productivity_application.db.entity.workout_session;
import com.example.productivity_application.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    public static final String EXTRA_SESSION_ID = "extra_session_id";

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private TaskAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Set layout manager (fixed: RecyclerView needs a LayoutManager)
        binding.recylerSessions.setLayoutManager(new LinearLayoutManager(this));
        
        // Initialize adapter
        adapter = new TaskAdapter(this);
        binding.recylerSessions.setAdapter(adapter);

        // Observe data
        viewModel.allSessionsWithLogs.observe(this, sessions -> {
            adapter.submitList(sessions);
        });

        // FAB to add session
        binding.fabAddSession.setOnClickListener(v -> {
            startActivity(new Intent(this, AddSessionActivity.class));
        });
    }

    @Override
    public void onTaskClick(workout_session item) {
        Intent intent = new Intent(this, SessionDetailActivity.class);
        intent.putExtra(EXTRA_SESSION_ID, item.session_id);
        startActivity(intent);
    }

    @Override
    public void onTaskChecked(workout_session item, boolean isChecked) {
        // Handle checkbox (e.g., update log status)
    }
}

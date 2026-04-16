package com.example.productivity_application;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.productivity_application.databinding.ActivityAddSessionBindingBinding;
import com.example.productivity_application.db.entity.workout_session;
import com.example.productivity_application.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddSessionActivity extends AppCompatActivity {

    private ActivityAddSessionBindingBinding binding;
    private MainViewModel viewModel;

    private List<Integer> selectedTagIds = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddSessionBindingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("New Session");
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupSaveButton();
    }

    private void setupSaveButton() {
        binding.saveSessionBtn.setOnClickListener(v -> saveSession());
    }

    private void saveSession() {
        if (binding.sessionTitle.getText() == null) return;
        
        String title = binding.sessionTitle.getText().toString().trim();

        if (title.isEmpty()) {
            binding.sessionTitleBox.setError("Title is required");
            return;
        }
        binding.sessionTitleBox.setError(null);

        // Create new session
        workout_session session = new workout_session();
        session.name = title;
        session.is_active = true;
        
        // Use default routine ID 1 (created in seedCallback)
        session.routine_id = 1; 

        viewModel.insertSession(session);
        
        Toast.makeText(this, "Session saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}

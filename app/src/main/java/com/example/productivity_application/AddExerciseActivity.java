package com.example.productivity_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.productivity_application.adapter.ExercisePickerAdapter;
import com.example.productivity_application.databinding.ActivityAddExerciseBindingBinding;
import com.example.productivity_application.db.AppDatabase;
import com.example.productivity_application.db.entity.workout_category;
import com.example.productivity_application.db.entity.workout_exercise;
import com.example.productivity_application.viewmodel.MainViewModel;

public class AddExerciseActivity extends AppCompatActivity {

    private ActivityAddExerciseBindingBinding binding;
    private MainViewModel viewModel;
    private int sessionId;
    private ExercisePickerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddExerciseBindingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionId = getIntent().getIntExtra(MainActivity.EXTRA_SESSION_ID, -1);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Exercise to Session");
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupRecyclerView();
        setupButtons();
    }

    private void setupRecyclerView() {
        adapter = new ExercisePickerAdapter(exercise -> {
            // Pick existing exercise and go to Add Option
            goToAddOption(exercise.exercise_id);
        });
        binding.rvExistingExercises.setLayoutManager(new LinearLayoutManager(this));
        binding.rvExistingExercises.setAdapter(adapter);

        viewModel.allExercises.observe(this, exercises -> {
            adapter.setExercises(exercises);
        });
    }

    private void setupButtons() {
        binding.saveExerciseBtn.setOnClickListener(v -> saveExercise());
        binding.saveCategoryBtn.setOnClickListener(v -> saveCategory());
    }

    private void saveExercise() {
        if (binding.exerciseTitle.getText() == null) return;

        String title = binding.exerciseTitle.getText().toString().trim();

        if (title.isEmpty()) {
            binding.exerciseTitleBox.setError("Title is required");
            return;
        }

        workout_exercise exercise = new workout_exercise();
        exercise.description = title;
        exercise.muscle_group = ""; 
        exercise.completed = false;

        AppDatabase.dbWriteExecutor.execute(() -> {
            long exerciseId = AppDatabase.getInstance(this).workoutExerciseDao().insert(exercise);
            runOnUiThread(() -> {
                Toast.makeText(this, "Exercise Created", Toast.LENGTH_SHORT).show();
                goToAddOption((int) exerciseId);
            });
        });
    }

    private void goToAddOption(int exerciseId) {
        Intent intent = new Intent(this, AddOptionActivity.class);
        intent.putExtra(MainActivity.EXTRA_SESSION_ID, sessionId);
        intent.putExtra(SessionDetailActivity.EXTRA_EXERCISE_ID, exerciseId);
        startActivity(intent);
        finish();
    }
    
    private void saveCategory() {
        if (binding.categoryTitle.getText() == null) return;
        String title = binding.categoryTitle.getText().toString().trim();

        if (title.isEmpty()) {
            binding.categoryTitleBox.setError("Category name is required");
            return;
        }

        workout_category category = new workout_category();
        category.name = title;

        viewModel.insertCategory(category);
        
        Toast.makeText(this, "Category Created", Toast.LENGTH_SHORT).show();
        binding.categoryTitle.setText("");
        binding.categoryTitleBox.setError(null);
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

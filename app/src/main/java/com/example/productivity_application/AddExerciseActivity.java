package com.example.productivity_application;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.productivity_application.databinding.ActivityAddExerciseBindingBinding;
import com.example.productivity_application.db.entity.workout_category;
import com.example.productivity_application.db.entity.workout_exercise;
import com.example.productivity_application.viewmodel.MainViewModel;

public class AddExerciseActivity extends AppCompatActivity {

    private ActivityAddExerciseBindingBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddExerciseBindingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Create New Exercise");
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupButtons();
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

        viewModel.insertExercise(exercise);

        Toast.makeText(this, "Exercise Created", Toast.LENGTH_SHORT).show();
        finish(); // Close activity after saving
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
        finish(); // Close activity after saving
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

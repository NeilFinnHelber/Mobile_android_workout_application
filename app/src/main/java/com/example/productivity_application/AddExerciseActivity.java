package com.example.productivity_application;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.productivity_application.databinding.ActivityAddExerciseBindingBinding;
import com.example.productivity_application.db.AppDatabase;
import com.example.productivity_application.db.entity.workout_category;
import com.example.productivity_application.db.entity.workout_exercise;
import com.example.productivity_application.db.entity.workout_option;
import com.example.productivity_application.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddExerciseActivity extends AppCompatActivity {

    private ActivityAddExerciseBindingBinding binding;
    private MainViewModel viewModel;
    private int sessionId;
    private List<workout_category> categoriesList = new ArrayList<>();


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

        setupCategorySpinner();
        setupSaveButton();
    }

    private void setupCategorySpinner() {
        viewModel.allCategories.observe(this, categories -> {
            if (categories != null) {
                this.categoriesList = categories;
                List<String> names = new ArrayList<>();
                for (workout_category c : categories) names.add(c.name);
                
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                        android.R.layout.simple_spinner_item, names);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.categorySpinner.setAdapter(adapter);
            }
        });
    }

    private void setupSaveButton() {
        binding.saveExerciseBtn.setOnClickListener(v -> saveExercise());
    }

    private void saveExercise() {
        if (binding.exerciseTitle.getText() == null) return;

        String title = binding.exerciseTitle.getText().toString().trim();

        if (title.isEmpty()) {
            binding.exerciseTitleBox.setError("Title is required");
            return;
        }
        
        if (categoriesList.isEmpty()) {
            Toast.makeText(this, "Please wait for categories to load", Toast.LENGTH_SHORT).show();
            return;
        }

        int categoryIndex = binding.categorySpinner.getSelectedItemPosition();
        int categoryId = categoriesList.get(categoryIndex).category_id;

        AppDatabase.dbWriteExecutor.execute(() -> {
            // 1. Create Exercise
            workout_exercise exercise = new workout_exercise();
            exercise.description = title;
            exercise.muscle_group = categoriesList.get(categoryIndex).name;
            
            // Note: Since insert returns long (the new row ID), we use it for the option
            long exerciseId = AppDatabase.getInstance(this).workoutExerciseDao().insert(exercise);

            // 2. Create Option (The Link)
            workout_option option = new workout_option();
            option.name = title;
            option.exercise_id = (int) exerciseId;
            option.session_id = sessionId;
            option.category_id = categoryId;
            option.is_active = true;

            viewModel.insertOption(option);

            runOnUiThread(() -> {
                Toast.makeText(this, "Exercise added to session", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
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

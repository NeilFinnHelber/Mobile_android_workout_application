package com.example.productivity_application;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.productivity_application.databinding.ActivityAddOptionOfExerciseBindingBinding;
import com.example.productivity_application.db.AppDatabase;
import com.example.productivity_application.db.entity.workout_category;
import com.example.productivity_application.db.entity.workout_option;
import com.example.productivity_application.db.entity.workout_option_log;
import com.example.productivity_application.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddOptionActivity extends AppCompatActivity {

    private ActivityAddOptionOfExerciseBindingBinding binding;
    private MainViewModel viewModel;
    private int sessionId;
    private int exerciseId;
    private List<workout_category> categoriesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddOptionOfExerciseBindingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionId = getIntent().getIntExtra(MainActivity.EXTRA_SESSION_ID, -1);
        exerciseId = getIntent().getIntExtra(SessionDetailActivity.EXTRA_EXERCISE_ID, -1);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Option to Exercise");
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupCategorySpinner();
        setupButtons();
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

    private void setupButtons() {
        binding.saveOptionBtn.setOnClickListener(v -> saveOption());
        binding.cancelOptionButton.setOnClickListener(v -> finish());
    }

    private void saveOption() {
        if (binding.optionTitle.getText() == null) return;

        String title = binding.optionTitle.getText().toString().trim();
        String setsStr = binding.setAmount.getText().toString().trim();
        String repsStr = binding.repsPerSet.getText().toString().trim();
        String kgStr = binding.kgPerRep.getText().toString().trim();

        if (title.isEmpty()) {
            binding.optionTitleBox.setError("Title is required");
            return;
        }

        if (categoriesList.isEmpty()) {
            Toast.makeText(this, "Categories loading...", Toast.LENGTH_SHORT).show();
            return;
        }

        int categoryIndex = binding.categorySpinner.getSelectedItemPosition();
        int categoryId = categoriesList.get(categoryIndex).category_id;

        AppDatabase.dbWriteExecutor.execute(() -> {
            // 1. Create the Option
            workout_option option = new workout_option();
            option.name = title;
            option.exercise_id = exerciseId;
            option.session_id = sessionId;
            option.category_id = categoryId;
            option.is_active = true;

            long newOptionId = AppDatabase.getInstance(this).workoutOptionDao().insert(option);

            // 2. Create the initial Log (Sets/Reps/Weight)
            workout_option_log log = new workout_option_log();
            log.option_id = (int) newOptionId;
            log.reps = repsStr.isEmpty() ? 0 : Integer.parseInt(repsStr);
            log.weight = kgStr.isEmpty() ? 0 : Integer.parseInt(kgStr);
            
            int setsCount = setsStr.isEmpty() ? 0 : Integer.parseInt(setsStr);
            ArrayList<Boolean> setsList = new ArrayList<>();
            for(int i=0; i<setsCount; i++) setsList.add(false);
            log.sets = setsList;

            AppDatabase.getInstance(this).workoutOptionLogDao().insert(log);

            runOnUiThread(() -> {
                Toast.makeText(this, "Option saved", Toast.LENGTH_SHORT).show();
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

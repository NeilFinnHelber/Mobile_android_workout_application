package com.example.productivity_application;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.productivity_application.adapter.RoutineAdapter;
import com.example.productivity_application.adapter.SessionAdapter;
import com.example.productivity_application.databinding.ActivityMainBinding;
import com.example.productivity_application.databinding.ActivityRoutineBottomsheetDetailBinding;
import com.example.productivity_application.db.entity.workout_session;
import com.example.productivity_application.db.entity.workout_sports_routine;
import com.example.productivity_application.db.relation.SessionWithLogs;
import com.example.productivity_application.viewmodel.MainViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements SessionAdapter.OnTaskClickListener, RoutineAdapter.OnRoutineClickListener {

    public static final String EXTRA_SESSION_ID = "extra_session_id";
    public static final String EXTRA_ROUTINE_ID = "extra_routine_id";

    private ActivityMainBinding binding;
    private ActivityRoutineBottomsheetDetailBinding routineBinding;
    private MainViewModel viewModel;
    private SessionAdapter adapter;
    private RoutineAdapter routineAdapter;
    private Time selectedTime;

    private int selectedRoutineId = -1;
    private List<SessionWithLogs> allSessions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Set layout manager for Sessions (Vertical)
        binding.recylerSessions.setLayoutManager(new LinearLayoutManager(this));
        
        // Initialize adapter for Sessions
        adapter = new SessionAdapter(this);
        binding.recylerSessions.setAdapter(adapter);

        // Observe Sessions data
        viewModel.allSessionsWithLogs.observe(this, sessions -> {
            this.allSessions = sessions;
            filterAndDisplaySessions();
        });

        // Set layout manager for Routines (Horizontal)
        binding.recyclerRoutines.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Initialize adapter for Routines
        routineAdapter = new RoutineAdapter(this);
        binding.recyclerRoutines.setAdapter(routineAdapter);

        // Observe Routines data
        viewModel.allRoutines.observe(this, routines -> {
            routineAdapter.submitList(routines);
        });

        // FAB to add session
        binding.fabAddSession.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddSessionActivity.class);
            // Pass the currently selected routine ID so the new session is linked to it
            intent.putExtra(EXTRA_ROUTINE_ID, selectedRoutineId);
            startActivity(intent);
        });

        // Button to open Bottom Sheet for Routine creation
        binding.addRoutineBtn.setOnClickListener(v -> showAddRoutineBottomSheet());
    }

    private void filterAndDisplaySessions() {
        if (allSessions == null) return;
        if (selectedRoutineId == -1) {
            adapter.submitList(allSessions);
        } else {
            List<SessionWithLogs> filtered = allSessions.stream()
                    .filter(s -> s.session.routine_id == selectedRoutineId)
                    .collect(Collectors.toList());
            adapter.submitList(filtered);
        }
    }

    private void showAddRoutineBottomSheet() {
        // Initialize the binding for the bottom sheet
        routineBinding = ActivityRoutineBottomsheetDetailBinding.inflate(getLayoutInflater());
        
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(routineBinding.getRoot());

        // Setup Day Spinner
        ArrayAdapter<DayOfWeek> dayAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, DayOfWeek.values());
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routineBinding.spinnerDay.setAdapter(dayAdapter);

        // Setup Time Picker inside the Bottom Sheet
        routineBinding.btnTime.setOnClickListener(v -> {
            TimePickerDialog timePicker = new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> {
                        String timeStr = String.format(Locale.getDefault(), "%02d:%02d:00", hourOfDay, minute);
                        selectedTime = Time.valueOf(timeStr);
                        routineBinding.btnTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                    },
                    12, 0, true
            );
            timePicker.show();
        });

        // Setup Save Button inside the Bottom Sheet
        routineBinding.saveRoutineBtn.setOnClickListener(v -> {
            if (saveRoutineFromBottomSheet()) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private boolean saveRoutineFromBottomSheet() {
        // Validation check for routineBinding (it should be initialized by now)
        if (routineBinding == null || routineBinding.routineTitle.getText() == null) return false;

        String title = routineBinding.routineTitle.getText().toString().trim();
        if (title.isEmpty()) {
            routineBinding.routineTitleBox.setError("Title is required");
            return false;
        }

        if (selectedTime == null) {
            Toast.makeText(this, "Please select a reset time", Toast.LENGTH_SHORT).show();
            return false;
        }

        DayOfWeek day = (DayOfWeek) routineBinding.spinnerDay.getSelectedItem();

        workout_sports_routine routine = new workout_sports_routine();
        routine.name = title;
        routine.day_of_reset = day;
        routine.time_to_reset = selectedTime;
        routine.is_active = true;

        viewModel.insertRoutine(routine);

        Toast.makeText(this, "Routine Created Successfully", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onTaskChecked(workout_session item, boolean isChecked) {
        // Logic for checking tasks
    }

    @Override
    public void onRoutineClicked(workout_sports_routine item) {
        if (selectedRoutineId == item.routine_id) {
            selectedRoutineId = -1; // Deselect
        } else {
            selectedRoutineId = item.routine_id;
        }
        
        // Update visual state in the adapter
        routineAdapter.setSelectedRoutineId(selectedRoutineId);

        filterAndDisplaySessions();
    }

    @Override
    public void onTaskClick(workout_session item) {
        Intent intent = new Intent(this, SessionDetailActivity.class);
        intent.putExtra(EXTRA_SESSION_ID, item.session_id);
        startActivity(intent);
    }


//first to do: you need to check the local date and time and once it is the same as the one saved in the routine, all its sessions become unchecked
    //sessions need to be saved in routine

    /// todo: click on routine like session
    /// todo: routine reset logic
    /// todo: edit and delete for
    /// routine
    /// session
    /// option

    /// todo: for version 2.0.0 try using an existing free api for option data to allow the user to choose workouts instead of creating them themselves
}

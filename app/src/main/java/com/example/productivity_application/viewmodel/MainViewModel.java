package com.example.productivity_application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.productivity_application.db.entity.workout_category;
import com.example.productivity_application.db.entity.workout_exercise;
import com.example.productivity_application.db.entity.workout_option;
import com.example.productivity_application.db.entity.workout_session;
import com.example.productivity_application.db.entity.workout_sports_routine;
import com.example.productivity_application.db.relation.OptionsWithExercise;
import com.example.productivity_application.db.relation.OptionsWithLogs;
import com.example.productivity_application.db.relation.RoutineWithSessions;
import com.example.productivity_application.db.relation.SessionWithOptions;
import com.example.productivity_application.repository.WorkoutRepository;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final WorkoutRepository repository;

    public final LiveData<List<RoutineWithSessions>> allRoutines;
    public final LiveData<List<OptionsWithExercise>> allOptionsWithExercise;
    public final LiveData<List<OptionsWithLogs>> optionsWithLogs;
    public final LiveData<List<SessionWithOptions>> allSessionsWithOptions;
    public final LiveData<List<workout_exercise>> allExercises;
    public final LiveData<List<workout_category>> allCategories;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new WorkoutRepository(application);

        allRoutines = repository.getAllRoutines();
        allOptionsWithExercise = repository.getAllOptionsWithExercise();
        optionsWithLogs = repository.getAllOptionsWithLogs();
        allSessionsWithOptions = repository.getAllSessionsWithOptions();
        allExercises = repository.getAllExercises();
        allCategories = repository.getAllCategories();
    }


    // insert
    public void insertRoutine(workout_sports_routine routine) {
        repository.insertRoutine(routine);
    }

    public void insertSession(workout_session session) {
        repository.insertSession(session);
    }

    public void insertOption(workout_option option) {
        repository.insertOption(option);
    }

    public void insertExercise(workout_exercise exercise) {
        repository.insertExercise(exercise);
    }



    // gets
    public LiveData<OptionsWithExercise> getOptionsWithExercise(int optionId) {
        return repository.getOptionsWithExercise(optionId);
    }

    public LiveData<OptionsWithLogs> getOptionsWithLogs(int optionId) {
        return repository.getOptionsWithLogs(optionId);
    }

    public LiveData<SessionWithOptions> getSessionWithOptions(int sessionId) {
        return repository.getSessionWithOptions(sessionId);
    }

    public LiveData<RoutineWithSessions> getRoutineWithSessions(int routineId) {
        return repository.getRoutineWithSessions(routineId);
    }

}

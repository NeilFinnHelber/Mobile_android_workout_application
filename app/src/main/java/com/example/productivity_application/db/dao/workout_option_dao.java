package com.example.productivity_application.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;
import com.example.productivity_application.db.entity.workout_option;
import com.example.productivity_application.db.relation.OptionsWithExercise;
import com.example.productivity_application.db.relation.OptionsWithLogs;

@Dao
public interface workout_option_dao {

    @Insert
    long insert(workout_option option);

    @Update
    void update(workout_option option);

    @Delete
    void delete(workout_option option);

    @Query("SELECT * FROM workout_options")
    List<workout_option> getAll();

    @Query("SELECT * FROM workout_options WHERE option_id = :id LIMIT 1")
    workout_option getById(int id);

    @Query("SELECT * FROM workout_options WHERE is_active = 1")
    List<workout_option> getActiveOptions();

    @Transaction
    @Query("SELECT * FROM workout_options")
    LiveData<List<OptionsWithExercise>> getAllOptionsWithExercise();

    @Transaction
    @Query("SELECT * FROM workout_options")
    LiveData<List<OptionsWithLogs>> getOptionsWithLogs();

    @Transaction
    @Query("SELECT * FROM workout_options WHERE session_id = :sessionId")
    LiveData<List<OptionsWithExercise>> getOptionsWithExercise(int sessionId);

    @Query("UPDATE workout_options SET is_active = :active WHERE option_id = :optionId")
    void setActive(int optionId, boolean active);


    @Transaction
    @Query("SELECT * FROM workout_options WHERE option_id = :optionId")
    LiveData<OptionsWithExercise> getOptionsWithExerciseById(int optionId);

    @Transaction
    @Query("SELECT * FROM workout_options WHERE option_id = :optionId")
    LiveData<OptionsWithLogs> getOptionsWithLogsById(int optionId);
}

package com.example.productivity_application.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;
import com.example.productivity_application.db.entity.workout_exercise;

@Dao
public interface workout_exercise_dao {

    @Insert
    long insert(workout_exercise exercise);

    @Update
    void update(workout_exercise exercise);

    @Delete
    void delete(workout_exercise exercise);

    @Query("SELECT * FROM workout_exercises")
    LiveData<List<workout_exercise>> getAll();

    @Query("SELECT * FROM workout_exercises WHERE exercise_id = :id LIMIT 1")
    workout_exercise getById(int id);
}
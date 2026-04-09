package com.example.productivity_application.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;
import com.example.productivity_application.db.entity.workout_sports_routine;
import com.example.productivity_application.db.relation.RoutineWithSessions;

@Dao
public interface workout_sports_routine_dao {

    @Insert
    long insert(workout_sports_routine routine);

    @Update
    void update(workout_sports_routine routine);

    @Delete
    void delete(workout_sports_routine routine);

    @Query("SELECT * FROM workout_sports_routines")
    List<workout_sports_routine> getAll();

    @Query("SELECT * FROM workout_sports_routines WHERE routine_id = :id LIMIT 1")
    workout_sports_routine getById(int id);

    @Query("SELECT * FROM workout_sports_routines WHERE is_active = 1")
    List<workout_sports_routine> getActiveRoutines();

    @Transaction
    @Query("SELECT * FROM workout_sports_routines")
    LiveData<List<RoutineWithSessions>> getRoutinesWithSessions();


    @Transaction
    @Query("SELECT * FROM workout_sports_routines WHERE routine_id = :routineId")
    LiveData<RoutineWithSessions> getRoutineWithSessionsById(int routineId);
}
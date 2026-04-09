package com.example.productivity_application.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;
import com.example.productivity_application.db.entity.workout_option_log;

@Dao
public interface workout_option_log_dao {

    @Insert
    long insert(workout_option_log log);

    @Update
    void update(workout_option_log log);

    @Delete
    void delete(workout_option_log log);

    @Query("SELECT * FROM workout_option_logs WHERE option_id = :optionId")
    LiveData<List<workout_option_log>> getLogsForOption(int optionId);
}
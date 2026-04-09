package com.example.productivity_application.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;
import com.example.productivity_application.db.entity.workout_session_log;

@Dao
public interface workout_session_log_dao {

    @Insert
    long insert(workout_session_log log);

    @Update
    void update(workout_session_log log);

    @Delete
    void delete(workout_session_log log);

    @Query("SELECT * FROM workout_session_logs WHERE session_id = :sessionId")
    LiveData<List<workout_session_log>> getLogsForSession(int sessionId);

    @Query("UPDATE workout_session_logs SET completed = :completed WHERE session_log_id = :logId")
    void setCompleted(int logId, boolean completed);
}
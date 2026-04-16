package com.example.productivity_application.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;
import com.example.productivity_application.db.entity.workout_session;
import com.example.productivity_application.db.relation.SessionWithLogs;
import com.example.productivity_application.db.relation.SessionWithOptions;

@Dao
public interface workout_session_dao {

    @Insert
    long insert(workout_session session);

    @Update
    void update(workout_session session);

    @Delete
    void delete(workout_session session);

    @Query("SELECT * FROM workout_sessions")
    List<workout_session> getAll();

    @Query("SELECT * FROM workout_sessions WHERE session_id = :id LIMIT 1")
    workout_session getById(int id);

    @Query("SELECT * FROM workout_sessions WHERE is_active = 1")
    List<workout_session> getActiveSessions();

    @Transaction
    @Query("SELECT * FROM workout_sessions")
    LiveData<List<SessionWithOptions>> getAllSessionsWithOptions();

    @Transaction
    @Query("SELECT * FROM workout_sessions")
    LiveData<List<SessionWithLogs>> getAllSessionsWithLogs();

    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE routine_id = :routineId")
    LiveData<List<SessionWithOptions>> getSessionsWithOptions(int routineId);

    @Query("UPDATE workout_sessions SET is_active = :active WHERE session_id = :sessionId")
    void setActive(int sessionId, boolean active);

    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE session_id = :sessionId")
    LiveData<SessionWithOptions> getSessionWithOptionsById(int sessionId);

    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE session_id = :sessionId")
    LiveData<SessionWithLogs> getSessionWithLogsById(int sessionId);
}

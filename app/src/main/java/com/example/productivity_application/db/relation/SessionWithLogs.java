package com.example.productivity_application.db.relation;

import androidx.room.Embedded;
import androidx.room.Relation;
import com.example.productivity_application.db.entity.workout_session;
import com.example.productivity_application.db.entity.workout_session_log;

public class SessionWithLogs {
    @Embedded
    public workout_session session;

    @Relation(
            parentColumn = "session_id",
            entityColumn = "session_id"
    )
    public workout_session_log log; // Use List<workout_session_log> if multiple logs exist per session
}
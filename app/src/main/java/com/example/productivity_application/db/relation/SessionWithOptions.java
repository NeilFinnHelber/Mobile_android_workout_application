package com.example.productivity_application.db.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.productivity_application.db.entity.workout_option;
import com.example.productivity_application.db.entity.workout_session;
import com.example.productivity_application.db.entity.workout_sports_routine;

import java.util.List;

public class SessionWithOptions {
    @Embedded
    public workout_session session;

    @Relation(
            parentColumn = "session_id",
            entityColumn = "session_id"
    )
    public List<workout_option> options;
}

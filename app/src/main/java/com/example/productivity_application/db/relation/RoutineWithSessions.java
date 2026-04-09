package com.example.productivity_application.db.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.productivity_application.db.entity.workout_session;
import com.example.productivity_application.db.entity.workout_sports_routine;

import java.util.List;

public class RoutineWithSessions {
    @Embedded
    public workout_sports_routine routine;

    @Relation(
            parentColumn = "routine_id",
            entityColumn = "routine_id"
    )
    public List<workout_session> sessions;
}

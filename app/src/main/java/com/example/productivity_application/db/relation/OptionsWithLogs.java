package com.example.productivity_application.db.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.productivity_application.db.entity.workout_option;
import com.example.productivity_application.db.entity.workout_option_log;

import java.util.List;

public class OptionsWithLogs {
    @Embedded
    public workout_option option;

    @Relation(
            parentColumn = "option_id",
            entityColumn = "option_id"
    )
    public List<workout_option_log> logs;
}

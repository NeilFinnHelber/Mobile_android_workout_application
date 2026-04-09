package com.example.productivity_application.db.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.productivity_application.db.entity.workout_option;
import com.example.productivity_application.db.entity.workout_exercise;

public class OptionsWithExercise {

    @Embedded
    public workout_option option;

    @Relation(
            parentColumn = "exercise_id",   // Column in workout_option
            entityColumn = "exercise_id"    // Column in workout_exercise
    )
    public workout_exercise exercise;
}

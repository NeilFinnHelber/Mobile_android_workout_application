package com.example.productivity_application.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "workout_exercises")
public class workout_exercise {

    @PrimaryKey(autoGenerate = true)
    public int exercise_id;

    public String muscle_group;

    public String description;

    @ColumnInfo(defaultValue = "1")
    public int workout_option_to_complete_amount;

    @ColumnInfo(defaultValue = "0")
    public boolean completed;
}

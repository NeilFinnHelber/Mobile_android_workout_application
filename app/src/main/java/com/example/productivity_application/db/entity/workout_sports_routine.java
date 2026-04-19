package com.example.productivity_application.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Time;
import java.time.DayOfWeek;

@Entity(tableName = "workout_sports_routines")
public class workout_sports_routine {

    @PrimaryKey(autoGenerate = true)
    public int routine_id;

    public String name;

    @ColumnInfo(defaultValue = "1")
    public boolean is_active;

    public Time time_to_reset;
    public DayOfWeek day_of_reset;
}

/// to do: add this, edit and delete other things and allow for reset
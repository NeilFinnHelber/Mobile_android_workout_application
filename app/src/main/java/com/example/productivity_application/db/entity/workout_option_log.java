package com.example.productivity_application.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity(tableName = "workout_option_logs")
public class workout_option_log {

    @PrimaryKey(autoGenerate = true)
    public int option_log_id;

    @ColumnInfo(name = "option_id")
    public int option_id;

    public int reps;
    public ArrayList<Boolean> sets;
    public int weight;

    @ColumnInfo(defaultValue = "0")
    public boolean completed;

}

package com.example.productivity_application.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Date;
import java.time.LocalDateTime;

@Entity(tableName = "workout_session_logs")
public class workout_session_log {

    @PrimaryKey(autoGenerate = true)
    public int session_log_id;

    public Date week_start;
    public LocalDateTime performed_at;

    @ColumnInfo(defaultValue = "0")
    public boolean completed;

    @ColumnInfo(name = "session_id")
    public int session_id;


}

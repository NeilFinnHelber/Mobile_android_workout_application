package com.example.productivity_application.db.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "workout_sessions",
        foreignKeys = @ForeignKey(
                entity = workout_sports_routine.class,
                parentColumns = "routine_id",
                childColumns = "routine_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("routine_id")}
)
public class workout_session {

    @PrimaryKey(autoGenerate = true)
    public int session_id;

    public int routine_id;

    public String name;

    public boolean is_active;


}

package com.example.productivity_application.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "workout_options",
        foreignKeys = {
                @ForeignKey(
                        entity = workout_session.class,
                        parentColumns = "session_id",
                        childColumns = "session_id",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = workout_exercise.class,
                        parentColumns = "exercise_id",
                        childColumns = "exercise_id"
                ),
                @ForeignKey(
                        entity = workout_category.class,
                        parentColumns = "category_id",
                        childColumns = "category_id"
                )
        },
        indices = {@Index("session_id"), @Index("exercise_id"), @Index("category_id")}
)
public class workout_option {

    @PrimaryKey(autoGenerate = true)
    public int option_id;

    public String name;

    public int session_id;
    public int exercise_id;
    public int category_id;

    @ColumnInfo(defaultValue = "1")
    public boolean is_active;
}

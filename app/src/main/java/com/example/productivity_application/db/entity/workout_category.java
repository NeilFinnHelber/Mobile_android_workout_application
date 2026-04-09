package com.example.productivity_application.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "workout_categories")
public class workout_category {

    @PrimaryKey(autoGenerate = true)
    public int category_id;

    public String name;
}

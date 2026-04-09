package com.example.productivity_application.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;
import com.example.productivity_application.db.entity.workout_category;

@Dao
public interface workout_category_dao {

    @Insert
    long insert(workout_category category);

    @Update
    void update(workout_category category);

    @Delete
    void delete(workout_category category);


    @Query("SELECT * FROM workout_categories WHERE category_id = :id LIMIT 1")
    workout_category getById(int id);

    @Query("SELECT * FROM workout_categories")
    LiveData<List<workout_category>> getAll();
}
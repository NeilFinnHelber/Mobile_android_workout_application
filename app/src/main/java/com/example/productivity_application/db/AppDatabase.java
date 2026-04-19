package com.example.productivity_application.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.productivity_application.db.dao.*;
import com.example.productivity_application.db.entity.*;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Room Database — single source of truth for workout app.
 *
 * Entities:
 *  • workout_sports_routine  – main routine config
 *  • workout_session         – sessions inside routine
 *  • workout_exercise        – exercise definitions
 *  • workout_category        – grouping (e.g. chest, legs)
 *  • workout_option          – selectable exercise option
 *  • workout_session_log     – session history
 *  • workout_option_log      – per-exercise logs
 */
@Database(
        entities = {
                workout_category.class,
                workout_exercise.class,
                workout_option.class,
                workout_option_log.class,
                workout_session.class,
                workout_session_log.class,
                workout_sports_routine.class
        },
        version = 1,
        exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    // ── DAOs ─────────────────────────────────────────────
    public abstract workout_category_dao workoutCategoryDao();
    public abstract workout_exercise_dao workoutExerciseDao();
    public abstract workout_option_dao workoutOptionDao();
    public abstract workout_option_log_dao workoutOptionLogDao();
    public abstract workout_session_dao workoutSessionDao();
    public abstract workout_session_log_dao workoutSessionLogDao();
    public abstract workout_sports_routine_dao workoutSportsRoutineDao();

    // ── Singleton ─────────────────────────────────────────
    private static volatile AppDatabase INSTANCE;

    /** Background executor for DB operations */
    public static final ExecutorService dbWriteExecutor =
            Executors.newFixedThreadPool(4);

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "workout_database"
                            )
                            .addCallback(seedCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // ── Seed Data ─────────────────────────────────────────
    private static final RoomDatabase.Callback seedCallback =
            new RoomDatabase.Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);

                    dbWriteExecutor.execute(() -> {
                        AppDatabase database = INSTANCE;

                        // ── Categories ─────────────────────
                        workout_category_dao categoryDao = database.workoutCategoryDao();
                        categoryDao.insert(new workout_category(){{
                            name = "Machine";
                        }});
                        categoryDao.insert(new workout_category(){{
                            name = "Calisthenics";
                        }});

                        // ── Exercises ──────────────────────
                        workout_exercise_dao exerciseDao = database.workoutExerciseDao();

                        exerciseDao.insert(new workout_exercise(){{
                            muscle_group = "chest";
                            description = "Chest";
                            workout_option_to_complete_amount = 3;
                        }});

                        exerciseDao.insert(new workout_exercise(){{
                            muscle_group = "back";
                            description = "Back";
                            workout_option_to_complete_amount = 3;
                        }});

                        // Options
                        /*
                        workout_option_dao optionDao = database.workoutOptionDao();

                        optionDao.insert(new workout_option() {{
                            name = "Bench Press";
                            exercise_id = 1;
                            category_id = 1;
                            is_active = true;
                        }});

                        workout_option_log_dao optionLogDao = database.workoutOptionLogDao();

                        optionLogDao.insert(new workout_option_log(){{
                            option_id = 1;
                            sets = new ArrayList<Boolean>(){{
                                add(true);
                                add(true);
                                add(true);
                            }};
                            reps = 12;
                            weight = 25;
                        }});
                        */


                        // ── Routine ────────────────────────
                        workout_sports_routine_dao routineDao = database.workoutSportsRoutineDao();

                        long routineId = routineDao.insert(new workout_sports_routine(){{
                            name = "Default Routine";
                            is_active = true;
                        }});

                        // ── Session ────────────────────────
                        workout_session_dao sessionDao = database.workoutSessionDao();

                        sessionDao.insert(new workout_session(){{
                            name = "Day 1";
                            is_active = true;
                        }});
                    });
                }
            };
}
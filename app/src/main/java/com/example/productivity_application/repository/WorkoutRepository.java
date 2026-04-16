package com.example.productivity_application.repository;


import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.productivity_application.db.AppDatabase;
import com.example.productivity_application.db.entity.workout_category;
import com.example.productivity_application.db.entity.workout_exercise;
import com.example.productivity_application.db.entity.workout_option;
import com.example.productivity_application.db.entity.workout_option_log;
import com.example.productivity_application.db.entity.workout_session;
import com.example.productivity_application.db.entity.workout_session_log;
import com.example.productivity_application.db.entity.workout_sports_routine;
import com.example.productivity_application.db.relation.OptionsWithExercise;
import com.example.productivity_application.db.relation.OptionsWithLogs;
import com.example.productivity_application.db.relation.RoutineWithSessions;
import com.example.productivity_application.db.dao.*;
import com.example.productivity_application.db.relation.SessionWithLogs;
import com.example.productivity_application.db.relation.SessionWithOptions;

import java.util.List;

/**
 * Central repository for workout feature
 */
public class WorkoutRepository {

    private final workout_sports_routine_dao routineDao;
    private final workout_session_dao sessionDao;
    private final workout_option_dao optionDao;
    private final workout_exercise_dao exerciseDao;
    private final workout_category_dao categoryDao;
    private final workout_option_log_dao optionLogDao;
    private final workout_session_log_dao sessionLogDao;

    public WorkoutRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);

        routineDao    = db.workoutSportsRoutineDao();
        sessionDao    = db.workoutSessionDao();
        optionDao     = db.workoutOptionDao();
        exerciseDao   = db.workoutExerciseDao();
        categoryDao   = db.workoutCategoryDao();
        optionLogDao  = db.workoutOptionLogDao();
        sessionLogDao = db.workoutSessionLogDao();
    }



    // ── RELATION QUERIES (DETAIL LOOKUPS) ───────────────────────

    public LiveData<OptionsWithExercise> getOptionsWithExercise(int optionId) {
        return optionDao.getOptionsWithExerciseById(optionId);
    }

    public LiveData<OptionsWithLogs> getOptionsWithLogs(int optionId) {
        return optionDao.getOptionsWithLogsById(optionId);
    }

    public LiveData<SessionWithOptions> getSessionWithOptions(int sessionId) {
        return sessionDao.getSessionWithOptionsById(sessionId);
    }

    public LiveData<RoutineWithSessions> getRoutineWithSessions(int routineId) {
        return routineDao.getRoutineWithSessionsById(routineId);
    }

    public LiveData<SessionWithLogs> getSessionWithLogs(int sessionId) {
        return sessionDao.getSessionWithLogsById(sessionId);
    }




    // ── ROUTINES ─────────────────────────────────────────────

    public LiveData<List<RoutineWithSessions>> getAllRoutines() {
        return routineDao.getRoutinesWithSessions();
    }

    public void insertRoutine(workout_sports_routine routine) {
        AppDatabase.dbWriteExecutor.execute(() -> routineDao.insert(routine));
    }

    // ── SESSIONS ─────────────────────────────────────────────

    public LiveData<List<SessionWithOptions>> getAllSessionsWithOptions() {
        return sessionDao.getAllSessionsWithOptions();
    }

    public LiveData<List<SessionWithLogs>> getAllSessionsWithLogs() {
        return sessionDao.getAllSessionsWithLogs();
    }

    public LiveData<List<SessionWithOptions>> getSessionsForRoutine(int routineId) {
        return sessionDao.getSessionsWithOptions(routineId);
    }

    public void insertSession(workout_session session) {
        AppDatabase.dbWriteExecutor.execute(() -> sessionDao.insert(session));
    }

    public void setSessionActive(int sessionId, boolean active) {
        AppDatabase.dbWriteExecutor.execute(() ->
                sessionDao.setActive(sessionId, active));
    }

    // ── OPTIONS (EXERCISES IN SESSION) ────────────────────────

    public LiveData<List<OptionsWithExercise>> getAllOptionsWithExercise() {
        return optionDao.getAllOptionsWithExercise();
    }

    public LiveData<List<OptionsWithLogs>> getAllOptionsWithLogs() {
        return optionDao.getOptionsWithLogs();
    }

    public LiveData<List<OptionsWithExercise>> getOptionsForSession(int sessionId) {
        return optionDao.getOptionsWithExercise(sessionId);
    }

    public void insertOption(workout_option option) {
        AppDatabase.dbWriteExecutor.execute(() -> optionDao.insert(option));
    }

    public void setOptionActive(int optionId, boolean active) {
        AppDatabase.dbWriteExecutor.execute(() ->
                optionDao.setActive(optionId, active));
    }

    // ── EXERCISES ─────────────────────────────────────────────

    public LiveData<List<workout_exercise>> getAllExercises() {
        return exerciseDao.getAll();
    }

    public void insertExercise(workout_exercise exercise) {
        AppDatabase.dbWriteExecutor.execute(() -> exerciseDao.insert(exercise));
    }

    // ── CATEGORIES ────────────────────────────────────────────

    public LiveData<List<workout_category>> getAllCategories() {
        return categoryDao.getAll();
    }

    // ── SESSION LOGS (WEEKLY TRACKING) ────────────────────────

    public LiveData<List<workout_session_log>> getLogsForSession(int sessionId) {
        return sessionLogDao.getLogsForSession(sessionId);
    }

    public void insertSessionLog(workout_session_log log) {
        AppDatabase.dbWriteExecutor.execute(() -> sessionLogDao.insert(log));
    }

    public void setSessionCompleted(int logId, boolean completed) {
        AppDatabase.dbWriteExecutor.execute(() ->
                sessionLogDao.setCompleted(logId, completed));
    }

    // ── OPTION LOGS (REPS / WEIGHT) ───────────────────────────

    public LiveData<List<workout_option_log>> getLogsForOption(int optionId) {
        return optionLogDao.getLogsForOption(optionId);
    }

    public void insertOptionLog(workout_option_log log) {
        AppDatabase.dbWriteExecutor.execute(() -> optionLogDao.insert(log));
    }

    public void updateOptionLog(workout_option_log log) {
        AppDatabase.dbWriteExecutor.execute(() -> optionLogDao.update(log));
    }

    // ── COMPLEX INSERT (SESSION WITH OPTIONS) ─────────────────

    public void insertSessionWithOptions(workout_session session, List<workout_option> options) {
        AppDatabase.dbWriteExecutor.execute(() -> {
            long sessionId = sessionDao.insert(session);

            if (options != null) {
                for (workout_option option : options) {
                    option.session_id = (int) sessionId;
                    optionDao.insert(option);
                }
            }
        });
    }
}

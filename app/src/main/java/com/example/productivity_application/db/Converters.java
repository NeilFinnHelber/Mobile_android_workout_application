package com.example.productivity_application.db;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Converters {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @TypeConverter
    public static ArrayList<Boolean> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Boolean>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Boolean> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static LocalDateTime toLocalDateTime(String dateString) {
        return dateString == null ? null : LocalDateTime.parse(dateString, formatter);
    }

    @TypeConverter
    public static String fromLocalDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(formatter);
    }

    @TypeConverter
    public static Time fromTimeTimestamp(Long value) {
        return value == null ? null : new Time(value);
    }

    @TypeConverter
    public static Long timeToTimestamp(Time time) {
        return time == null ? null : time.getTime();
    }

    @TypeConverter
    public static DayOfWeek toDayOfWeek(Integer value) {
        return value == null ? null : DayOfWeek.of(value);
    }

    @TypeConverter
    public static Integer fromDayOfWeek(DayOfWeek dayOfWeek) {
        return dayOfWeek == null ? null : dayOfWeek.getValue();
    }
}

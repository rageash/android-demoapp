package com.example.demoapp.database;

import android.content.Context;

import androidx.room.RoomDatabase;
import androidx.room.Database;
import androidx.room.Room;

import com.example.demoapp.user.model.User;
import com.example.demoapp.user.dao.UserDao;

/**
 * @Database annotation helps the room.databaseBuilder to build the database definition
 * where
 * entities - array of tables in the database
 * version - database version, which need to incremented on database or table changes
 */
@Database(entities = {User.class}, version = 1)
/*
 * Database instance should implement the RoomDatabase abstract class,
 * definition is created by the room database library itself via Room.databaseBuilder.
 */
public abstract class AppDatabase extends RoomDatabase {

    private final static String DATABASE_NAME = "app_database";

    private static AppDatabase instance;

    /**
     * Every table should be declared here for SQL operations
     * @return UserDao
     */
    public abstract UserDao userDao();

    /**
     * Returns AppDatabase instance
     *
     * @param context - recommended to pass Application context
     * @return AppDatabase instance
     */
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context,
                    AppDatabase.class, DATABASE_NAME).build();
        }
        return instance;
    }
}

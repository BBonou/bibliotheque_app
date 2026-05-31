package com.esp.bibliothequeapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// @Database tells Room which entities are part of the database.
// version = 1 indicates the first version of our schema.
@Database(entities = {Livre.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Single instance of the database.
    private static volatile AppDatabase INSTANCE;

    // An abstract method that provides access to the DAO.
    public abstract LivreDao livreDao();

    // Thread-safe singleton with double-checked locking.
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "bibliotheque_database")
                            // For an introductory lab exercise.
                            // In a production environment, migrations would be used.
                            .fallbackToDestructiveMigration(false)
                            .build();

                }
            }
        }
        return INSTANCE;
    }
}

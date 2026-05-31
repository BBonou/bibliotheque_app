package com.esp.bibliothequeapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

// @Database tells Room which entities are part of the database.
// version = 2 adds the anneePublication column.
@Database(entities = {Livre.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Single instance of the database.
    private static volatile AppDatabase INSTANCE;

    // An abstract method that provides access to the DAO.
    public abstract LivreDao livreDao();

    // Migration from version 1 to version 2:
    // Adds the anneePublication column with a default value of 0.
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE livres ADD COLUMN anneePublication INTEGER NOT NULL DEFAULT 0");
        }
    };

    // Thread-safe singleton with double-checked locking.
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "bibliotheque_database")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

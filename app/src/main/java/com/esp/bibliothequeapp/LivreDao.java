package com.esp.bibliothequeapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// @Dao tells Room that this interface contains the SQL queries.
@Dao
public interface LivreDao {

    // Inserts a new book into the database.
    @Insert
    void insert(Livre livre);

    // Modifies an existing book.
    @Update
    void update(Livre livre);

    // Deletes an existing book.
    @Delete
    void delete(Livre livre);

    // Retrieve all books, from the newest to the oldest.
    @Query("SELECT * FROM livres ORDER BY id DESC")
    List<Livre> getAllLivres();

    // Search books by title (case-insensitive LIKE).
    @Query("SELECT * FROM livres WHERE titre LIKE '%' || :query || '%' ORDER BY id DESC")
    List<Livre> searchByTitle(String query);

    // Delete all books.
    // Useful for resetting the database during testing.
    @Query("DELETE FROM livres")
    void deleteAll();
}

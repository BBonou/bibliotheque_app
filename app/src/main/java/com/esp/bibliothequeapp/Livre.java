package com.esp.bibliothequeapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

// @Entity tells Room that this class represents a SQLite table.
// tableName allows you to give the table a clear name.
@Entity(tableName = "livres")
public class Livre implements Serializable {
    // @PrimaryKey specifies the primary key of the table.
    // autoGenerate = true allows Room to automatically generate the ID.
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String titre;
    private String auteur;
    private String isbn;
    private boolean disponible;

    public Livre(int id, String titre, String auteur, String isbn, boolean disponible) {
        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.isbn = isbn;
        this.disponible = disponible;
    }

    public int getId() {return id;}
    public String getTitre() {return titre;}
    public String getAuteur() {return auteur;}
    public String getIsbn() {return isbn;}
    public boolean isDisponible() {return disponible;}

    public void setId(int id) {this.id = id;}
    public void setTitre(String titre) {this.titre = titre;}
    public void setAuteur(String auteur) {this.auteur = auteur;}
    public void setIsbn(String isbn) {this.isbn = isbn;}
    public void setDisponible(boolean disponible) {this.disponible = disponible;}
}

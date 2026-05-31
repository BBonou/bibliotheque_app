package com.esp.bibliothequeapp;

import java.io.Serializable;

public class Livre implements Serializable {
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

package com.esp.bibliothequeapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // List display component
    private RecyclerView recyclerViewLivres;

    // Custom fit
    private LivreAdapter livreAdapter;

    // List of books
    private ArrayList<Livre> listeLivres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Associates the XML layout to the activity
        setContentView(R.layout.activity_main);

        // Retrieving the RecyclerView in the layout
        recyclerViewLivres = findViewById(R.id.recylerViewLivres);

        // Initializing the list
        listeLivres = new ArrayList<>();

        // Addition of 8 fictional books
        listeLivres.add(new Livre(1, "Le Petit Prince", "Antoine de Saint Exupéry", "9780156013987", true));
        listeLivres.add(new Livre(2, "L'Étranger", "Albert Camus", "9782070360024", false));
        listeLivres.add(new Livre(3, "Les Misérables", "Victor Hugo", "9782253096344", true));
        listeLivres.add(new Livre(4, "Une si longue lettre", "Mariama Bâ", "9782841290529", true));
        listeLivres.add(new Livre(5, "Le Vieux Nègre et la Médaille", "Ferdinand Oyono", "9782264018304", false));
        listeLivres.add(new Livre(6, "Madame Bovary", "Gustave Flaubert", "9782070409228", true));
        listeLivres.add(new Livre(7, "La Peste", "Alert Camus", "9782070360420", false));
        listeLivres.add(new Livre(8, "Sous l'orage", "Seydou Badian", "9782708707691", true));

        // The RecyclerView will display the items vertically
        recyclerViewLivres.setLayoutManager(new LinearLayoutManager(this));

        // Creating the adapter with the list of books
        livreAdapter = new LivreAdapter(listeLivres);

        // Link between the RecyclerView and the adapter
        recyclerViewLivres.setAdapter(livreAdapter);
    }
}
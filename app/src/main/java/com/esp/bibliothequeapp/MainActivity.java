package com.esp.bibliothequeapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ADD_EDIT_LIVRE = 100;

    // List display component
    private RecyclerView recyclerViewLivres;

    // Custom fit
    private LivreAdapter livreAdapter;

    // List of books
    private ArrayList<Livre> listeLivres;

    private FloatingActionButton fabAjouterLivre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Associates the XML layout to the activity
        setContentView(R.layout.activity_main);

        // Retrieving the RecyclerView in the layout
        recyclerViewLivres = findViewById(R.id.recyclerViewLivres);

        fabAjouterLivre = findViewById(R.id.fabAjouterLivre);

        initialiserLivres();

        recyclerViewLivres.setLayoutManager(new LinearLayoutManager(this));

        livreAdapter = new LivreAdapter(listeLivres);
        recyclerViewLivres.setAdapter(livreAdapter);

        fabAjouterLivre.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            intent.putExtra(AddEditActivity.EXTRA_MODE, AddEditActivity.MODE_ADD);

            startActivityForResult(intent, REQUEST_ADD_EDIT_LIVRE);
        });

//        // The RecyclerView will display the items vertically
//        recyclerViewLivres.setLayoutManager(new LinearLayoutManager(this));
//
//        // Creating the adapter with the list of books
//        livreAdapter = new LivreAdapter(listeLivres);
//
//        // Link between the RecyclerView and the adapter
//        recyclerViewLivres.setAdapter(livreAdapter);
    }

    private void initialiserLivres() {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_EDIT_LIVRE && resultCode == RESULT_OK && data != null) {
            String mode = data.getStringExtra(AddEditActivity.EXTRA_MODE);
            Livre livre = (Livre) data.getSerializableExtra(AddEditActivity.EXTRA_LIVRE);
            int position = data.getIntExtra(AddEditActivity.EXTRA_POSITION, -1);

            if (livre == null) {
                return;
            }

            if (AddEditActivity.MODE_ADD.equals(mode)) {
                int nouvelId = listeLivres.size() + 1;
                livre.setId(nouvelId);

                listeLivres.add(livre);
            } else if (AddEditActivity.MODE_EDIT.equals(mode) && position >= 0) {
                listeLivres.set(position, livre);
            }
            livreAdapter.notifyDataSetChanged();
        }
    }
}
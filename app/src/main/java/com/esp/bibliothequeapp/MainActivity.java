package com.esp.bibliothequeapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ADD_EDIT_LIVRE = 100;

    // List display component
    private RecyclerView recyclerViewLivres;

    // Custom fit
    private LivreAdapter livreAdapter;

    // List of books
    private List<Livre> listeLivres;

    private FloatingActionButton fabAjouterLivre;

    private AppDatabase database;
    private ExecutorService executorService;

    private ActivityResultLauncher<Intent> addEditLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Associates the XML layout to the activity
        setContentView(R.layout.activity_main);

        // Retrieving the RecyclerView in the layout
        recyclerViewLivres = findViewById(R.id.recyclerViewLivres);

        fabAjouterLivre = findViewById(R.id.fabAjouterLivre);

        // Initializing the Room database
        database = AppDatabase.getInstance(this);

        // A single thread to execute the database operations
        executorService = Executors.newSingleThreadExecutor();

        listeLivres = new ArrayList<>();

        livreAdapter = new LivreAdapter((ArrayList<Livre>) listeLivres, new LivreAdapter.OnLivreClickListener() {
            @Override
            public void onLivreClick(Livre livre) {
                ouvrirDetailLivre(livre);
            }

            @Override
            public void onLivreLongClick(Livre livre, int postion) {
                afficherOptionsLivre(livre);
            }
        });

        recyclerViewLivres.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLivres.setAdapter(livreAdapter);

        initialiserActivityResultLauncher();

        fabAjouterLivre.setOnClickListener(v -> ouvrirFormulaireAjout());

        chargerLivresDepuisRoom();
    }

    private void initialiserActivityResultLauncher() {
        addEditLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();

                        Livre livre = (Livre) data.getSerializableExtra(AddEditActivity.EXTRA_LIVRE);
                        String mode = data.getStringExtra(AddEditActivity.EXTRA_MODE);

                        if (livre == null) {
                            return;
                        }

                        if (AddEditActivity.MODE_ADD.equals(mode)) {
                            ajouterLivreDansRoom(livre);
                        } else if (AddEditActivity.MODE_EDIT.equals(mode)) {
                            modifierLivreDansRoom(livre);
                        }
                    }
                }
        );
    }

    private void chargerLivresDepuisRoom() {
        executorService.execute(() -> {
            // This operation is performed in a secondary thread.
            List<Livre> livresDepuisBase = database.livreDao().getAllLivres();

            runOnUiThread(() -> {
                // Mise à jour de l'interface sur le thread UI.
                listeLivres.clear();
                listeLivres.addAll(livresDepuisBase);
                livreAdapter.notifyDataSetChanged();
            });
        });
    }

    private void ajouterLivreDansRoom(Livre livre) {
        executorService.execute(() -> {
            // The ID will be generated automatically by Room.
            livre.setId(0);

            database.livreDao().insert(livre);

            runOnUiThread(() -> {
                Toast.makeText(this, "Livre ajouté", Toast.LENGTH_SHORT).show();
                chargerLivresDepuisRoom();
            });
        });
    }

    private void modifierLivreDansRoom(Livre livre) {
        executorService.execute(() -> {
            database.livreDao().update(livre);

            runOnUiThread(() -> {
                Toast.makeText(this, "Livre modifié", Toast.LENGTH_SHORT).show();
                chargerLivresDepuisRoom();
            });
        });
    }

    private void supprimerLivreDansRoom(Livre livre) {
        executorService.execute(() -> {
            database.livreDao().delete(livre);

            runOnUiThread(() -> {
                Toast.makeText(this, "Livre supprimé", Toast.LENGTH_SHORT).show();
                chargerLivresDepuisRoom();
            });
        });
    }

    private void ouvrirFormulaireAjout() {
        Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
        intent.putExtra(AddEditActivity.EXTRA_MODE, AddEditActivity.MODE_ADD);
        addEditLauncher.launch(intent);
    }

    private void ouvrirFormulaireModification(Livre livre) {
        Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
        intent.putExtra(AddEditActivity.EXTRA_MODE, AddEditActivity.MODE_EDIT);
        intent.putExtra(AddEditActivity.EXTRA_LIVRE, livre);
        addEditLauncher.launch(intent);
    }

    private void ouvrirDetailLivre(Livre livre) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("livre", livre);
        startActivity(intent);
    }

    private void afficherOptionsLivre(Livre livre) {
        String[] options = {"Modifier", "Supprimer"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(livre.getTitre());
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                ouvrirFormulaireModification(livre);
            } else if (which == 1) {
                confirmerSuppression(livre);
            }
        });
        builder.show();
    }

    private void confirmerSuppression(Livre livre) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer le livre")
                .setMessage("Voulez-vous vraiment supprimer ce livre ?")
                .setPositiveButton("Supprimer", (dialog, which) -> supprimerLivreDansRoom(livre))
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // The thread is properly closed when the activity is destroyed.
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
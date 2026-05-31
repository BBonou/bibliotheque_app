package com.esp.bibliothequeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    // List display component
    private RecyclerView recyclerViewLivres;

    // Custom adapter
    private LivreAdapter livreAdapter;

    // List of books displayed
    private List<Livre> listeLivres;

    // Empty state container
    private LinearLayout layoutEmptyState;

    private FloatingActionButton fabAjouterLivre;
    private SearchView searchView;

    private AppDatabase database;
    private ExecutorService executorService;

    private ActivityResultLauncher<Intent> addEditLauncher;

    // Tracks the position of the book being edited
    private int positionEnCoursDEdition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind views
        recyclerViewLivres = findViewById(R.id.recyclerViewLivres);
        fabAjouterLivre = findViewById(R.id.fabAjouterLivre);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        searchView = findViewById(R.id.searchView);

        // Initialize Room database
        database = AppDatabase.getInstance(this);

        // Single background thread for database operations
        executorService = Executors.newSingleThreadExecutor();

        listeLivres = new ArrayList<>();

        livreAdapter = new LivreAdapter(listeLivres, new LivreAdapter.OnLivreClickListener() {
            @Override
            public void onLivreClick(Livre livre) {
                ouvrirDetailLivre(livre);
            }

            @Override
            public void onEditClick(Livre livre, int position) {
                positionEnCoursDEdition = position;
                ouvrirFormulaireModification(livre);
            }

            @Override
            public void onDeleteClick(Livre livre, int position) {
                confirmerSuppression(livre, position);
            }
        });

        recyclerViewLivres.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLivres.setAdapter(livreAdapter);

        initialiserActivityResultLauncher();
        initialiserSearchView();

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

                        if (livre == null) return;

                        if (AddEditActivity.MODE_ADD.equals(mode)) {
                            ajouterLivreDansRoom(livre);
                        } else if (AddEditActivity.MODE_EDIT.equals(mode)) {
                            modifierLivreDansRoom(livre, positionEnCoursDEdition);
                        }
                    }
                }
        );
    }

    private void initialiserSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                rechercherLivres(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter in real time as the user types
                rechercherLivres(newText);
                return true;
            }
        });
    }

    private void rechercherLivres(String query) {
        executorService.execute(() -> {
            List<Livre> resultats;
            if (query == null || query.trim().isEmpty()) {
                resultats = database.livreDao().getAllLivres();
            } else {
                resultats = database.livreDao().searchByTitle(query.trim());
            }
            final List<Livre> finalResultats = resultats;
            runOnUiThread(() -> {
                livreAdapter.setLivres(finalResultats);
                mettreAJourEtatVide(finalResultats.isEmpty());
            });
        });
    }

    private void chargerLivresDepuisRoom() {
        executorService.execute(() -> {
            List<Livre> livresDepuisBase = database.livreDao().getAllLivres();
            runOnUiThread(() -> {
                listeLivres.clear();
                listeLivres.addAll(livresDepuisBase);
                livreAdapter.notifyDataSetChanged();
                mettreAJourEtatVide(listeLivres.isEmpty());
            });
        });
    }

    private void ajouterLivreDansRoom(Livre livre) {
        executorService.execute(() -> {
            // ID is auto-generated by Room
            livre.setId(0);
            database.livreDao().insert(livre);

            // Reload to get the generated ID, then insert at top
            List<Livre> livresDepuisBase = database.livreDao().getAllLivres();
            runOnUiThread(() -> {
                if (!livresDepuisBase.isEmpty()) {
                    Livre livreInsere = livresDepuisBase.get(0);
                    listeLivres.add(0, livreInsere);
                    livreAdapter.notifyItemInserted(0);
                    recyclerViewLivres.scrollToPosition(0);
                }
                mettreAJourEtatVide(listeLivres.isEmpty());
                Toast.makeText(this, R.string.toast_book_added, Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void modifierLivreDansRoom(Livre livre, int position) {
        executorService.execute(() -> {
            database.livreDao().update(livre);
            runOnUiThread(() -> {
                if (position >= 0 && position < listeLivres.size()) {
                    listeLivres.set(position, livre);
                    livreAdapter.notifyItemChanged(position);
                }
                Toast.makeText(this, R.string.toast_book_edited, Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void supprimerLivreDansRoom(Livre livre, int position) {
        executorService.execute(() -> {
            database.livreDao().delete(livre);
            runOnUiThread(() -> {
                if (position >= 0 && position < listeLivres.size()) {
                    listeLivres.remove(position);
                    livreAdapter.notifyItemRemoved(position);
                }
                mettreAJourEtatVide(listeLivres.isEmpty());
                Toast.makeText(this, R.string.toast_book_deleted, Toast.LENGTH_SHORT).show();
            });
        });
    }

    // Show or hide the empty state depending on whether the list is empty
    private void mettreAJourEtatVide(boolean estVide) {
        if (estVide) {
            recyclerViewLivres.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerViewLivres.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }

    private void ouvrirFormulaireAjout() {
        positionEnCoursDEdition = -1;
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

    private void confirmerSuppression(Livre livre, int position) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)
                .setPositiveButton(R.string.dialog_confirm_delete, (dialog, which) ->
                        supprimerLivreDansRoom(livre, position))
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Properly shut down the background thread
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}

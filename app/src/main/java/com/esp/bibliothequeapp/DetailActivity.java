package com.esp.bibliothequeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    private TextView tvTitre;
    private TextView tvAuteur;
    private TextView tvIsbn;
    private TextView tvAnnee;
    private TextView tvDisponibilite;
    private Button btnModifier;
    private ImageButton btnBack;

    private Livre livre;

    private ActivityResultLauncher<Intent> editLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Bind views
        tvTitre = findViewById(R.id.tvTitre);
        tvAuteur = findViewById(R.id.tvAuteur);
        tvIsbn = findViewById(R.id.tvIsbn);
        tvAnnee = findViewById(R.id.tvAnnee);
        tvDisponibilite = findViewById(R.id.tvDisponibilite);
        btnModifier = findViewById(R.id.btnModifier);
        btnBack = findViewById(R.id.btnBack);

        // Custom back button
        btnBack.setOnClickListener(v -> finish());

        // Retrieve the Book from the Intent
        livre = (Livre) getIntent().getSerializableExtra("livre");

        if (livre != null) {
            afficherLivre(livre);
        }

        initialiserEditLauncher();

        btnModifier.setOnClickListener(v -> {
            if (livre != null) {
                ouvrirFormulaireModification(livre);
            }
        });
    }

    // Display the book data in the views
    private void afficherLivre(Livre l) {
        tvTitre.setText(l.getTitre());
        tvAuteur.setText(getString(R.string.label_author_prefix) + l.getAuteur());
        tvIsbn.setText(getString(R.string.label_isbn_prefix) + l.getIsbn());

        // Show year only if set
        if (l.getAnneePublication() > 0) {
            tvAnnee.setText(getString(R.string.label_year_prefix) + l.getAnneePublication());
        } else {
            tvAnnee.setText("");
        }

        // Availability badge with correct drawable
        // Bug fix: original code had typo "Indispoible"
        if (l.isDisponible()) {
            tvDisponibilite.setText(R.string.badge_available);
            tvDisponibilite.setBackgroundResource(R.drawable.badge_available);
        } else {
            tvDisponibilite.setText(R.string.badge_unavailable);
            tvDisponibilite.setBackgroundResource(R.drawable.badge_unavailable);
        }
    }

    private void initialiserEditLauncher() {
        editLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Livre livreMisAJour = (Livre) result.getData()
                                .getSerializableExtra(AddEditActivity.EXTRA_LIVRE);
                        if (livreMisAJour != null) {
                            // Update local reference and refresh the displayed data
                            livre = livreMisAJour;
                            afficherLivre(livre);
                        }
                    }
                }
        );
    }

    private void ouvrirFormulaireModification(Livre l) {
        Intent intent = new Intent(DetailActivity.this, AddEditActivity.class);
        intent.putExtra(AddEditActivity.EXTRA_MODE, AddEditActivity.MODE_EDIT);
        intent.putExtra(AddEditActivity.EXTRA_LIVRE, l);
        editLauncher.launch(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

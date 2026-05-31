package com.esp.bibliothequeapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditActivity extends AppCompatActivity {

    public static final String EXTRA_MODE = "MODE";
    public static final String EXTRA_LIVRE = "LIVRE";
    public static final String EXTRA_POSITION = "POSITION";

    public static final String MODE_ADD = "ADD";
    public static final String MODE_EDIT = "EDIT";

    private EditText etTitre;
    private EditText etAuteur;
    private EditText etIsbn;
    private Switch switchDisponible;
    private Button btnEnregistrer;
    private TextView tvTitreFormulaire;

    private String mode;
    private Livre livreAModifier;
    private int positionLivre = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        // Activates the back button if an ActionBar is available
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Java binding with XML views
        tvTitreFormulaire = findViewById(R.id.tvTitreFormulaire);
        etTitre = findViewById(R.id.etTitre);
        etAuteur = findViewById(R.id.etAuteur);
        etIsbn = findViewById(R.id.etIsbn);
        switchDisponible = findViewById(R.id.switchDisponible);
        btnEnregistrer = findViewById(R.id.btnEnregistrer);

        // Retrieving the mode sent by MainActivity
        Intent intent = getIntent();
        mode = intent.getStringExtra(EXTRA_MODE);

        if (MODE_EDIT.equals(mode)) {
            // Edit mode
            tvTitreFormulaire.setText("Modifier le livre");

            livreAModifier = (Livre) intent.getSerializableExtra(EXTRA_LIVRE);
            positionLivre = intent.getIntExtra(EXTRA_POSITION, -1);

            if (livreAModifier != null) {
                etTitre.setText(livreAModifier.getTitre());
                etAuteur.setText(livreAModifier.getTitre());
                etIsbn.setText(livreAModifier.getTitre());
                switchDisponible.setChecked(livreAModifier.isDisponible());
            }
        } else {
            // Default add mode
            mode = MODE_ADD;
            tvTitreFormulaire.setText("Ajouter un livre");
        }

        btnEnregistrer.setOnClickListener(v -> enregistrerLivre());
    }

    private void enregistrerLivre() {
        String titre = etTitre.getText().toString().trim();
        String auteur = etAuteur.getText().toString().trim();
        String isbn = etIsbn.getText().toString().trim();
        boolean disponible = switchDisponible.isChecked();

        if (!validerFormulaire(titre, auteur, isbn)) {
            return;
        }

        Livre livre;

        if (MODE_EDIT.equals(mode) && livreAModifier != null) {
            // We keep the same ID for the modified book
            livre = new Livre(
                    livreAModifier.getId(),
                    titre,
                    auteur,
                    isbn,
                    disponible
            );
        } else {
            // The ID will be adjusted on the MainActivity side.
            livre = new Livre(
                    0,
                    titre,
                    auteur,
                    isbn,
                    disponible
            );
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_MODE, mode);
        resultIntent.putExtra(EXTRA_LIVRE, livre);
        resultIntent.putExtra(EXTRA_POSITION, positionLivre);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private boolean validerFormulaire(String titre, String auteur, String isbn) {
        boolean formulaireValide = true;

        if (TextUtils.isEmpty(titre)) {
            etTitre.setError("Le titre est obligatoire");
            formulaireValide = false;
        }

        if (TextUtils.isEmpty(auteur)) {
            etAuteur.setError("L'auteur est obligatoire");
            formulaireValide = false;
        }

        if (!TextUtils.isEmpty(isbn) && isbn.length() < 10) {
            etIsbn.setError("L'ISBN doit contenir au moins 10 caractères");
            formulaireValide = false;
        }

        return formulaireValide;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
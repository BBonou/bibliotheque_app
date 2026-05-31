package com.esp.bibliothequeapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class AddEditActivity extends AppCompatActivity {

    public static final String EXTRA_MODE = "MODE";
    public static final String EXTRA_LIVRE = "LIVRE";

    public static final String MODE_ADD = "ADD";
    public static final String MODE_EDIT = "EDIT";

    private EditText etTitre;
    private EditText etAuteur;
    private EditText etIsbn;
    private EditText etAnneePublication;
    private SwitchMaterial switchDisponible;
    private Button btnEnregistrer;
    private TextView tvTitreFormulaire;

    private String mode;
    private Livre livreAModifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        // Activate the back button if an ActionBar is present
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Bind views
        tvTitreFormulaire = findViewById(R.id.tvTitreFormulaire);
        etTitre = findViewById(R.id.etTitre);
        etAuteur = findViewById(R.id.etAuteur);
        etIsbn = findViewById(R.id.etIsbn);
        etAnneePublication = findViewById(R.id.etAnneePublication);
        switchDisponible = findViewById(R.id.switchDisponible);
        btnEnregistrer = findViewById(R.id.btnEnregistrer);

        // Retrieve the mode sent by MainActivity
        Intent intent = getIntent();
        mode = intent.getStringExtra(EXTRA_MODE);

        if (MODE_EDIT.equals(mode)) {
            // Edit mode: pre-fill the form with existing book data
            tvTitreFormulaire.setText(R.string.form_title_edit);
            livreAModifier = (Livre) intent.getSerializableExtra(EXTRA_LIVRE);

            if (livreAModifier != null) {
                etTitre.setText(livreAModifier.getTitre());
                etAuteur.setText(livreAModifier.getAuteur());
                etIsbn.setText(livreAModifier.getIsbn());
                switchDisponible.setChecked(livreAModifier.isDisponible());

                // Display year only if set
                if (livreAModifier.getAnneePublication() > 0) {
                    etAnneePublication.setText(String.valueOf(livreAModifier.getAnneePublication()));
                }
            }
        } else {
            // Default: add mode
            mode = MODE_ADD;
            tvTitreFormulaire.setText(R.string.form_title_add);
        }

        btnEnregistrer.setOnClickListener(v -> enregistrerLivre());
    }

    private void enregistrerLivre() {
        String titre = etTitre.getText().toString().trim();
        String auteur = etAuteur.getText().toString().trim();
        String isbn = etIsbn.getText().toString().trim();
        String anneeStr = etAnneePublication.getText().toString().trim();
        boolean disponible = switchDisponible.isChecked();

        if (!validerFormulaire(titre, auteur, isbn, anneeStr)) {
            return;
        }

        // Parse year: 0 means not provided
        int annee = 0;
        if (!TextUtils.isEmpty(anneeStr)) {
            annee = Integer.parseInt(anneeStr);
        }

        Livre livre;

        if (MODE_EDIT.equals(mode) && livreAModifier != null) {
            // Keep the same ID for the updated book
            livre = new Livre(livreAModifier.getId(), titre, auteur, isbn, disponible, annee);
        } else {
            // ID will be set to 0; Room will auto-generate it
            livre = new Livre(0, titre, auteur, isbn, disponible, annee);
        }

        // Show confirmation Toast
        if (MODE_ADD.equals(mode)) {
            Toast.makeText(this, R.string.toast_book_added, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.toast_book_edited, Toast.LENGTH_SHORT).show();
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_MODE, mode);
        resultIntent.putExtra(EXTRA_LIVRE, livre);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private boolean validerFormulaire(String titre, String auteur, String isbn, String anneeStr) {
        boolean formulaireValide = true;

        if (TextUtils.isEmpty(titre)) {
            etTitre.setError(getString(R.string.error_title_required));
            formulaireValide = false;
        }

        if (TextUtils.isEmpty(auteur)) {
            etAuteur.setError(getString(R.string.error_author_required));
            formulaireValide = false;
        }

        if (!TextUtils.isEmpty(isbn) && isbn.length() < 10) {
            etIsbn.setError(getString(R.string.error_isbn_length));
            formulaireValide = false;
        }

        // Validate year only if provided
        if (!TextUtils.isEmpty(anneeStr)) {
            try {
                int annee = Integer.parseInt(anneeStr);
                if (annee < 1000 || annee > 2100) {
                    etAnneePublication.setError(getString(R.string.error_year_invalid));
                    formulaireValide = false;
                }
            } catch (NumberFormatException e) {
                etAnneePublication.setError(getString(R.string.error_year_invalid));
                formulaireValide = false;
            }
        }

        return formulaireValide;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

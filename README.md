# BibliothequeApp

Application Android de gestion d'une bibliothèque personnelle, développée en Java avec Android Studio.

---

## Description

BibliothequeApp permet de gérer une collection de livres directement depuis un appareil Android. L'utilisateur peut ajouter, consulter, modifier et supprimer des livres, avec persistance locale des données grâce à Room (SQLite). Une barre de recherche permet de filtrer les livres par titre en temps réel.

---

## Fonctionnalités

- **Liste des livres** — affichage sous forme de cartes avec titre, auteur, ISBN et badge de disponibilité
- **Ajout** — formulaire avec titre, auteur, ISBN, année de publication et statut de disponibilité
- **Modification** — pré-remplissage du formulaire avec les données existantes
- **Suppression** — dialogue de confirmation avant suppression
- **Détail** — écran dédié avec toutes les informations du livre et bouton Modifier
- **Recherche** — filtrage en temps réel par titre via `SearchView`
- **État vide** — message et illustration affichés quand la liste est vide
- **Persistance** — base de données SQLite locale via Room, avec migration automatique

---

## Architecture

Le projet suit une architecture simple et directe, adaptée à un niveau pédagogique :

```
app/src/main/
├── java/com/esp/bibliothequeapp/
│   ├── Livre.java              # Entité Room (modèle de données)
│   ├── LivreDao.java           # Interface DAO (requêtes SQL)
│   ├── AppDatabase.java        # Singleton Room Database (version 2)
│   ├── LivreAdapter.java       # Adaptateur RecyclerView
│   ├── MainActivity.java       # Liste, recherche, suppression
│   ├── AddEditActivity.java    # Formulaire ajout / modification
│   └── DetailActivity.java     # Affichage détaillé d'un livre
└── res/
    ├── layout/                 # activity_main, activity_detail,
    │                           # activity_add_edit, item_livre
    ├── drawable/               # Badges, icônes vectoriels, boutons arrondis
    └── values/                 # colors, strings, themes
```

**Patterns utilisés :**
- Singleton pour `AppDatabase`
- `ActivityResultLauncher` pour la communication entre activités
- `ExecutorService` (thread unique) pour les opérations Room en arrière-plan
- `RecyclerView` avec `ViewHolder` statique

---

## Stack technique

| Composant | Version |
|-----------|---------|
| Android Studio | Ladybug (2024.x) |
| Java | 1.8 |
| compileSdk / targetSdk | 34 |
| minSdk | 34 |
| Gradle | 8.6.1 |
| Room | 2.8.4 |
| Material Components | 1.10.0 |
| AppCompat | 1.6.1 |
| RecyclerView | 1.3.2 |
| CardView | 1.0.0 |
| ConstraintLayout | 2.1.4 |

---

## Base de données

La base de données Room s'appelle `bibliotheque_database` et contient une table `livres`.

### Schéma — table `livres`

| Colonne | Type | Description |
|---------|------|-------------|
| `id` | INTEGER (PK, auto) | Identifiant unique |
| `titre` | TEXT | Titre du livre |
| `auteur` | TEXT | Nom de l'auteur |
| `isbn` | TEXT | Numéro ISBN (optionnel) |
| `disponible` | INTEGER (bool) | Disponibilité (0 ou 1) |
| `anneePublication` | INTEGER | Année de publication (0 si non renseignée) |

### Migration

La colonne `anneePublication` a été ajoutée en **version 2** via une migration `ALTER TABLE` :

```java
// AppDatabase.java
static final Migration MIGRATION_1_2 = new Migration(1, 2) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL(
            "ALTER TABLE livres ADD COLUMN anneePublication INTEGER NOT NULL DEFAULT 0"
        );
    }
};
```

Si la base version 1 est déjà installée sur l'appareil, la migration s'exécute automatiquement sans perte de données.

---

## Installation

### Prérequis

- Android Studio **Ladybug** ou plus récent
- JDK 8 ou supérieur
- Un appareil ou émulateur Android API 34+

### Étapes

1. Extraire le ZIP du projet
2. Ouvrir Android Studio → **File → Open** → sélectionner le dossier `projet_final`
3. Attendre la synchronisation Gradle (*Sync Project with Gradle Files*)
4. Lancer l'application sur un émulateur ou appareil connecté (**Run ▶️**)

> **Note :** ne pas ouvrir le dossier `app/` directement — ouvrir le dossier racine du projet.

---

## Utilisation

| Action | Comment faire |
|--------|--------------|
| Voir les livres | Écran principal au lancement |
| Ajouter un livre | Bouton **+** (FAB rose, en bas à droite) |
| Voir le détail | Appuyer sur une carte |
| Modifier | Bouton ✏️ sur la carte **ou** bouton *Modifier* dans le détail |
| Supprimer | Bouton 🗑️ sur la carte → confirmer |
| Rechercher | Barre de recherche en haut de l'écran (filtrage instantané) |

---

## Structure des fichiers modifiés

Par rapport au projet initial, les fichiers suivants ont été modifiés ou créés :

**Java**
- `Livre.java` — ajout du champ `anneePublication`
- `LivreDao.java` — ajout de `searchByTitle()`
- `AppDatabase.java` — version 2 + migration
- `LivreAdapter.java` — boutons edit/delete, badges arrondis, `setLivres()`
- `MainActivity.java` — SearchView, état vide, `notifyItemInserted/Changed/Removed`
- `AddEditActivity.java` — champ année, `SwitchMaterial`, validation
- `DetailActivity.java` — bouton Modifier, `tvAnnee`, badges, correction typo

**Ressources**
- `values/colors.xml` — palette complète
- `values/strings.xml` — toutes les chaînes externalisées
- `values/themes.xml` — thème enrichi
- `layout/activity_main.xml` — AppBar, SearchView, état vide
- `layout/item_livre.xml` — MaterialCardView, icône livre, boutons icônes
- `layout/activity_detail.xml` — zone couverture, CardView, badge centré
- `layout/activity_add_edit.xml` — champ année, SwitchMaterial, bouton arrondi
- `drawable/` — 9 nouveaux fichiers vectoriels (badges, icônes, boutons)

---

## Auteur

Projet réalisé dans le cadre d'un cours Android — ESP Dakar.

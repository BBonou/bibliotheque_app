package com.esp.bibliothequeapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LivreAdapter extends RecyclerView.Adapter<LivreAdapter.LivreViewHolder> {

    public interface OnLivreClickListener {
        void onLivreClick(Livre livre);
        void onLivreLongClick(Livre livre, int postion);
    }

    private List<Livre> listeLivres;
    private OnLivreClickListener listener;

    // Adapter constructor
    public LivreAdapter(ArrayList<Livre> listeLivres, OnLivreClickListener listener) {
        this.listeLivres = listeLivres;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LivreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Coverts the XML file into an object view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_livre, parent, false);
        return new LivreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LivreViewHolder holder, int position) {
        // Retrieve the book from the current position.
        Livre livre = listeLivres.get(position);

        // Inject the book data into the views
        holder.tvTitreLivre.setText(livre.getTitre());
        holder.tvAuteurLivre.setText("Auteur : " + livre.getAuteur());
        holder.tvIsbnLivre.setText("ISBN : " + livre.getIsbn());

        // Availability badge management
        if (livre.isDisponible()) {
            holder.tvDisponibilite.setText("Disponible");
            holder.tvDisponibilite.setBackgroundColor(Color.parseColor("#2E7D32"));
        } else {
            holder.tvDisponibilite.setText("Indisponible");
            holder.tvDisponibilite.setBackgroundColor(Color.parseColor("#C62828"));
        }

        // Single click: open book details
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLivreClick(livre);
            }
        });

        // Long press: open the form in edit mode
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                int currentPosition = holder.getAdapterPosition();

                if (currentPosition != RecyclerView.NO_POSITION) {
                    listener.onLivreLongClick(livre, currentPosition);
                }
            }

            return true;
        });
    }

    @Override
    public int getItemCount() {
        // Returns the total number of items to display
        return listeLivres.size();
    }

    // Internal static viewholder
    public static class LivreViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitreLivre;
        TextView tvAuteurLivre;
        TextView tvIsbnLivre;
        TextView tvDisponibilite;

        public LivreViewHolder(@NotNull View itemView) {
            super(itemView);

            // Linking XML components and Java variables
            tvTitreLivre = itemView.findViewById(R.id.tvTitreLivre);
            tvAuteurLivre = itemView.findViewById(R.id.tvAuteurLivre);
            tvIsbnLivre = itemView.findViewById(R.id.tvIsbnLivre);
            tvDisponibilite = itemView.findViewById(R.id.tvDisponibilite);
        }
    }
}

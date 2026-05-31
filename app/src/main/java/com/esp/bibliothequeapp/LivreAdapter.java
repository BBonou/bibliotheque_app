package com.esp.bibliothequeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LivreAdapter extends RecyclerView.Adapter<LivreAdapter.LivreViewHolder> {

    public interface OnLivreClickListener {
        void onLivreClick(Livre livre);
        void onEditClick(Livre livre, int position);
        void onDeleteClick(Livre livre, int position);
    }

    private List<Livre> listeLivres;
    private OnLivreClickListener listener;

    // Adapter constructor
    public LivreAdapter(List<Livre> listeLivres, OnLivreClickListener listener) {
        this.listeLivres = listeLivres;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LivreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_livre, parent, false);
        return new LivreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LivreViewHolder holder, int position) {
        // Retrieve the book at the current position
        Livre livre = listeLivres.get(position);

        // Inject book data into views
        holder.tvTitreLivre.setText(livre.getTitre());
        holder.tvAuteurLivre.setText("Auteur : " + livre.getAuteur());
        holder.tvIsbnLivre.setText("ISBN : " + livre.getIsbn());

        // Availability badge: rounded green or red drawable
        if (livre.isDisponible()) {
            holder.tvDisponibilite.setText(R.string.badge_available);
            holder.tvDisponibilite.setBackgroundResource(R.drawable.badge_available);
        } else {
            holder.tvDisponibilite.setText(R.string.badge_unavailable);
            holder.tvDisponibilite.setBackgroundResource(R.drawable.badge_unavailable);
        }

        // Single click: open book details
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLivreClick(livre);
            }
        });

        // Edit icon button click
        holder.btnEditLivre.setOnClickListener(v -> {
            if (listener != null) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    listener.onEditClick(livre, currentPosition);
                }
            }
        });

        // Delete icon button click
        holder.btnDeleteLivre.setOnClickListener(v -> {
            if (listener != null) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(livre, currentPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listeLivres.size();
    }

    // Add a book and notify the RecyclerView at the correct position
    public void addLivre(Livre livre) {
        listeLivres.add(0, livre);
        notifyItemInserted(0);
    }

    // Update a book at a given position
    public void updateLivre(Livre livre, int position) {
        listeLivres.set(position, livre);
        notifyItemChanged(position);
    }

    // Remove a book at a given position
    public void removeLivre(int position) {
        listeLivres.remove(position);
        notifyItemRemoved(position);
    }

    // Replace the full list (used for search results)
    public void setLivres(List<Livre> livres) {
        listeLivres.clear();
        listeLivres.addAll(livres);
        notifyDataSetChanged();
    }

    // Internal static ViewHolder
    public static class LivreViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitreLivre;
        TextView tvAuteurLivre;
        TextView tvIsbnLivre;
        TextView tvDisponibilite;
        ImageButton btnEditLivre;
        ImageButton btnDeleteLivre;

        public LivreViewHolder(@NonNull View itemView) {
            super(itemView);
            // Link XML components to Java variables
            tvTitreLivre = itemView.findViewById(R.id.tvTitreLivre);
            tvAuteurLivre = itemView.findViewById(R.id.tvAuteurLivre);
            tvIsbnLivre = itemView.findViewById(R.id.tvIsbnLivre);
            tvDisponibilite = itemView.findViewById(R.id.tvDisponibilite);
            btnEditLivre = itemView.findViewById(R.id.btnEditLivre);
            btnDeleteLivre = itemView.findViewById(R.id.btnDeleteLivre);
        }
    }
}

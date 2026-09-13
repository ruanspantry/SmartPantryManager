package com.example.smartpantrymanager.adapters;

// Imports

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.models.Ingredient;

import java.util.List;

// Class for Binding Ingredient View Data to RecyclerView Layout
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    // List containing ingredients
    private List<Ingredient> ingredientList;

    // Listener for Handling delete actions, declared as final
    private final OnIngredientDeleteListener deleteListener;

    // Data link between Delete button click and host action
    public interface OnIngredientDeleteListener {
        void onDeleteClick(Ingredient ingredient);
    }

    //Constructor
    public IngredientAdapter(List<Ingredient> ingredientList, OnIngredientDeleteListener deleteListener) {
        this.ingredientList = ingredientList;
        this.deleteListener = deleteListener;
    }

    // Creation of ViewHolder instance
    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    // Linking Ingredient Data to UI View, Suppression of minor acceptable warnings
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredientList.get(position);
        holder.tvName.setText(ingredient.getName());
        holder.tvQuantity.setText("Qty: " + ingredient.getQuantity() + " " + ingredient.getUnit());

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) { //Check not null
                deleteListener.onDeleteClick(ingredient);
            }
        });
    }

    //Getting the item count
    @Override
    public int getItemCount() {
        return ingredientList != null ? ingredientList.size() : 0;
    }

    // Updating the internal list for ingredients
    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<Ingredient> newList) {
        this.ingredientList = newList; //Set ingredient list
        notifyDataSetChanged();
    }

    // References to Layout Views
    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity; // TextView
        ImageButton btnDelete; //Button

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvIngredientName); // TextView
            tvQuantity = itemView.findViewById(R.id.tvIngredientQuantity); // TextView
            btnDelete = itemView.findViewById(R.id.btnDeleteIngredient); // Button
        }
    }
}
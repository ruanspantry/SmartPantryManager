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
import com.example.smartpantrymanager.models.Recipe;

import java.util.List;

// Binding Recipe data to RecyclerView layout
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    // List of Recipes
    private List<Recipe> recipeList;

    //Delete button listener, declaring as final
    private final OnRecipeDeleteListener deleteListener;

    // Host Action Link
    public interface OnRecipeDeleteListener {
        void onDeleteClick(Recipe recipe);
    }

    // Constructor
    public RecipeAdapter(List<Recipe> recipeList, OnRecipeDeleteListener deleteListener) {
        this.recipeList = recipeList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    // Linking Recipe Data to UI View, minor warning suppressions
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.tvTitle.setText(recipe.getTitle());
        holder.tvInstructions.setText(recipe.getInstructions());

        if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
            holder.tvIngredients.setText("Ingredients: " + String.join(", ", recipe.getIngredients()));
        } else {
            holder.tvIngredients.setText("Ingredients: None listed");
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(recipe);
            }
        });
    }

    // Getter Item Count
    @Override
    public int getItemCount() {
        return recipeList != null ? recipeList.size() : 0;
    }

    // Updating List internally
    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<Recipe> newList) {
        this.recipeList = newList;
        notifyDataSetChanged();
    }

    // References to Layout Views
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvIngredients, tvInstructions;
        ImageButton btnDelete;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvRecipeTitle);
            tvIngredients = itemView.findViewById(R.id.tvRecipeIngredients);
            tvInstructions = itemView.findViewById(R.id.tvRecipeInstructions);
            btnDelete = itemView.findViewById(R.id.btnDeleteRecipe);
        }
    }
}
package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapters.RecipeAdapter;
import com.example.smartpantrymanager.db.DatabaseHelper;
import com.example.smartpantrymanager.models.Recipe;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

// Displaying recipes matching current pantry inventory
public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecipeAdapter recipeAdapter;
    private TextView tvEmptyState;

    // OnCreate Function
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        dbHelper = new DatabaseHelper(this);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        setupRecyclerView();
        setupBottomNavigation();
    }

    // Loading the details again
    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestedRecipes();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_suggested);
        }
    }

    private void setupRecyclerView() {
        RecyclerView rvSuggested = findViewById(R.id.rvSuggestedRecipes);
        rvSuggested.setLayoutManager(new LinearLayoutManager(this));

        recipeAdapter = new RecipeAdapter(new ArrayList<>(), new RecipeAdapter.OnRecipeDeleteListener() {
            @Override
            public void onDeleteClick(Recipe recipe) {
                boolean deleted = dbHelper.deleteRecipe(recipe.getId());
                if (deleted) {
                    Toast.makeText(SuggestedRecipesActivity.this, recipe.getTitle() + " deleted", Toast.LENGTH_SHORT).show();
                    loadSuggestedRecipes();
                }
            }

            @Override
            public void onItemClick(Recipe recipe) {
                Intent intent = new Intent(SuggestedRecipesActivity.this, RecipeDetailActivity.class);
                intent.putExtra("EXTRA_RECIPE_TITLE", recipe.getTitle());
                intent.putExtra("EXTRA_RECIPE_INSTRUCTIONS", recipe.getInstructions());
                if (recipe.getIngredients() != null) {
                    intent.putStringArrayListExtra("EXTRA_RECIPE_INGREDIENTS", new ArrayList<>(recipe.getIngredients()));
                }
                startActivity(intent);
            }
        });

        rvSuggested.setAdapter(recipeAdapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_suggested);

            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_pantry) {
                    startActivity(new Intent(SuggestedRecipesActivity.this, MainActivity.class));
                    finish(); // Closing the activity maintaining clean
                    return true;
                } else if (itemId == R.id.nav_suggested) {
                    return true; // Already on Suggested screen
                } else if (itemId == R.id.nav_settings) {
                    startActivity(new Intent(SuggestedRecipesActivity.this, SettingsActivity.class));
                    finish();
                    return true;
                }
                return false;
            });
        }
    }

    private void loadSuggestedRecipes() {
        List<Recipe> suggestions = dbHelper.getSuggestedRecipes();
        recipeAdapter.updateList(suggestions);

        if (suggestions.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
        }
    }
}
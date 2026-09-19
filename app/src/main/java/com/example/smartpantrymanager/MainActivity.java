package com.example.smartpantrymanager;

import android.content.Intent;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapters.IngredientAdapter;
import com.example.smartpantrymanager.adapters.RecipeAdapter;
import com.example.smartpantrymanager.db.DatabaseHelper;
import com.example.smartpantrymanager.models.Ingredient;
import com.example.smartpantrymanager.models.Recipe;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

// Main Activity Class which runs at application start
public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private IngredientAdapter ingredientAdapter;
    private RecipeAdapter recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        seedInitialData();
        setupIngredientRecyclerView();
        setupRecipeRecyclerView();

        // Launch AddEditIngredientActivity via Intent
        findViewById(R.id.btnAddIngredient).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditIngredientActivity.class);
            startActivity(intent);
        });

        // Setup Bottom Navigation Bar listener
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_pantry); // Highlighting the Pantry tab as active

            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_pantry) {
                    return true; // When we are already on Pantry view
                } else if (itemId == R.id.nav_suggested) {
                    startActivity(new Intent(MainActivity.this, SuggestedRecipesActivity.class));
                    return true;
                } else if (itemId == R.id.nav_settings) {
                    Toast.makeText(MainActivity.this, "Settings button works.", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });
        }
    }

    //Reloading the items from the database when you go back to the main activity
    @Override
    protected void onResume() {
        super.onResume();
        refreshLists();
    }

    private void seedInitialData() {
        if (dbHelper.getAllIngredients().isEmpty()) {
            long flourId = dbHelper.addIngredient(new Ingredient("Flour", 1.5, "kg"));
            long sugarId = dbHelper.addIngredient(new Ingredient("Sugar", 0.5, "kg"));
            long eggId = dbHelper.addIngredient(new Ingredient("Eggs", 12.0, "pcs"));

            if (flourId != -1 && sugarId != -1) {
                Recipe pancakeRecipe = new Recipe("Pancakes", "Mix ingredients and fry on medium heat until golden brown.");
                dbHelper.addRecipe(pancakeRecipe, Arrays.asList(flourId, sugarId, eggId));
            }
        }
    }

    // The Setup Ingredients Recycle View function
    private void setupIngredientRecyclerView() {
        RecyclerView rvIngredients = findViewById(R.id.rvIngredients);
        if (rvIngredients == null) return;

        rvIngredients.setLayoutManager(new LinearLayoutManager(this));

        List<Ingredient> ingredients = dbHelper.getAllIngredients();
        ingredientAdapter = new IngredientAdapter(ingredients, ingredient -> {
            boolean deleted = dbHelper.deleteIngredient(ingredient.getId());
            if (deleted) {
                Toast.makeText(this, ingredient.getName() + " deleted", Toast.LENGTH_SHORT).show();
                refreshLists();
            }
        });

        rvIngredients.setAdapter(ingredientAdapter);
    }

    // The Setup Recipe Recycler View function
    private void setupRecipeRecyclerView() {
        RecyclerView rvRecipes = findViewById(R.id.rvRecipes);
        if (rvRecipes == null) return;

        rvRecipes.setLayoutManager(new LinearLayoutManager(this));

        List<Recipe> recipes = dbHelper.getAllRecipes();
        recipeAdapter = new RecipeAdapter(recipes, new RecipeAdapter.OnRecipeDeleteListener() {
            @Override
            public void onDeleteClick(Recipe recipe) {
                boolean deleted = dbHelper.deleteRecipe(recipe.getId());
                if (deleted) {
                    Toast.makeText(MainActivity.this, recipe.getTitle() + " deleted", Toast.LENGTH_SHORT).show();
                    refreshLists();
                }
            }

            @Override
            public void onItemClick(Recipe recipe) {
                Intent intent = new Intent(MainActivity.this, RecipeDetailActivity.class);
                intent.putExtra("EXTRA_RECIPE_TITLE", recipe.getTitle());
                intent.putExtra("EXTRA_RECIPE_INSTRUCTIONS", recipe.getInstructions());
                if (recipe.getIngredients() != null) {
                    intent.putStringArrayListExtra("EXTRA_RECIPE_INGREDIENTS", new ArrayList<>(recipe.getIngredients()));
                }
                startActivity(intent);
            }
        });

        rvRecipes.setAdapter(recipeAdapter);
    }


    // The Refreshing Lists function
    private void refreshLists() {
        if (ingredientAdapter != null) {
            ingredientAdapter.updateList(dbHelper.getAllIngredients());
        }
        if (recipeAdapter != null) {
            recipeAdapter.updateList(dbHelper.getAllRecipes());
        }
    }
}
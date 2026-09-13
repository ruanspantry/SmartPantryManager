package com.example.smartpantrymanager;

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

        // Adding some initial sample date
        seedInitialData();

        //Setting up the UI components
        setupIngredientRecyclerView();
        setupRecipeRecyclerView();
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
        recipeAdapter = new RecipeAdapter(recipes, recipe -> {
            boolean deleted = dbHelper.deleteRecipe(recipe.getId());
            if (deleted) {
                Toast.makeText(this, recipe.getTitle() + " deleted", Toast.LENGTH_SHORT).show();
                refreshLists();
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
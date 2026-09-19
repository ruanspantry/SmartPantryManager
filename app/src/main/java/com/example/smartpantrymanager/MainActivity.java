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
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class)); // Open settings
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
            // Seed initial pantry ingredients
            long flourId = dbHelper.addIngredient(new Ingredient("Flour", 1.5, "kg"));
            long sugarId = dbHelper.addIngredient(new Ingredient("Sugar", 0.5, "kg"));
            long eggId = dbHelper.addIngredient(new Ingredient("Eggs", 12.0, "pcs"));
            long milkId = dbHelper.addIngredient(new Ingredient("Milk", 1.0, "L"));
            long butterId = dbHelper.addIngredient(new Ingredient("Butter", 0.25, "kg"));
            long breadId = dbHelper.addIngredient(new Ingredient("Bread", 1.0, "loaf"));
            long saltId = dbHelper.addIngredient(new Ingredient("Salt", 0.1, "kg"));
            long tomatoId = dbHelper.addIngredient(new Ingredient("Tomatoes", 4.0, "pcs"));
            long garlicId = dbHelper.addIngredient(new Ingredient("Garlic", 3.0, "cloves"));
            long riceId = dbHelper.addIngredient(new Ingredient("Rice", 2.0, "kg"));

            // Seed initial Recipes
            dbHelper.addRecipe(new Recipe("Pancakes", "Mix ingredients and fry on medium heat until golden brown."), Arrays.asList(flourId, sugarId, eggId, milkId));
            dbHelper.addRecipe(new Recipe("Egg Omelette", "Start by to whisk eggs with a pinch of salt, then cook on a warm pan with oil."), Arrays.asList(eggId, saltId));
            dbHelper.addRecipe(new Recipe("Scrambled Eggs", "Begin by stirring eggs continuously in a melted buttered pan over medium heat."), Arrays.asList(eggId, butterId, saltId));
            dbHelper.addRecipe(new Recipe("French Toast", "Whisk the egg and milk, dip bread and fry"), Arrays.asList(breadId, eggId, milkId, butterId));
            dbHelper.addRecipe(new Recipe("Sweet Cookies", "Cream butter and sugar, add flour, shape cookies, and bake."), Arrays.asList(flourId, sugarId, butterId));
            dbHelper.addRecipe(new Recipe("Standard Boiled Eggs", "Place eggs in boiling water, cool in cold water, peel."), List.of(eggId));
            dbHelper.addRecipe(new Recipe("Garlic Toast Bread", "Melt butter with garlic, spread over bread and toast."), Arrays.asList(breadId, butterId, garlicId));
            dbHelper.addRecipe(new Recipe("Fried Bread in a Pan", "Melt butter on a hot pan and fry."), Arrays.asList(breadId, butterId));
            dbHelper.addRecipe(new Recipe("Sweet Milk", "Whisk flour, eggs, and milk, fry and spread sugar."), Arrays.asList(flourId, eggId, milkId, sugarId));
            dbHelper.addRecipe(new Recipe("Rice Pudding", "Cooked rice with milk plus sugar slow until ready."), Arrays.asList(riceId, milkId, sugarId));
            dbHelper.addRecipe(new Recipe("Garlic Rice", "Add garlic to butter - put in cooked rice, throw salt on."), Arrays.asList(riceId, garlicId, butterId, saltId));
            dbHelper.addRecipe(new Recipe("Tomato Garlic as a Sauce", "Add garlic, add tomatoes with salt."), Arrays.asList(tomatoId, garlicId, saltId));
            dbHelper.addRecipe(new Recipe("Egg base with Tomatoes", "Add tomatoes and whisk with eggs with salt."), Arrays.asList(eggId, tomatoId, saltId));
            dbHelper.addRecipe(new Recipe("Rice and Egg", "Add rice to eggs and butter with salt."), Arrays.asList(riceId, eggId, butterId, saltId));
            dbHelper.addRecipe(new Recipe("Pudding with Bread", "Add bread and soak in milk and egg, then bake."), Arrays.asList(breadId, milkId, eggId, sugarId));
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
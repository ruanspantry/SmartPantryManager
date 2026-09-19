// Preparing the data Storage using chosen method as SQLite
package com.example.smartpantrymanager.db;

// Package Imports

import com.example.smartpantrymanager.models.Ingredient;
import com.example.smartpantrymanager.models.Recipe;

// Other Imports

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Setting the Database Name
    private static final String DATABASE_NAME = "pantry_manager.db";

    // Setting the Database Versin
    private static final int DATABASE_VERSION = 2;

    // Setting the Table Names, ingredients, recipes, recipe_ingredients
    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    // Setting the Common Columns
    public static final String COLUMN_ID = "_id"; // A common column

    // Setting the Columns for the Ingredients Table
    public static final String COLUMN_INGREDIENT_NAME = "name";
    public static final String COLUMN_INGREDIENT_QTY = "quantity";
    public static final String COLUMN_INGREDIENT_UNIT = "unit";

    // Setting the Columns for the Recipes Table
    public static final String COLUMN_RECIPE_NAME = "title";
    public static final String COLUMN_RECIPE_INSTRUCTIONS = "instructions";

    //Setting the Columns for Recipe-Ingredients Table (Relationship Table)
    public static final String COLUMN_RI_RECIPE_ID = "recipe_id";
    public static final String COLUMN_RI_INGREDIENT_NAME = "ingredient_name";
    public static final String COLUMN_RI_INGREDIENT_ID = "ingredient_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating the Ingredients Table
        String CREATE_INGREDIENTS_TABLE = "CREATE TABLE " + TABLE_INGREDIENTS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_INGREDIENT_NAME + " TEXT UNIQUE NOT NULL, "
                + COLUMN_INGREDIENT_QTY + " REAL NOT NULL, "
                + COLUMN_INGREDIENT_UNIT + " TEXT NOT NULL);";

        // Creating the Recipes Table
        String CREATE_RECIPES_TABLE = "CREATE TABLE " + TABLE_RECIPES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_RECIPE_NAME + " TEXT UNIQUE NOT NULL, "
                + COLUMN_RECIPE_INSTRUCTIONS + " TEXT NOT NULL);";

        // Creating the Recipe-Ingredients Table
        String CREATE_RI_TABLE = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_RI_RECIPE_ID + " INTEGER NOT NULL, "
                + COLUMN_RI_INGREDIENT_ID + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + COLUMN_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_ID + ") ON DELETE CASCADE, "
                + "FOREIGN KEY(" + COLUMN_RI_INGREDIENT_ID + ") REFERENCES " + TABLE_INGREDIENTS + "(" + COLUMN_ID + ") ON DELETE CASCADE);";

        // Executing the Table Creation Statements
        db.execSQL(CREATE_INGREDIENTS_TABLE);
        db.execSQL(CREATE_RECIPES_TABLE);
        db.execSQL(CREATE_RI_TABLE);
    }

    // Overriding
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tables in case there is conflicts when structure changes
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS); //recipes_ingredients
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS); // ingredients
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES); //recipes

        // Building the Database Schema
        onCreate(db);
    }

    // Overriding
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Set Foreign Key Constraints to ensure for Data Integrity
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Crud Operations for Ingredients
    // Inserting a new ingredient
    public long addIngredient(Ingredient ingredient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_INGREDIENT_NAME, ingredient.getName());
        values.put(COLUMN_INGREDIENT_QTY, ingredient.getQuantity());
        values.put(COLUMN_INGREDIENT_UNIT, ingredient.getUnit());

        long id = db.insert(TABLE_INGREDIENTS, null, values);
        db.close();
        return id; // Returning the id of the inserted or otherwise -1 for errors
    }

    // Getting ingredients from the database
    public List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredientList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_INGREDIENTS + " ORDER BY " + COLUMN_INGREDIENT_NAME + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_NAME));
                double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_QTY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_UNIT));

                Ingredient ingredient = new Ingredient(id, name, quantity, unit);
                ingredientList.add(ingredient);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return ingredientList;
    }

    //Updating the details of an ingredient
    public int updateIngredient(Ingredient ingredient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_INGREDIENT_NAME, ingredient.getName());
        values.put(COLUMN_INGREDIENT_QTY, ingredient.getQuantity());
        values.put(COLUMN_INGREDIENT_UNIT, ingredient.getUnit());

        int rowsAffected = db.update(
                TABLE_INGREDIENTS,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(ingredient.getId())}
        );
        db.close();
        return rowsAffected;
    }

    // Deletion of an ingredient based on its ID
    public boolean deleteIngredient(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(
                TABLE_INGREDIENTS,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rowsDeleted > 0;
    }


    // Implementation of CRUD logic for Recipes
    // Inserting new Recipe and linking ingredients
    public long addRecipe(Recipe recipe, List<Long> ingredientIds) {
        SQLiteDatabase db = this.getWritableDatabase();
        long recipeId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_RECIPE_NAME, recipe.getTitle());
            values.put(COLUMN_RECIPE_INSTRUCTIONS, recipe.getInstructions());

            recipeId = db.insert(TABLE_RECIPES, null, values);

            if (recipeId != -1 && ingredientIds != null) {
                for (Long ingredientId : ingredientIds) {
                    ContentValues linkValues = new ContentValues();
                    linkValues.put(COLUMN_RI_RECIPE_ID, recipeId);
                    linkValues.put(COLUMN_RI_INGREDIENT_ID, ingredientId);
                    db.insert(TABLE_RECIPE_INGREDIENTS, null, linkValues);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }

        return recipeId;
    }

    //Getting all Recipes including with associated ingredients
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipeList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RECIPES + " ORDER BY " + COLUMN_RECIPE_NAME + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME));
                String instructions = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_INSTRUCTIONS));

                // Get ingredient list for this recipe
                List<String> ingredients = getIngredientsForRecipe(db, id);

                Recipe recipe = new Recipe(id, title, instructions, ingredients);
                recipeList.add(recipe);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return recipeList;
    }

    // A Junction table used for ingredients linked to recipe ID
    private List<String> getIngredientsForRecipe(SQLiteDatabase db, long recipeId) {
        List<String> ingredientNames = new ArrayList<>();

        // JOIN recipe_ingredients with ingredients table to fetch the real names
        String query = "SELECT i." + COLUMN_INGREDIENT_NAME +
                " FROM " + TABLE_INGREDIENTS + " i " +
                " INNER JOIN " + TABLE_RECIPE_INGREDIENTS + " ri " +
                " ON i." + COLUMN_ID + " = ri." + COLUMN_RI_INGREDIENT_ID +
                " WHERE ri." + COLUMN_RI_RECIPE_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(recipeId)});

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                ingredientNames.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ingredientNames;
    }

    // Removal of a recipe cascade-deletion of ingredient links
    public boolean deleteRecipe(long recipeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Foreign key constraints will automatically delete linked recipe_ingredients rows
        int rowsDeleted = db.delete(
                TABLE_RECIPES,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(recipeId)}
        );
        db.close();
        return rowsDeleted > 0;
    }

    // Retrieving the recipes that can be made with currently available ingredients in list
    public List<Recipe> getSuggestedRecipes() {
        List<Recipe> suggested = new ArrayList<>();
        List<Recipe> allRecipes = getAllRecipes();
        List<Ingredient> pantryList = getAllIngredients();

        // Store pantry ingredient names in lowercase for case-insensitive matching
        List<String> pantryNames = new ArrayList<>();
        for (Ingredient ing : pantryList) {
            if (ing.getQuantity() > 0) {
                pantryNames.add(ing.getName().toLowerCase().trim());
            }
        }

        // Looping through
        for (Recipe recipe : allRecipes) {
            List<String> required = recipe.getIngredients();
            if (required != null && !required.isEmpty()) {
                boolean canMake = true;
                for (String req : required) {
                    if (!pantryNames.contains(req.toLowerCase().trim())) {
                        canMake = false;
                        break;
                    }
                }
                if (canMake) {
                    suggested.add(recipe);
                }
            }
        }
        return suggested;
    }

}
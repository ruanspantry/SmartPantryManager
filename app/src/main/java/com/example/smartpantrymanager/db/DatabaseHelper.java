// Preparing the data Storage using chosen method as SQLite
package com.example.smartpantrymanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantrymanager.models.Ingredient;
import com.example.smartpantrymanager.models.Recipe;

import java.util.ArrayList;
import java.util.List;

// Database Helper
public class DatabaseHelper extends SQLiteOpenHelper {

    //Setting the Database Name
    private static final String DATABASE_NAME = "pantry_manager.db";

    // Setting the Database Versin
    private static final int DATABASE_VERSION = 3;

    // Setting the Table Names, ingredients, recipes, recipe_ingredients
    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    // Common Column
    public static final String COLUMN_ID = "_id";

    // Columns for Ingredients Table
    public static final String COLUMN_INGREDIENT_NAME = "name";
    public static final String COLUMN_INGREDIENT_QTY = "quantity";
    public static final String COLUMN_INGREDIENT_UNIT = "unit";

    // Columns for Recipes Table
    public static final String COLUMN_RECIPE_NAME = "title";
    public static final String COLUMN_RECIPE_INSTRUCTIONS = "instructions";

    // Columns for Recipe-Ingredients Table
    public static final String COLUMN_RI_RECIPE_ID = "recipe_id";
    public static final String COLUMN_RI_INGREDIENT_NAME = "ingredient_name";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating the Ingredients Table
        String CREATE_INGREDIENTS_TABLE = "CREATE TABLE " + TABLE_INGREDIENTS + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_INGREDIENT_NAME + " TEXT UNIQUE NOT NULL, " + COLUMN_INGREDIENT_QTY + " REAL NOT NULL, " + COLUMN_INGREDIENT_UNIT + " TEXT NOT NULL);";

        // Creating the Recipes Table
        String CREATE_RECIPES_TABLE = "CREATE TABLE " + TABLE_RECIPES + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_RECIPE_NAME + " TEXT UNIQUE NOT NULL, " + COLUMN_RECIPE_INSTRUCTIONS + " TEXT NOT NULL);";

        // Creating the Recipe-Ingredients Table
        String CREATE_RI_TABLE = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_RI_RECIPE_ID + " INTEGER NOT NULL, " + COLUMN_RI_INGREDIENT_NAME + " TEXT NOT NULL, " + "FOREIGN KEY(" + COLUMN_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_ID + ") ON DELETE CASCADE);";

        // Executing the Table Creation Statements
        db.execSQL(CREATE_INGREDIENTS_TABLE);
        db.execSQL(CREATE_RECIPES_TABLE);
        db.execSQL(CREATE_RI_TABLE);
    }

    // Overriding
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
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
        return id;
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

        int rowsAffected = db.update(TABLE_INGREDIENTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(ingredient.getId())});
        db.close();
        return rowsAffected;
    }

    // Deletion of an ingredient based on its ID
    public boolean deleteIngredient(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_INGREDIENTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
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
                    // Query the ingredient name using the passed ID
                    String nameQuery = "SELECT " + COLUMN_INGREDIENT_NAME + " FROM " + TABLE_INGREDIENTS + " WHERE " + COLUMN_ID + " = ?";
                    Cursor cursor = db.rawQuery(nameQuery, new String[]{String.valueOf(ingredientId)});

                    if (cursor.moveToFirst()) {
                        String name = cursor.getString(0);
                        ContentValues linkValues = new ContentValues();
                        linkValues.put(COLUMN_RI_RECIPE_ID, recipeId);
                        linkValues.put(COLUMN_RI_INGREDIENT_NAME, name);
                        db.insert(TABLE_RECIPE_INGREDIENTS, null, linkValues);
                    }
                    cursor.close();
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }

        return recipeId;
    }

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
        String query = "SELECT " + COLUMN_RI_INGREDIENT_NAME + " FROM " + TABLE_RECIPE_INGREDIENTS + " WHERE " + COLUMN_RI_RECIPE_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(recipeId)});

        if (cursor.moveToFirst()) {
            do {
                ingredientNames.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ingredientNames;
    }

    // Removal of a recipe cascade-deletion of ingredient links
    public boolean deleteRecipe(long recipeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Foreign key constraints will automatically delete linked recipe_ingredients rows
        int rowsDeleted = db.delete(TABLE_RECIPES, COLUMN_ID + " = ?", new String[]{String.valueOf(recipeId)});
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
        for (Ingredient item : pantryList) {
            pantryNames.add(item.getName().toLowerCase().trim());
        }

        // Looping through
        for (Recipe recipe : allRecipes) {
            List<String> requiredList = recipe.getIngredients();
            if (requiredList == null || requiredList.isEmpty()) continue;

            boolean canMake = true;

            for (String req : requiredList) {
                String reqName = req.toLowerCase().trim();
                boolean matchFound = false;

                for (String pantryName : pantryNames) {
                    if (isNameMatch(reqName, pantryName)) {
                        matchFound = true;
                        break;
                    }
                }

                if (!matchFound) {
                    canMake = false;
                    break;
                }
            }

            if (canMake) {
                suggested.add(recipe);
            }
        }
        return suggested;
    }

    private boolean isNameMatch(String name1, String name2) {
        if (name1.equals(name2)) return true;
        if (name1.endsWith("s") && name1.substring(0, name1.length() - 1).equals(name2))
            return true;
        if (name2.endsWith("s") && name2.substring(0, name2.length() - 1).equals(name1))
            return true;
        if (name1.endsWith("es") && name1.substring(0, name1.length() - 2).equals(name2))
            return true;
        if (name2.endsWith("es") && name2.substring(0, name2.length() - 2).equals(name1))
            return true;
        return false;
    }
}
// Preparing the data Storage using chosen method as SQLite
package com.example.smartpantrymanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Setting the Database Name
    private static final String DATABASE_NAME = "pantry_manager.db";

    // Setting the Database Versin
    private static final int DATABASE_VERSION = 1;

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
                + COLUMN_RI_INGREDIENT_NAME + " TEXT NOT NULL, "
                + "FOREIGN KEY(" + COLUMN_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COLUMN_ID + ") ON DELETE CASCADE);";

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
}
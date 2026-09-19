package com.example.smartpantrymanager;

// Importing

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

// Display the detail view of a recipe
public class RecipeDetailActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView tvTitle = findViewById(R.id.tvDetailRecipeTitle);
        TextView tvIngredients = findViewById(R.id.tvDetailRecipeIngredients);
        TextView tvInstructions = findViewById(R.id.tvDetailRecipeInstructions);
        Button btnBack = findViewById(R.id.btnBack);

        // Receiving the data passed through Intent extras
        String title = getIntent().getStringExtra("EXTRA_RECIPE_TITLE");
        String instructions = getIntent().getStringExtra("EXTRA_RECIPE_INSTRUCTIONS");
        ArrayList<String> ingredients = getIntent().getStringArrayListExtra("EXTRA_RECIPE_INGREDIENTS");

        // Implementation of display data
        if (title != null) tvTitle.setText(title);
        if (instructions != null) tvInstructions.setText(instructions);
        if (ingredients != null && !ingredients.isEmpty()) {
            tvIngredients.setText("• " + String.join("\n• ", ingredients));
        } else {
            tvIngredients.setText("No ingredients specified.");
        }

        // Implementing the navigation
        btnBack.setOnClickListener(v -> finish());
    }
}
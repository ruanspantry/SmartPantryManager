package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.db.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Settings to be able to manage basic app preferences and view app version
public class SettingsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbHelper = new DatabaseHelper(this);

        Button btnClearData = findViewById(R.id.btnClearData);
        btnClearData.setOnClickListener(v -> showResetConfirmationDialog());

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_settings);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_settings);

            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_pantry) {
                    startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_suggested) {
                    startActivity(new Intent(SettingsActivity.this, SuggestedRecipesActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_settings) {
                    return true; // Already here
                }
                return false;
            });
        }
    }

    private void showResetConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Reset Data")
                .setMessage("Are you sure you want to delete all custom ingredients and restore defaults?")
                .setPositiveButton("Reset", (dialog, which) -> {
                    // Dropping the database or re-seeding it
                    this.deleteDatabase("SmartPantry.db");
                    dbHelper = new DatabaseHelper(this);
                    Toast.makeText(this, "Pantry reset to default settings", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
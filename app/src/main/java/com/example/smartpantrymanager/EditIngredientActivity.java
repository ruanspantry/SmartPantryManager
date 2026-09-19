package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.db.DatabaseHelper;
import com.example.smartpantrymanager.models.Ingredient;

// Class that is meant to be used to edit an ingredient
public class EditIngredientActivity extends AppCompatActivity {

    private EditText etName, etQty, etUnit;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    private Ingredient currentIngredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ingredient);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etEditIngredientName);
        etQty = findViewById(R.id.etEditIngredientQty);
        etUnit = findViewById(R.id.etEditIngredientUnit);
        btnSave = findViewById(R.id.btnSaveIngredient);

        // Retrieving the passed ingredient from Intent
        if (getIntent().hasExtra("EXTRA_INGREDIENT")) {
            currentIngredient = (Ingredient) getIntent().getSerializableExtra("EXTRA_INGREDIENT");

            if (currentIngredient != null) {
                // Populate fields
                etName.setText(currentIngredient.getName());
                etQty.setText(String.valueOf(currentIngredient.getQuantity()));
                etUnit.setText(currentIngredient.getUnit());
            }
        }

        btnSave.setOnClickListener(v -> saveIngredientChanges());
    }

    // Function to save the changes to the ingredient
    private void saveIngredientChanges() {
        String name = etName.getText().toString().trim();
        String qtyStr = etQty.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();

        if (name.isEmpty() || qtyStr.isEmpty() || unit.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(qtyStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number for quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        // Updating the models object and executing the update in the DB
        currentIngredient.setName(name);
        currentIngredient.setQuantity(quantity);
        currentIngredient.setUnit(unit);

        int rowsUpdated = dbHelper.updateIngredient(currentIngredient);

        if (rowsUpdated > 0) {
            Toast.makeText(this, "Ingredient updated successfully!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK); // Refreshing the list
            finish(); // Finishing the activity
        } else {
            Toast.makeText(this, "Failed to update ingredient", Toast.LENGTH_SHORT).show();
        }
    }
}
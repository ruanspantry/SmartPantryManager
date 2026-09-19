package com.example.smartpantrymanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.db.DatabaseHelper;
import com.example.smartpantrymanager.models.Ingredient;

//Creation and validation of new ingredient entries
public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText etName, etQuantity, etUnit;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etIngredientQuantity);
        etUnit = findViewById(R.id.etIngredientUnit);
        Button btnSave = findViewById(R.id.btnSaveIngredient);

        // Event Listener
        btnSave.setOnClickListener(v -> saveIngredient());
    }

    // Extract the data from form and save to the db
    private void saveIngredient() {
        String name = etName.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();

        // Implementation of Input Validation
        if (TextUtils.isEmpty(name)) {
            etName.setError("Please enter a Name");
            return;
        }
        if (TextUtils.isEmpty(qtyStr)) {
            etQuantity.setError("Please enter a Quantity");
            return;
        }
        if (TextUtils.isEmpty(unit)) {
            etUnit.setError("Please enter a Unit");
            return;
        }

        // Verify formatting
        double quantity;
        try {
            quantity = Double.parseDouble(qtyStr);
        } catch (NumberFormatException e) {
            etQuantity.setError("Invalid number format");
            return;
        }

        Ingredient ingredient = new Ingredient(name, quantity, unit);
        long id = dbHelper.addIngredient(ingredient);

        if (id != -1) {
            Toast.makeText(this, "Ingredient saved", Toast.LENGTH_SHORT).show();
            finish(); // Closing activity and returning to the previous screen
        } else {
            Toast.makeText(this, "Error saving the ingredient", Toast.LENGTH_SHORT).show();
        }
    }
}
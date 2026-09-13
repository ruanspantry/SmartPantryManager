package com.example.smartpantrymanager.models;

import java.util.List;

// Class meant to represent a give full recipe with the ingredient list required
public class Recipe {

    // Declaring the id, title, instructions and ingredients variables
    private long id;
    private String title;
    private String instructions;
    private List<String> ingredients;

    // Creating theDefault Constructor for Recipe
    public Recipe() {
    }

    // Creation of the Constructor without the ID (SQLite to handle ID creation)
    public Recipe(String title, String instructions) {
        this.title = title;
        this.instructions = instructions;
    }

    // Creation of the Ful Constructor
    public Recipe(long id, String title, String instructions, List<String> ingredients) {
        this.id = id;
        this.title = title;
        this.instructions = instructions;
        this.ingredients = ingredients;
    }

    // Creation of the Getters and Setters for a Recipe
    // Getter ID
    public long getId() {
        return id;
    }

    //Setter ID
    public void setId(long id) {
        this.id = id;
    }

    // Getter Title
    public String getTitle() {
        return title;
    }

    // Setter Title
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter Instructions
    public String getInstructions() {
        return instructions;
    }

    // Setter Instructions
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    //Getter List of Ingredients
    public List<String> getIngredients() {
        return ingredients;
    }

    // Setter List of Ingredients
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
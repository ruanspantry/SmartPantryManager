package com.example.smartpantrymanager.models;

// Class meant to represent a single ingredient entry that is stored in the pantry db
public class Ingredient {

    private long id;
    private String name;
    private double quantity;
    private String unit;

    // Creation of the Default Constructor
    public Ingredient() {
    }

    // Constructor without ID (When you create new ingredient before inserting in DB, no ID)
    public Ingredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    // Full Constructor - for Reading Existing Data inside Db
    public Ingredient(long id, String name, double quantity, String unit) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    // Getters and Setters
    // Getter ID
    public long getId() {
        return id;
    }

    // Setter Id
    public void setId(long id) {
        this.id = id;
    }

    //Getter Name
    public String getName() {
        return name;
    }

    // Setter Name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for Quantity
    public double getQuantity() {
        return quantity;
    }

    // Setter Quantity
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    // Getter Unit
    public String getUnit() {
        return unit;
    }

    // Setter Unit
    public void setUnit(String unit) {
        this.unit = unit;
    }
}
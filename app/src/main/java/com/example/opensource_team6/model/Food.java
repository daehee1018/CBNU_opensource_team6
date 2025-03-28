package com.example.opensource_team6.model;

public class Food {
    public String name;
    public double kcal;
    public double carb;
    public double protein;
    public double fat;

    public Food(String name, double kcal, double carb, double protein, double fat) {
        this.name = name;
        this.kcal = kcal;
        this.carb = carb;
        this.protein = protein;
        this.fat = fat;
    }
}
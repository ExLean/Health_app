package com.example.health_app.models;

import androidx.annotation.NonNull;

public class Food {

    private int id;
    private String name;
    private float calories;
    private float carbs;
    private float protein;
    private float fat;

    public Food(int id, String name, float calories, float carbs, float protein, float fat) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public float getCalories() {
        return calories;
    }

    @NonNull
    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", calories=" + calories +
                ", carbs=" + carbs +
                ", protein=" + protein +
                ", fat=" + fat +
                '}';
    }
}

package com.example.health_app.models;

import androidx.annotation.NonNull;

import com.example.health_app.models.type.Metric;

public class Product {

    private int id;
    private Food food;
    private int mealId;
    private float amount;
    private Metric metric;

    public Product(int id, Food food, int mealId, float amount, Metric metric) {
        this.id = id;
        this.food = food;
        this.mealId = mealId;
        this.amount = amount;
        this.metric = metric;
    }

    public Product() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    @NonNull
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", foodId=" + food +
                ", mealId=" + mealId +
                ", amount=" + amount +
                ", metric=" + metric +
                '}';
    }
}

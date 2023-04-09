package com.example.health_app.models.requests;

import androidx.annotation.NonNull;

import com.example.health_app.models.type.Metric;

public class ProductRequest {

    private int productId;
    private int foodId;
    private int mealId;
    private float amount;
    private Metric metric;

    public int getMealId() {
        return mealId;
    }

    public int getFoodId() {
        return foodId;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    @NonNull
    @Override
    public String toString() {
        return "ProductRequest{" +
                "productId=" + productId +
                ", foodId=" + foodId +
                ", mealId=" + mealId +
                ", amount=" + amount +
                ", metric=" + metric +
                '}';
    }
}

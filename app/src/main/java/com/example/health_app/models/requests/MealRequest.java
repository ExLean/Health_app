package com.example.health_app.models.requests;

import com.example.health_app.models.type.Metric;

public class MealRequest {

    private int mealId;
    private int historyId;
    private String title;
    private String info;
    private int cookingTime;
    private String creator;
    private float mealAmount;
    private Metric mealMetric;

    public float getMealAmount() {
        return mealAmount;
    }

    public String getTitle() {
        return title;
    }

    public Metric getMetric() {
        return mealMetric;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setAmount(float mealAmount) {
        this.mealAmount = mealAmount;
    }

    public void setMetric(Metric mealMetric) {
        this.mealMetric = mealMetric;
    }
}

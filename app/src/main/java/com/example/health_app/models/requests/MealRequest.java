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

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public float getMealAmount() {
        return mealAmount;
    }

    public void setMealAmount(float mealAmount) {
        this.mealAmount = mealAmount;
    }

    public Metric getMealMetric() {
        return mealMetric;
    }

    public void setMealMetric(Metric mealMetric) {
        this.mealMetric = mealMetric;
    }
}

package com.example.health_app.models.requests;

public class MealRequest {

    private int mealId;
    private int historyId;
    private String title;
    private String info;
    private int cookingTime;
    private String creator;

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
}

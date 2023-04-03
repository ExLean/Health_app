package com.example.health_app.models.requests;

import java.sql.Date;

public class StatsRequest {

    private int statsId;
    private int userId;
    private Date statsDate;
    private float weight;
    private float dailyCalorieIntake;
    private int amountOfCups;
    private float leftCalories;

    public void setStatsId(int statsId) {
        this.statsId = statsId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setStatsDate(Date statsDate) {
        this.statsDate = statsDate;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setDailyCalorieIntake(float dailyCalorieIntake) {
        this.dailyCalorieIntake = dailyCalorieIntake;
    }

    public void setAmountOfCups(int amountOfCups) {
        this.amountOfCups = amountOfCups;
    }

    public void setLeftCalories(float leftCalories) {
        this.leftCalories = leftCalories;
    }
}

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
    private float carbAmount;
    private float fatAmount;
    private float proteinAmount;

    public int getStatsId() {
        return statsId;
    }

    public void setStatsId(int statsId) {
        this.statsId = statsId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getStatsDate() {
        return statsDate;
    }

    public void setStatsDate(Date statsDate) {
        this.statsDate = statsDate;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getDailyCalorieIntake() {
        return dailyCalorieIntake;
    }

    public void setDailyCalorieIntake(float dailyCalorieIntake) {
        this.dailyCalorieIntake = dailyCalorieIntake;
    }

    public int getAmountOfCups() {
        return amountOfCups;
    }

    public void setAmountOfCups(int amountOfCups) {
        this.amountOfCups = amountOfCups;
    }

    public float getLeftCalories() {
        return leftCalories;
    }

    public void setLeftCalories(float leftCalories) {
        this.leftCalories = leftCalories;
    }

    public float getCarbAmount() {
        return carbAmount;
    }

    public void setCarbAmount(float carbAmount) {
        this.carbAmount = carbAmount;
    }

    public float getFatAmount() {
        return fatAmount;
    }

    public void setFatAmount(float fatAmount) {
        this.fatAmount = fatAmount;
    }

    public float getProteinAmount() {
        return proteinAmount;
    }

    public void setProteinAmount(float proteinAmount) {
        this.proteinAmount = proteinAmount;
    }
}

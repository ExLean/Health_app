package com.example.health_app.models;

import java.sql.Date;

public class Stats {

    private int id;
    private User user;
    private float weight;
    private float dailyCalorieIntake;
    private int amountOfCups;
    private float leftCalories;
    private Date date;

    public Stats(int id, User user, float weight, float dailyCalorieIntake, int amountOfCups, float leftCalories, Date date) {
        this.id = id;
        this.user = user;
        this.weight = weight;
        this.dailyCalorieIntake = dailyCalorieIntake;
        this.amountOfCups = amountOfCups;
        this.leftCalories = leftCalories;
        this.date = date;
    }

    public Stats() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "id=" + id +
                ", user=" + user +
                ", weight=" + weight +
                ", dailyCalorieIntake=" + dailyCalorieIntake +
                ", amountOfCups=" + amountOfCups +
                ", leftCalories=" + leftCalories +
                ", date=" + date +
                '}';
    }
}

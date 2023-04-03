package com.example.health_app.models;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

public class History implements Serializable {

    private int id;
    private User user;
    private Date date;
    private List<Meal> meals;

    public History(int id, User user, Date date, List<Meal> historyMeals) {
        this.id = id;
        this.user = user;
        this.date = date;
        this.meals = historyMeals;
    }

    public History() {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", user=" + user +
                ", date=" + date +
                ", historyMeals=" + meals +
                '}';
    }
}

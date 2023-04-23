package com.example.health_app.models;

import androidx.annotation.NonNull;

import com.example.health_app.models.type.Metric;

import java.util.ArrayList;
import java.util.List;

public class Meal {

    private int id;
    private List<History> histories = new ArrayList<>();
    private String title;
    private String info;
    private int cookingTime;
    private String creator;
    private List<Product> products;
    private float amount;
    private Metric metric;

    public Meal() {
    }

    public List<History> getHistories() {
        return histories;
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", histories=" + histories +
                ", title='" + title + '\'' +
                ", info='" + info + '\'' +
                ", cookingTime=" + cookingTime +
                ", creator='" + creator + '\'' +
                ", products=" + products +
                ", amount=" + amount +
                ", metric=" + metric +
                '}';
    }
}

package com.example.health_app.models;

import com.example.health_app.models.type.Metric;

import java.util.List;

public class Meal {

    private int id;
    private int historyId;
    private String title;
    private String info;
    private int cookingTime;
    private String creator;
    private List<Product> products;
    private float amount;
    private Metric metric;

    public Meal(int id, int historyId, String title, String info, int cookingTime, String creator, List<Product> mealProducts) {
        this.id = id;
        this.historyId = historyId;
        this.title = title;
        this.info = info;
        this.cookingTime = cookingTime;
        this.creator = creator;
        this.products = mealProducts;
    }

    public Meal() {
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
                ", historyId=" + historyId +
                ", title='" + title + '\'' +
                ", info='" + info + '\'' +
                ", cookingTime=" + cookingTime +
                ", creator='" + creator + '\'' +
                ", mealProducts=" + products +
                '}';
    }
}

package com.example.health_app.retrofit;

import com.example.health_app.models.Food;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FoodApi {

    @GET("/food/get-all")
    Call<List<Food>> getAllFood();
}

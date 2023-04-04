package com.example.health_app.retrofit;

import com.example.health_app.models.Meal;
import com.example.health_app.models.requests.MealRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MealApi {

    @POST("/meal/create")
    Call<Meal> createMeal(@Body MealRequest request);

    @PUT("/meal/update")
    Call<Meal> updateMeal(@Body MealRequest request);

    @GET("/meal/{mealId}")
    Call<Meal> getMealById(@Path("mealId") int mealId);

    @GET("/meal/get-all")
    Call<List<Meal>> getAllMeals();

    @DELETE("/meal/delete/{mealId}")
    void delete(@Path("mealId") int mealId);
}

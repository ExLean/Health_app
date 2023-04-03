package com.example.health_app.retrofit;

import com.example.health_app.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApi {

    @GET("/user/{userId}")
    Call<User> getUserById(@Path("userId") int userId);

    @GET("/user/get-all")
    Call<List<User>> getAllUsers();

    @POST("user/login")
    Call<User> getUserByLoginData(@Body User user);

    @POST("/user/create")
    Call<User> createUser(@Body User user);

    @PUT("/user/update")
    Call<User> updateUser(@Body User user);

    @DELETE("user/delete/{userId}")
    Call<User> delete(@Path("userID") int userId);
}

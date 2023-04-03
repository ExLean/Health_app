package com.example.health_app.retrofit;

import com.example.health_app.models.Stats;
import com.example.health_app.models.requests.StatsRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface StatsApi {

    @POST("/stats/create")
    Call<Stats> createStats(@Body StatsRequest request);

    @PUT("/stats/update")
    Call<Stats> updateStats(@Body StatsRequest request);

    @GET("/stats/{statsId}")
    Call<Stats> getStatsById(@Path("statsId") int statsId);

    @GET("/stats/get-all")
    Call<List<Stats>> getAllStats();

    @DELETE("/stats/delete/{statsId}")
    Call<Stats> delete(@Path("statsId") int statsId);

    @GET("/stats/current/{userId}")
    Call<Stats> getCurrentUserTodayStats(@Path("userId") int userId);

    @GET("/stats/all-current/{userId}")
    Call<List<Stats>> getAllCurrentUserStatsHistory(@Path("userId") int userId);
}

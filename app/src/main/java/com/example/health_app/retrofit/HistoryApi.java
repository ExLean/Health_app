package com.example.health_app.retrofit;

import com.example.health_app.models.History;
import com.example.health_app.models.requests.HistoryRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface HistoryApi {

    @POST("/history/create")
    Call<History> createHistory(@Body HistoryRequest request);

    @GET("/history/{historyId}")
    Call<History> getHistoryById(@Path("historyId") int historyId);

    @GET("/history/get-all")
    Call<List<History>> getAllHistory();

    @DELETE("/history/delete/{historyId}")
    Call<History> delete(@Path("historyId") int historyId);

    @GET("history/current/{userId}")
    Call<History> getCurrentUserTodayHistory(@Path("userId") int userId);
}

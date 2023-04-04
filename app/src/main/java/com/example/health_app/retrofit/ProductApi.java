package com.example.health_app.retrofit;

import com.example.health_app.models.Product;
import com.example.health_app.models.requests.ProductRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ProductApi {

    @POST("/product/create")
    Call<Product> createProduct(@Body ProductRequest request);

    @PUT("/product/update")
    Call<Product> updateProduct(@Body ProductRequest request);
}

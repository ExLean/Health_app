package com.example.health_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.health_app.models.Food;
import com.example.health_app.models.History;
import com.example.health_app.models.Meal;
import com.example.health_app.models.Product;
import com.example.health_app.models.User;
import com.example.health_app.models.requests.MealRequest;
import com.example.health_app.models.requests.ProductRequest;
import com.example.health_app.models.type.Metric;
import com.example.health_app.retrofit.FoodApi;
import com.example.health_app.retrofit.MealApi;
import com.example.health_app.retrofit.ProductApi;
import com.example.health_app.retrofit.RetrofitService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseMealActivity extends AppCompatActivity {

    User currentUser;
    History currentHistory;

    int mealId;
    List<Product> mealProducts;
    int size;

    SearchView userMealSearch;
    ListView userMeals;
    Button btnSave;

    ArrayAdapter<String> adapter;

    RetrofitService retrofitService;
    MealApi mealApi;
    ProductApi productApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_meal);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String jsonUser = extras.getString("json_user");
            currentUser = new Gson().fromJson(jsonUser, User.class);
            String jsonHistory = extras.getString("json_history");
            currentHistory = new Gson().fromJson(jsonHistory, History.class);
        }

        initialize();

        showFoodList();
    }

    private void initialize() {
        mealId = 0;
        mealProducts = new ArrayList<>();
        size = 0;

        userMealSearch = findViewById(R.id.userMealSearch);
        userMeals = findViewById(R.id.userMealList);
        btnSave = findViewById(R.id.btnSaveChosenMeal);

        retrofitService = new RetrofitService();
        mealApi = retrofitService.getRetrofit().create(MealApi.class);
        productApi = retrofitService.getRetrofit().create(ProductApi.class);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mealId != 0) {
                    mealApi.getMealById(mealId).enqueue(new Callback<Meal>() {
                        @Override
                        public void onResponse(Call<Meal> call, Response<Meal> response) {
                            if (response.code() == 200 && response.body() != null) {
                                MealRequest newMeal = new MealRequest();

                                newMeal.setHistoryId(currentHistory.getId());
                                newMeal.setTitle(response.body().getTitle());
                                newMeal.setInfo(response.body().getInfo()); // Test to check if null is ok
                                newMeal.setCookingTime(response.body().getCookingTime());
                                newMeal.setCreator(response.body().getCreator());
                                newMeal.setMealAmount(response.body().getAmount());
                                newMeal.setMealMetric(response.body().getMetric());

                                mealProducts.addAll(response.body().getProducts());

                                goAndCreateNewMeal(newMeal);
                            }
                        }
                        @Override
                        public void onFailure(Call<Meal> call, Throwable t) {
                        }
                    });
                }
            }
        });
    }

    private void showFoodList() {
        mealApi.getAllMeals().enqueue(new Callback<List<Meal>>() {
            @Override
            public void onResponse(@NonNull Call<List<Meal>> call, @NonNull Response<List<Meal>> response) {
                if (response.code() == 200 && response.body() != null) {
                    List<String> mealArr = new ArrayList<>();

                    for (Meal meal : response.body()) {
                        if (meal.getCreator().equals(currentUser.getUsername())) {
                            mealArr.add(meal.getTitle());
                        }
                    }

                    adapter = new ArrayAdapter<>(ChooseMealActivity.this, android.R.layout.simple_list_item_1, mealArr);

                    userMeals.setAdapter(adapter);
                    userMeals.setBackgroundResource(R.drawable.border);
                    userMeals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selected = mealArr.get(position);

                            for (Meal meal : response.body()) {
                                if (meal.getTitle().equals(selected)) {
                                    mealId = meal.getId();
                                    break;
                                }
                            }
                        }
                    });

                    userMealSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            if (mealArr.contains(query)) {
                                adapter.getFilter().filter(query);
                            } else {
                                Toast.makeText(ChooseMealActivity.this, "Nėra tokio patiekalo", Toast.LENGTH_LONG).show();
                            }
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            adapter.getFilter().filter(newText);
                            return false;
                        }
                    });
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Meal>> call, @NonNull Throwable t) {
                Toast.makeText(ChooseMealActivity.this,
                        "Nepavyko gauti naudotojo patiekalų",
                        Toast.LENGTH_SHORT).show();
                Logger.getLogger(ProductActivity.class.getName()).log(Level.SEVERE,
                        "Error occurred", t);
            }
        });
    }

    private void goAndCreateNewMeal(MealRequest newMeal) {
        mealApi.createMeal(newMeal).enqueue(new Callback<Meal>() {
            @Override
            public void onResponse(Call<Meal> call, Response<Meal> response) {
                if (response.code() == 200 && response.body() != null) {
                    if (!mealProducts.isEmpty()) {
                        size = mealProducts.size() + 1;
                        for (Product prod : mealProducts) {
                            --size;
                            goAndAddProductToNewMeal(response.body().getId(), prod, size);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<Meal> call, Throwable t) {
            }
        });
    }

    private void goAndAddProductToNewMeal(int newMealId, Product product, int size) {
        ProductRequest productRequest = new ProductRequest();

        productRequest.setFoodId(product.getFood().getId());
        productRequest.setMealId(newMealId);
        productRequest.setAmount(product.getAmount());
        productRequest.setMetric(product.getMetric());

        productApi.createProduct(productRequest).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.code() == 200 && response.body() != null) {
                    if (size == 1) {
                        Intent i = new Intent(ChooseMealActivity.this,
                                MenuActivity.class);
                        i.putExtra("json_user", (new Gson()).toJson(currentUser));
                        startActivity(i);
                        finish();
                    }
                }
            }
            @Override
            public void onFailure(Call<Product> call, Throwable t) {
            }
        });
    }
}

package com.example.health_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.health_app.models.Food;
import com.example.health_app.models.Meal;
import com.example.health_app.models.Product;
import com.example.health_app.models.User;
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

public class ProductActivity extends AppCompatActivity {

    private User currentUser;
    private Product currentProduct;

    private int foodId;

    private SearchView foodSearch;
    private ListView foodList;
    private EditText productAmount;
    private ToggleButton btnMetric;
    private Button btnSave;

    private ArrayAdapter<String> adapter;

    private RetrofitService retrofitService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String jsonUser = extras.getString("json_user");
            currentUser = new Gson().fromJson(jsonUser, User.class);
            String jsonProduct = extras.getString("json_product");
            currentProduct = new Gson().fromJson(jsonProduct, Product.class);
        }

        initialize();

        if (currentProduct.getId() == 0) {
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductRequest createProduct = new ProductRequest();

                    createProduct.setMealId(currentProduct.getMealId());
                    createProduct.setFoodId(foodId);
                    createProduct.setAmount(Float.parseFloat(productAmount.getText().toString()));
                    createProduct.setMetric(Metric.valueOf(btnMetric.getText().toString()));

                    if (createProduct.getFoodId() != 0 && createProduct.getMealId() != 0) {
                        goAndCreateProduct(createProduct);
                    }
                }
            });
        } else {
            initializeExistingProduct();
        }

        showFoodList();
    }

    @Override
    public void onBackPressed() {
        getUpdatedMeal();
    }

    private void initialize() {
        foodId = 0;
        foodSearch = findViewById(R.id.foodSearch);
        foodList = findViewById(R.id.foodList);
        productAmount = findViewById(R.id.inputFProductAmount);
        btnMetric = findViewById(R.id.btnMetric);
        btnSave = findViewById(R.id.btnSaveProduct);

        retrofitService = new RetrofitService();
    }

    private void initializeExistingProduct() {
        foodSearch.setQuery(currentProduct.getFood().getName(), true);
        productAmount.setText(String.valueOf(currentProduct.getAmount()));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductRequest updateProduct = new ProductRequest();

                updateProduct.setMealId(currentProduct.getMealId());
                updateProduct.setFoodId(foodId);
                updateProduct.setProductId(currentProduct.getId());
                updateProduct.setAmount(Float.parseFloat(productAmount.getText().toString()));
                updateProduct.setMetric(Metric.valueOf(btnMetric.getText().toString()));

                if (updateProduct.getFoodId() != 0) {
                    goAndUpdateProduct(updateProduct);
                } else {
                    Toast.makeText(ProductActivity.this,
                            "Produktui būtinas maisto produktas",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void goAndCreateProduct(ProductRequest request) {
        ProductApi productApi = retrofitService.getRetrofit().create(ProductApi.class);
        productApi.createProduct(request).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.code() == 200 && response.body() != null) {
                    Toast.makeText(ProductActivity.this,
                            "Produktas sukurtas",
                            Toast.LENGTH_SHORT).show();

                    getUpdatedMeal();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(ProductActivity.this,
                        "Nepavyko išsaugoti produkto",
                        Toast.LENGTH_SHORT).show();
                Logger.getLogger(ProductActivity.class.getName()).log(Level.SEVERE,
                        "Error occurred", t);
            }
        });
    }

    private void goAndUpdateProduct(ProductRequest request) {
        ProductApi productApi = retrofitService.getRetrofit().create(ProductApi.class);
        productApi.updateProduct(request).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.code() == 200 && response.body() != null) {
                    Toast.makeText(ProductActivity.this,
                            "Produktas atnaujintas",
                            Toast.LENGTH_SHORT).show();

                    getUpdatedMeal();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(ProductActivity.this,
                        "Nepavyko išsaugoti produkto",
                        Toast.LENGTH_SHORT).show();
                Logger.getLogger(ProductActivity.class.getName()).log(Level.SEVERE,
                        "Error occurred", t);
            }
        });
    }

    private void showFoodList() {
        FoodApi foodApi = retrofitService.getRetrofit().create(FoodApi.class);
        foodApi.getAllFood().enqueue(new Callback<List<Food>>() {
            @Override
            public void onResponse(Call<List<Food>> call, Response<List<Food>> response) {
                if (response.code() == 200 && response.body() != null) {
                    List<String> foodArr = new ArrayList<>();

                    for (Food food : response.body()) {
                        foodArr.add(food.getName());
                    }

                    adapter = new ArrayAdapter<String>(ProductActivity.this, android.R.layout.simple_list_item_1, foodArr);

                    foodList.setAdapter(adapter);
                    foodList.setBackgroundResource(R.drawable.border);
                    foodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String selected = foodArr.get(position);

                            for (Food food : response.body()) {
                                if (food.getName().equals(selected)) {
                                    foodId = food.getId();
                                    break;
                                }
                            }
                        }
                    });

                    foodSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            if (foodArr.contains(query)) {
                                adapter.getFilter().filter(query);
                            } else {
                                Toast.makeText(ProductActivity.this, "Nėra tokio maisto produkto", Toast.LENGTH_LONG).show();
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
            public void onFailure(Call<List<Food>> call, Throwable t) {
                Toast.makeText(ProductActivity.this,
                        "Nepavyko gauti maisto produktų",
                        Toast.LENGTH_SHORT).show();
                Logger.getLogger(ProductActivity.class.getName()).log(Level.SEVERE,
                        "Error occurred", t);
            }
        });
    }

    private void getUpdatedMeal() {
        MealApi mealApi = retrofitService.getRetrofit().create(MealApi.class);
        mealApi.getMealById(currentProduct.getMealId()).enqueue(new Callback<Meal>() {
            @Override
            public void onResponse(Call<Meal> call, Response<Meal> response) {
                if (response.code() == 200 && response.body() != null) {
                    goBackToMeal(response.body());
                }
            }

            @Override
            public void onFailure(Call<Meal> call, Throwable t) {
                Toast.makeText(ProductActivity.this,
                        "Nepavyko gauti patiekalo",
                        Toast.LENGTH_SHORT).show();
                Logger.getLogger(ProductActivity.class.getName()).log(Level.SEVERE,
                        "Error occurred", t);
            }
        });
    }

    private void goBackToMeal(Meal response) {
        Intent i = new Intent(ProductActivity.this, MealActivity.class);
        i.putExtra("json_user", (new Gson()).toJson(currentUser));
        i.putExtra("json_meal", (new Gson()).toJson(response));
        startActivity(i);
        finish();
    }
}

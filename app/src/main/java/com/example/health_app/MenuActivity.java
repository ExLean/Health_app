package com.example.health_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.health_app.models.History;
import com.example.health_app.models.Meal;
import com.example.health_app.models.Product;
import com.example.health_app.models.Stats;
import com.example.health_app.models.User;
import com.example.health_app.models.requests.MealRequest;
import com.example.health_app.models.requests.ProductRequest;
import com.example.health_app.models.requests.StatsRequest;
import com.example.health_app.retrofit.HistoryApi;
import com.example.health_app.retrofit.MealApi;
import com.example.health_app.retrofit.ProductApi;
import com.example.health_app.retrofit.RetrofitService;
import com.example.health_app.retrofit.StatsApi;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {

    User currentUser;
    Stats currentStats = new Stats();
    History currentHistory = new History();

    TextView calories;
    TextView water;
    TableLayout mealTable;
    float totalCaloriesConsumed, totalCalories;
    private boolean isValuesNan;
    private boolean wasLongPressed;
    RetrofitService retrofitService;
    ImageButton btnReload;

    StatsApi statsApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String jsonUser = extras.getString("json_user");
            currentUser = new Gson().fromJson(jsonUser, User.class);
        }

        initialize();

        water.setOnClickListener(v -> {
            if (!wasLongPressed) {
                currentStats.setAmountOfCups(currentStats.getAmountOfCups() + 1);

                int cups = currentStats.getAmountOfCups();
                String waterCups = "Vanduo - " + cups + " x 250 ml ";
                water.setText(waterCups);

                goAndUpdateStats();
            }
            wasLongPressed = false;
        });
        water.setOnLongClickListener(v -> {
            wasLongPressed = true;
            currentStats.setAmountOfCups(currentStats.getAmountOfCups() - 1);

            int cups = currentStats.getAmountOfCups();
            String waterCups = "Vanduo - " + cups + " x 250 ml ";
            water.setText(waterCups);

            goAndUpdateStats();
            return true; // ?
        });
        btnReload.setOnClickListener(v -> {
            goAndUpdateStats();

            finish();
            startActivity(getIntent());
        });

        statsApi.getCurrentUserTodayStats(currentUser.getId()).enqueue(new Callback<Stats>() {
            @Override
            public void onResponse(@NonNull Call<Stats> call, @NonNull Response<Stats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentStats = response.body();

                    totalCaloriesConsumed = currentStats.getDailyCalorieIntake();
                    totalCalories = currentStats.getDailyCalorieIntake();
                    int cups = currentStats.getAmountOfCups();

                    String waterCups = "Vanduo - " + cups + " x 250 ml ";
                    water.setText(waterCups);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Stats> call, @NonNull Throwable t) {
                Toast.makeText(MenuActivity.this,
                        "Nepavyko gauti dabartinio naudotojo siandienos statistikos",
                        Toast.LENGTH_SHORT).show();
                Logger.getLogger(MenuActivity.class.getName()).log(Level.SEVERE,
                        "Error occurred", t);
            }
        });

        HistoryApi historyApi = retrofitService.getRetrofit().create(HistoryApi.class);
        historyApi.getCurrentUserTodayHistory(currentUser.getId())
                .enqueue(new Callback<History>() {
            @Override
            public void onResponse(@NonNull Call<History> call, @NonNull Response<History> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    if (response.body().getMeals() != null) {
                        currentHistory = response.body();

                        fillMealTable();

                        String s = String.valueOf(totalCaloriesConsumed);
                        BigDecimal rounded = new BigDecimal(s).setScale(1, RoundingMode.HALF_UP);
                        String currentKcal = rounded + "/" + totalCalories + " kcal";
                        if (isValuesNan) {
                            calories.setTextColor(Color.RED);
                        }
                        calories.setText(currentKcal);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<History> call, @NonNull Throwable t) {
                Toast.makeText(MenuActivity.this,
                        "Nepavyko gauti dabartinio naudotojo siandienos istorijos",
                        Toast.LENGTH_SHORT).show();
                Logger.getLogger(MenuActivity.class.getName()).log(Level.SEVERE,
                        "Error occurred", t);
            }
        });
    }

    private void initialize() {
        isValuesNan = false;
        wasLongPressed = false;
        calories = findViewById(R.id.calories);
        water = findViewById(R.id.water);
        mealTable = findViewById(R.id.mealTable);
        btnReload = findViewById(R.id.btnReload);
        btnReload.setImageResource(R.drawable.reload_img);

        retrofitService = new RetrofitService();
        statsApi = retrofitService.getRetrofit().create(StatsApi.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.profile:
                goAndUpdateStats();
                i = new Intent(MenuActivity.this, ProfileActivity.class);
                i.putExtra("json_user", (new Gson()).toJson(currentUser));
                i.putExtra("json_stats", (new Gson()).toJson(currentStats));
                startActivity(i);
                finish();
                return true;
            case R.id.stats:
                goAndUpdateStats();
                return true;
            case R.id.calculation:
                goAndUpdateStats();
                return true;
            case R.id.log_out:
                goAndUpdateStats();
                i = new Intent(MenuActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                return true;
        }
        return false;
    }

    private void deleteMeal(int id) {
        ProductApi productApi = retrofitService.getRetrofit().create(ProductApi.class);
        productApi.getAllProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
                if (response.code() == 200 && response.body() != null) {
                    for (Product prod : response.body()) {
                        if (prod.getMealId() == id) {
                            productApi.delete(prod.getId()).enqueue(new Callback<Product>() {
                                @Override
                                public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {
                                }

                                @Override
                                public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {
                                }
                            });
                        }
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
            }
        });

        MealApi mealApi = retrofitService.getRetrofit().create(MealApi.class);
        mealApi.delete(id).enqueue(new Callback<Meal>() {
            @Override
            public void onResponse(@NonNull Call<Meal> call, @NonNull Response<Meal> response) {
            }
            @Override
            public void onFailure(@NonNull Call<Meal> call, @NonNull Throwable t) {
            }
        });
    }

    private void goAndUpdateStats() {
        StatsApi statsApi = retrofitService.getRetrofit().create(StatsApi.class);

        StatsRequest updateStats = new StatsRequest();
        updateStats.setStatsId(currentStats.getId());
        updateStats.setAmountOfCups(currentStats.getAmountOfCups());
        updateStats.setLeftCalories(totalCaloriesConsumed);

        statsApi.updateStats(updateStats).enqueue(new Callback<Stats>() {
            @Override
            public void onResponse(@NonNull Call<Stats> call, @NonNull Response<Stats> response) {

            }

            @Override
            public void onFailure(@NonNull Call<Stats> call, @NonNull Throwable t) {

            }
        });
    }

    private void fillMealTable() {
        mealTable.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mealTable.setShrinkAllColumns(true);
        mealTable.setStretchAllColumns(true);

        for (Meal meal : currentHistory.getMeals()) {
            TableRow row = new TableRow(MenuActivity.this);
            row.setBackgroundResource(R.drawable.border);

            row.addView(createAndFillTextView(meal.getTitle()));

            String amountText = meal.getAmount() + " " + meal.getMetric().toString().toLowerCase();
            row.addView(createAndFillTextView(amountText));

            float allProductsAmount = 0;
            float allProductsKcal = 0;
            for (Product product : meal.getProducts()) {
                allProductsAmount += product.getAmount();
                allProductsKcal += product.getFood().getCalories()
                        * (product.getAmount() / 100.0);
            }

            float mealKcal = (100 * allProductsKcal) / allProductsAmount;
            float mealEatenKcal = (meal.getAmount() / 100) * mealKcal;

            if (!Float.isNaN(mealEatenKcal)) {
                totalCaloriesConsumed -= mealEatenKcal;
            } else {
                isValuesNan = true;
            }

            if (!Float.isNaN(mealEatenKcal)) {
                String s = String.valueOf(mealEatenKcal);
                BigDecimal rounded = new BigDecimal(s).setScale(1, RoundingMode.HALF_UP);
                String kcalText = rounded + " kcal";
                row.addView(createAndFillTextView(kcalText));
            } else {
                TextView tv = new TextView(MenuActivity.this);
                tv.setText(R.string.zero_kcal);
                tv.setTextSize(2, 20);
                tv.setTextColor(Color.RED);
                tv.setTypeface(null, Typeface.BOLD);
                row.addView(tv);
            }

            row.setOnClickListener(v -> {
                goAndUpdateStats();

                Intent i = new Intent(MenuActivity.this,
                        MealActivity.class);
                i.putExtra("json_user", (new Gson()).toJson(currentUser));
                i.putExtra("json_meal", (new Gson()).toJson(meal));
                startActivity(i);
                finish();
            });

            row.setOnLongClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(MenuActivity.this, row);

                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().equals("Ištrinti")) {
                        deleteMeal(meal.getId());
                    } else {
                        MealApi mealApi = retrofitService.getRetrofit().create(MealApi.class);
                        mealApi.getMealById(meal.getId()).enqueue(new Callback<Meal>() {
                            @Override
                            public void onResponse(@NonNull Call<Meal> call, @NonNull Response<Meal> response) {
                                if (response.code() == 200 && response.body() != null) {
                                    MealRequest copyMeal = new MealRequest();

                                    copyMeal.setHistoryId(response.body().getHistoryId());
                                    copyMeal.setTitle(response.body().getTitle());
                                    if (response.body().getInfo() != null) {
                                        copyMeal.setInfo(response.body().getInfo());
                                    }
                                    copyMeal.setCreator(response.body().getCreator());
                                    if (response.body().getCookingTime() != 0) {
                                        copyMeal.setCookingTime(response.body().getCookingTime());
                                    }
                                    copyMeal.setMealAmount(response.body().getAmount());
                                    copyMeal.setMealMetric(response.body().getMetric());

                                    mealApi.createMeal(copyMeal).enqueue(new Callback<Meal>() {
                                        @Override
                                        public void onResponse(@NonNull Call<Meal> call, @NonNull Response<Meal> response) {
                                            if (response.code() == 200 && response.body() != null) {
                                                if (response.body().getProducts() != null) {
                                                    ProductApi productApi = retrofitService.getRetrofit().create(ProductApi.class);

                                                    for (Product prod : response.body().getProducts()) {
                                                        ProductRequest copyProduct = new ProductRequest();

                                                        copyProduct.setFoodId(prod.getFood().getId());
                                                        copyProduct.setMealId(prod.getMealId());
                                                        copyProduct.setAmount(prod.getAmount());
                                                        copyProduct.setMetric(prod.getMetric());

                                                        productApi.createProduct(copyProduct).enqueue(new Callback<Product>() {
                                                            @Override
                                                            public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {
                                                            }
                                                            @Override
                                                            public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                        @Override
                                        public void onFailure(@NonNull Call<Meal> call, @NonNull Throwable t) {
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Meal> call, @NonNull Throwable t) {
                            }
                        });
                    }
                    return true;
                });
                popupMenu.show();
                return true;
            });

            row.setLayoutParams(new ViewGroup.LayoutParams(20, 200));

            mealTable.addView(row);
        }

        TableRow rowButton = new TableRow(MenuActivity.this);

        Button btn = new Button(MenuActivity.this);

        String btnText = "Pridėti naują patiekalą";
        SpannableString spanString = new SpannableString(btnText);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        btn.setText(spanString);

        btn.setBackgroundResource(R.drawable.button_border);
        btn.setTextColor(Color.BLACK);

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT + 10,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.END;
        btn.setLayoutParams(params);

        btn.setOnClickListener(v -> {
            goAndUpdateStats();

            Meal newMeal = new Meal();
            newMeal.setHistoryId(currentHistory.getId());
            newMeal.setTitle("");

            Intent i = new Intent(MenuActivity.this,
                    MealActivity.class);
            i.putExtra("json_user", (new Gson()).toJson(currentUser));
            i.putExtra("json_meal", (new Gson()).toJson(newMeal));
            startActivity(i);
            finish();
        });

        rowButton.addView(btn);
        mealTable.addView(rowButton);

    }

    private TextView createAndFillTextView(String text) {
        TextView tv = new TextView(MenuActivity.this);
        tv.setText(text);
        tv.setTextSize(2, 20);
        tv.setTextColor(Color.parseColor("#FF000000"));
        tv.setTypeface(null, Typeface.BOLD);
        return tv;
    }
}

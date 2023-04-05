package com.example.health_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.health_app.models.History;
import com.example.health_app.models.Meal;
import com.example.health_app.models.Product;
import com.example.health_app.models.Stats;
import com.example.health_app.models.User;
import com.example.health_app.retrofit.HistoryApi;
import com.example.health_app.retrofit.MealApi;
import com.example.health_app.retrofit.RetrofitService;
import com.example.health_app.retrofit.StatsApi;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {

    User currentUser;
    TextView textView;
    TableLayout table;
    float totalCaloriesConsumed, totalCalories;
    boolean doneWithDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String jsonUser = extras.getString("json_user");
            currentUser = new Gson().fromJson(jsonUser, User.class);
        }

        textView = findViewById(R.id.calories);
        table = findViewById(R.id.mealTable);

        RetrofitService retrofitService = new RetrofitService();
        StatsApi statsApi = retrofitService.getRetrofit().create(StatsApi.class);

        statsApi.getCurrentUserTodayStats(currentUser.getId())
                .enqueue(new Callback<Stats>() {
            @Override
            public void onResponse(Call<Stats> call, Response<Stats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Stats currentStats = response.body();

                    totalCaloriesConsumed = currentStats.getDailyCalorieIntake();
                    totalCalories = currentStats.getDailyCalorieIntake();

                    String text = String.valueOf(currentStats.getDailyCalorieIntake());
                    textView.setText(text);
                }
            }

            @Override
            public void onFailure(Call<Stats> call, Throwable t) {
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
            public void onResponse(Call<History> call, Response<History> response) {
                if (response.code() == 200 && response.body().getMeals() != null) {
                    History currentHistory = response.body();

                    table.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    table.setShrinkAllColumns(true);
                    table.setStretchAllColumns(true);

                    for (Meal meal : currentHistory.getMeals()) {
                        TableRow row = new TableRow(MenuActivity.this);

                        row.addView(createAndFillTextView(meal.getTitle()));

                        String amountText = meal.getAmount() + meal.getMetric().toString().toLowerCase();
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
                        totalCaloriesConsumed -= mealEatenKcal;

                        if (!Float.isNaN(mealEatenKcal)) {
                            String s = String.valueOf(mealEatenKcal); // results in "0.525"
                            BigDecimal rounded = new BigDecimal(s).setScale(1, RoundingMode.HALF_UP);
                            String kcalText = rounded + "kcal";
                            row.addView(createAndFillTextView(kcalText));
                        } else {
                            String kcalText = mealEatenKcal + "kcal";
                            row.addView(createAndFillTextView(kcalText));
                        }

                        row.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                Intent i = new Intent(MenuActivity.this,
                                        MealActivity.class);
                                i.putExtra("json_user", (new Gson()).toJson(currentUser));
                                i.putExtra("json_meal", (new Gson()).toJson(meal));
                                startActivity(i);
                                finish();
                            }
                        });

                        row.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                PopupMenu popupMenu = new PopupMenu(MenuActivity.this, row);

                                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        Toast.makeText(MenuActivity.this, "You Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();
                                        if (item.getTitle().equals("Ištrinti")) {
                                            MealApi mealApi = retrofitService.getRetrofit().create(MealApi.class);
                                            mealApi.delete(meal.getId()).enqueue(new Callback<Meal>() {
                                                @Override
                                                public void onResponse(Call<Meal> call, Response<Meal> response) {
                                                }

                                                @Override
                                                public void onFailure(Call<Meal> call, Throwable t) {
                                                }
                                            });
                                        } else {
                                            // ToDo:
                                        }
                                        return true;
                                    }
                                });
                                popupMenu.show();
                                return true;
                            }
                        });
                        table.addView(row);
                    }

                    TableRow rowButton = new TableRow(MenuActivity.this);

                    Button btn = new Button(MenuActivity.this);
                    btn.setText("Pridėti naują patiekalą");
                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    params.gravity = Gravity.RIGHT;
                    btn.setLayoutParams(params);

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Meal newMeal = new Meal();
                            newMeal.setHistoryId(currentHistory.getId());
                            newMeal.setTitle("");

                            Intent i = new Intent(MenuActivity.this,
                                    MealActivity.class);
                            i.putExtra("json_user", (new Gson()).toJson(currentUser));
                            i.putExtra("json_meal", (new Gson()).toJson(newMeal));
                            startActivity(i);
                            finish();
                        }
                    });
                    rowButton.addView(btn);

                    table.addView(rowButton);

                    if (!Float.isNaN(totalCaloriesConsumed)) {
                        String s = String.valueOf(totalCaloriesConsumed);
                        BigDecimal rounded = new BigDecimal(s).setScale(1, RoundingMode.HALF_UP);
                        String currentKcal = rounded + "/" + totalCalories + "kcal";
                        textView.setText(currentKcal);
                    } else {
                        String currentKcal = totalCaloriesConsumed + "/" + totalCalories + "kcal";
                        textView.setText(currentKcal);
                    }
                }
            }

            @Override
            public void onFailure(Call<History> call, Throwable t) {
                Toast.makeText(MenuActivity.this,
                        "Nepavyko gauti dabartinio naudotojo siandienos istorijos",
                        Toast.LENGTH_SHORT).show();
                Logger.getLogger(MenuActivity.class.getName()).log(Level.SEVERE,
                        "Error occurred", t);
            }
        });
    }

    private TextView createAndFillTextView(String text) {
        TextView tv = new TextView(MenuActivity.this);
        tv.setText(text);
        return tv;
    }
}

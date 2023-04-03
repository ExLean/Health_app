package com.example.health_app;

import android.os.Bundle;
import android.view.ViewGroup;
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
import com.example.health_app.retrofit.RetrofitService;
import com.example.health_app.retrofit.StatsApi;
import com.google.gson.Gson;

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

                        float amountSum = 0;
                        for (Product product : meal.getProducts()) {
                            amountSum += product.getAmount();
                        }
                        String amountText = amountSum + "g";
                        row.addView(createAndFillTextView(amountText));

                        float kcalSum = 0;
                        for (Product product : meal.getProducts()) {
                            kcalSum += product.getFood().getCalories() * (product.getAmount() / 100.0);
                        }
                        totalCaloriesConsumed -= kcalSum;
                        String kcalText = kcalSum + "kcal";
                        row.addView(createAndFillTextView(String.valueOf(kcalText)));

                        table.addView(row);
                    }

                    String currentKcal = totalCaloriesConsumed + "/" + totalCalories + "kcal";
                    textView.setText(currentKcal);
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

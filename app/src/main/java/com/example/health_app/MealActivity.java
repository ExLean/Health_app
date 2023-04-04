package com.example.health_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.health_app.models.Meal;
import com.example.health_app.models.Product;
import com.example.health_app.models.User;
import com.example.health_app.models.requests.MealRequest;
import com.example.health_app.retrofit.MealApi;
import com.example.health_app.retrofit.RetrofitService;
import com.google.gson.Gson;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealActivity extends AppCompatActivity {

    User currentUser;
    Meal currentMeal;

    EditText mealTitle;
    EditText mealInfo;
    EditText mealTime;
    Button btnSave;

    TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String jsonUser = extras.getString("json_user");
            currentUser = new Gson().fromJson(jsonUser, User.class);
            String jsonMeal = extras.getString("json_meal");
            currentMeal = new Gson().fromJson(jsonMeal, Meal.class);
        }

        table = findViewById(R.id.productTable);

        mealTitle = findViewById(R.id.inputFMealTitle);
        mealTitle.setText(currentMeal.getTitle());

        mealInfo = findViewById(R.id.inputFMealInfo);
        mealInfo.setText(currentMeal.getInfo());

        mealTime = findViewById(R.id.inputFMealTime);
        mealTime.setText(String.valueOf(currentMeal.getCookingTime()));

        btnSave = findViewById(R.id.btnSaveMeal);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MealRequest updateMeal = new MealRequest();
                updateMeal.setMealId(currentMeal.getId());
                updateMeal.setTitle(mealTitle.getText().toString());
                updateMeal.setInfo(mealInfo.getText().toString());
                updateMeal.setCookingTime(Integer.parseInt(mealTime.getText().toString()));

                RetrofitService retrofitService = new RetrofitService();
                MealApi mealApi = retrofitService.getRetrofit().create(MealApi.class);

                mealApi.updateMeal(updateMeal).enqueue(new Callback<Meal>() {
                    @Override
                    public void onResponse(Call<Meal> call, Response<Meal> response) {
                        if (response.code() == 200 && response.body() != null) {
                            Toast.makeText(MealActivity.this,
                                    "Patiekalas atnaujintas",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Meal> call, Throwable t) {
                        Toast.makeText(MealActivity.this,
                                "Nepavyko išsaugoti patiekalo",
                                Toast.LENGTH_SHORT).show();
                        Logger.getLogger(MealActivity.class.getName()).log(Level.SEVERE,
                                "Error occurred", t);
                    }
                });

                Intent i = new Intent(MealActivity.this, MenuActivity.class);
                i.putExtra("json_user", (new Gson()).toJson(currentUser));
                startActivity(i);
                finish();
            }
        });

        table.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        table.setShrinkAllColumns(true);
        table.setStretchAllColumns(true);

        for (Product product : currentMeal.getProducts()) {
            TableRow row = new TableRow(MealActivity.this);

            row.addView(createAndFillTextView(product.getFood().getName()));

            String amountText = product.getAmount() + "g";
            row.addView(createAndFillTextView(amountText));

            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(MealActivity.this,
                            ProductActivity.class);
                    i.putExtra("json_user", (new Gson()).toJson(currentUser));
                    i.putExtra("json_meal", (new Gson()).toJson(currentMeal));
                    i.putExtra("json_product", (new Gson()).toJson(product));
                    startActivity(i);
                    finish();
                }
            });

            table.addView(row);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(MealActivity.this, MenuActivity.class);
        i.putExtra("json_user", (new Gson()).toJson(currentUser));
        startActivity(i);
        finish();
    }

    private TextView createAndFillTextView(String text) {
        TextView tv = new TextView(MealActivity.this);
        tv.setText(text);
        return tv;
    }
}

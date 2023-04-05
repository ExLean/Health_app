package com.example.health_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
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
import com.example.health_app.models.type.Metric;
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

    MealRequest createMeal = new MealRequest();
    MealRequest updateMeal = new MealRequest();

    EditText mealTitle;
    EditText mealInfo;
    EditText mealTime;
    EditText mealAmount;
    Button btnG;
    Button btnMl;
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
        table.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        table.setShrinkAllColumns(true);
        table.setStretchAllColumns(true);
        mealTitle = findViewById(R.id.inputFMealTitle);
        mealInfo = findViewById(R.id.inputFMealInfo);
        mealTime = findViewById(R.id.inputFMealTime);
        mealAmount = findViewById(R.id.inputFMealAmount);
        btnG = findViewById(R.id.btnMealG);
        btnMl = findViewById(R.id.btnMealMl);
        btnSave = findViewById(R.id.btnSaveMeal);

        btnG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMeal.setMetric(Metric.G);
                updateMeal.setMetric(Metric.G);
            }
        });
        btnMl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMeal.setMetric(Metric.ML);
                updateMeal.setMetric(Metric.ML);
            }
        });

        if (currentMeal.getTitle().equals("")) {
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createMeal.setHistoryId(currentMeal.getHistoryId());
                    createMeal.setTitle(mealTitle.getText().toString());
                    createMeal.setCreator(currentUser.getUsername());
                    createMeal.setAmount(Float.parseFloat(mealAmount.getText().toString()));
                    if (!mealInfo.getText().toString().equals("")) {
                        createMeal.setInfo(mealInfo.getText().toString());
                    }
                    if (!mealTime.getText().toString().equals("")) {
                        createMeal.setCookingTime(Integer.parseInt(mealTime.getText().toString()));
                    }

                    if (!createMeal.getTitle().equals("") && createMeal.getMetric() != null) {
                        goAndCreateMeal(createMeal);
                    } else {
                        Toast.makeText(MealActivity.this,
                                "Patiekalui yra būtinas pavadinimas ir pasirinktas kiekio matas",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            initializeExistingMeal();
        }

        if (currentMeal.getProducts() == null && !currentMeal.getTitle().equals("")) {
            addCreateBtn();
        } else if (currentMeal.getProducts() != null) {
            for (Product product : currentMeal.getProducts()) {
                TableRow row = new TableRow(MealActivity.this);

                row.addView(createAndFillTextView(product.getFood().getName()));

                String amountText = product.getAmount() + product.getMetric().toString().toLowerCase();
                row.addView(createAndFillTextView(amountText));

                row.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        Toast.makeText(MealActivity.this,
                                currentMeal.getAmount() + " - labasss",
                                Toast.LENGTH_LONG).show();

                        Intent i = new Intent(MealActivity.this,
                                ProductActivity.class);
                        i.putExtra("json_user", (new Gson()).toJson(currentUser));
                        i.putExtra("json_product", (new Gson()).toJson(product));
                        i.putExtra("float_amount", currentMeal.getAmount());
                        startActivity(i);
                        finish();
                    }
                });
                table.addView(row);
            }
            addCreateBtn();
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

    private void initializeExistingMeal() {
        mealTitle.setText(currentMeal.getTitle());
        mealInfo.setText(currentMeal.getInfo());
        mealTime.setText(String.valueOf(currentMeal.getCookingTime()));
        mealAmount.setText(String.valueOf(currentMeal.getAmount()));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMeal.getProducts() != null) {
                    float allProdAmount = 0;
                    for (Product prod : currentMeal.getProducts()) {
                        allProdAmount += prod.getAmount();
                    }
                    if (Float.parseFloat(mealAmount.getText().toString()) <= allProdAmount) {
                        tryToUpdateMeal();
                    } else {
                        Toast.makeText(MealActivity.this,
                                "Patiekalo svoris negali viršyti produktų svorio: " + allProdAmount + "g",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    tryToUpdateMeal();
                }
            }
        });
    }

    private void tryToUpdateMeal() {
        updateMeal.setMealId(currentMeal.getId());
        updateMeal.setTitle(mealTitle.getText().toString());
        updateMeal.setInfo(mealInfo.getText().toString());
        updateMeal.setCookingTime(Integer.parseInt(mealTime.getText().toString()));
        updateMeal.setAmount(Float.parseFloat(mealAmount.getText().toString()));

        if (updateMeal.getMetric() != null) {
            goAndUpdateMeal(updateMeal);
        } else {
            Toast.makeText(MealActivity.this,
                    "Patiekalui yra būtinas kiekio matas",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void goAndCreateMeal(MealRequest request) {
        RetrofitService retrofitService = new RetrofitService();
        MealApi mealApi = retrofitService.getRetrofit().create(MealApi.class);

        mealApi.createMeal(request).enqueue(new Callback<Meal>() {
            @Override
            public void onResponse(Call<Meal> call, Response<Meal> response) {
                if (response.code() == 200 && response.body() != null) {
                    Toast.makeText(MealActivity.this,
                            "Patiekalas sukurtas",
                            Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(MealActivity.this, MenuActivity.class);
                    i.putExtra("json_user", (new Gson()).toJson(currentUser));
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Meal> call, Throwable t) {
                Toast.makeText(MealActivity.this,
                        "Nepavyko sukurti patiekalo",
                        Toast.LENGTH_SHORT).show();
                Logger.getLogger(MealActivity.class.getName()).log(Level.SEVERE,
                        "Error occurred", t);
            }
        });
    }

    private void goAndUpdateMeal(MealRequest request) {
        RetrofitService retrofitService = new RetrofitService();
        MealApi mealApi = retrofitService.getRetrofit().create(MealApi.class);

        mealApi.updateMeal(request).enqueue(new Callback<Meal>() {
            @Override
            public void onResponse(Call<Meal> call, Response<Meal> response) {
                if (response.code() == 200 && response.body() != null) {
                    Toast.makeText(MealActivity.this,
                            "Patiekalas atnaujintas",
                            Toast.LENGTH_SHORT).show();

                    goBackToMenu();
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
    }

    private void addCreateBtn() {
        TableRow rowButton = new TableRow(MealActivity.this);

        Button btn = new Button(MealActivity.this);
        btn.setText("Pridėti naują produktą");
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER;
        btn.setLayoutParams(params);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product newProduct = new Product();
                newProduct.setMealId(currentMeal.getId());

                Intent i = new Intent(MealActivity.this,
                        ProductActivity.class);
                i.putExtra("json_user", (new Gson()).toJson(currentUser));
                i.putExtra("json_product", (new Gson()).toJson(newProduct));
                i.putExtra("float_amount", currentMeal.getAmount());
                startActivity(i);
                finish();
            }
        });

        rowButton.addView(btn);
        table.addView(rowButton);
    }

    private TextView createAndFillTextView(String text) {
        TextView tv = new TextView(MealActivity.this);
        tv.setText(text);
        return tv;
    }

    private void goBackToMenu() {
        Intent i = new Intent(MealActivity.this, MenuActivity.class);
        i.putExtra("json_user", (new Gson()).toJson(currentUser));
        startActivity(i);
        finish();
    }
}

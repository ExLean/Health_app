package com.example.health_app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.health_app.models.Meal;
import com.example.health_app.models.Product;
import com.example.health_app.models.User;
import com.example.health_app.models.requests.MealRequest;
import com.example.health_app.models.type.Metric;
import com.example.health_app.retrofit.MealApi;
import com.example.health_app.retrofit.ProductApi;
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
    EditText mealAmount;
    ToggleButton btnMetric;
    Button btnSave;

    TableLayout productTable;

    RetrofitService retrofitService;

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

        initialize();

        if (currentMeal.getTitle().equals("")) {
            btnSave.setOnClickListener(v -> {
                MealRequest createMeal = new MealRequest();

                createMeal.setHistoryId(currentMeal.getHistoryId());
                createMeal.setTitle(mealTitle.getText().toString());
                createMeal.setCreator(currentUser.getUsername());
                createMeal.setMealAmount(Float.parseFloat(mealAmount.getText().toString()));
                createMeal.setMealMetric(Metric.valueOf(btnMetric.getText().toString()));
                createMeal.setInfo(mealInfo.getText().toString());

                if (mealTime.getText().toString().equals("")) {
                    createMeal.setCookingTime(0);
                }

                if (!createMeal.getTitle().equals("") && createMeal.getMealAmount() > 0) {
                    goAndCreateMeal(createMeal);
                } else {
                    Toast.makeText(MealActivity.this,
                            "Patiekalui yra būtinas pavadinimas ir kiekis",
                            Toast.LENGTH_LONG).show();
                }
            });
        } else {
            initializeExistingMeal();
        }

        if (currentMeal.getProducts() == null && !currentMeal.getTitle().equals("")) {
            addCreateBtn();
        } else if (currentMeal.getProducts() != null) {
            fillProductTable();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBackToMenu();
    }

    private void initialize() {
        productTable = findViewById(R.id.productTable);
        productTable.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        productTable.setShrinkAllColumns(true);
        productTable.setStretchAllColumns(true);
        mealTitle = findViewById(R.id.inputFMealTitle);
        mealInfo = findViewById(R.id.inputFMealInfo);
        mealTime = findViewById(R.id.inputFMealTime);
        mealAmount = findViewById(R.id.inputFMealAmount);
        btnMetric = findViewById(R.id.btnMetric);
        btnSave = findViewById(R.id.btnSaveMeal);

        retrofitService = new RetrofitService();
    }

    private void initializeExistingMeal() {
        mealTitle.setText(currentMeal.getTitle());
        mealInfo.setText(currentMeal.getInfo());
        mealTime.setText(String.valueOf(currentMeal.getCookingTime()));
        mealAmount.setText(String.valueOf(currentMeal.getAmount()));
        btnMetric.setChecked(currentMeal.getMetric().toString().equals("G"));

        btnSave.setOnClickListener(v -> {
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
        });
    }

    private void tryToUpdateMeal() {
        MealRequest updateMeal = new MealRequest();

        updateMeal.setMealId(currentMeal.getId());
        updateMeal.setTitle(mealTitle.getText().toString());
        updateMeal.setInfo(mealInfo.getText().toString());
        updateMeal.setCookingTime(Integer.parseInt(mealTime.getText().toString()));
        updateMeal.setMealAmount(Float.parseFloat(mealAmount.getText().toString()));
        updateMeal.setMealMetric(Metric.valueOf(btnMetric.getText().toString()));

        goAndUpdateMeal(updateMeal);
    }

    private void goAndCreateMeal(MealRequest request) {
        MealApi mealApi = retrofitService.getRetrofit().create(MealApi.class);

        mealApi.createMeal(request).enqueue(new Callback<Meal>() {
            @Override
            public void onResponse(@NonNull Call<Meal> call, @NonNull Response<Meal> response) {
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
            public void onFailure(@NonNull Call<Meal> call, @NonNull Throwable t) {
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
            public void onResponse(@NonNull Call<Meal> call, @NonNull Response<Meal> response) {
                if (response.code() == 200 && response.body() != null) {
                    Toast.makeText(MealActivity.this,
                            "Patiekalas atnaujintas",
                            Toast.LENGTH_SHORT).show();

                    goBackToMenu();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Meal> call, @NonNull Throwable t) {
                Toast.makeText(MealActivity.this,
                        "Nepavyko išsaugoti patiekalo",
                        Toast.LENGTH_SHORT).show();
                Logger.getLogger(MealActivity.class.getName()).log(Level.SEVERE,
                        "Error occurred", t);
            }
        });
    }

    private void fillProductTable() {
        for (Product product : currentMeal.getProducts()) {
            TableRow row = new TableRow(MealActivity.this);
            row.setBackgroundResource(R.drawable.border);

            row.addView(createAndFillTextView(product.getFood().getName()));

            String amountText = product.getAmount() + product.getMetric().toString().toLowerCase();
            row.addView(createAndFillTextView(amountText));

            row.setOnClickListener(v -> {
                Intent i = new Intent(MealActivity.this,
                        ProductActivity.class);
                i.putExtra("json_user", (new Gson()).toJson(currentUser));
                i.putExtra("json_product", (new Gson()).toJson(product));
                i.putExtra("float_amount", currentMeal.getAmount());
                startActivity(i);
                finish();
            });

            row.setOnLongClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(MealActivity.this, row);

                popupMenu.getMenuInflater().inflate(R.menu.popup_single_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().equals("Ištrinti")) { //ToDo: need to create refresh button
                        deleteProduct(product.getId());
                    }
                    return true;
                });
                popupMenu.show();
                return true;
            });

            productTable.addView(row);
        }
        addCreateBtn();
    }

    private void addCreateBtn() {
        TableRow rowButton = new TableRow(MealActivity.this);

        Button btn = new Button(MealActivity.this);

        String btnText = "Pridėti naują produktą";
        SpannableString spanString = new SpannableString(btnText);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
        btn.setText(spanString);

        btn.setBackgroundResource(R.drawable.button_border);
        btn.setTextColor(Color.BLACK);

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.END;
        btn.setLayoutParams(params);

        btn.setOnClickListener(v -> {
            Product newProduct = new Product();
            newProduct.setMealId(currentMeal.getId());

            Intent i = new Intent(MealActivity.this,
                    ProductActivity.class);
            i.putExtra("json_user", (new Gson()).toJson(currentUser));
            i.putExtra("json_product", (new Gson()).toJson(newProduct));
            i.putExtra("float_amount", currentMeal.getAmount());
            startActivity(i);
            finish();
        });

        rowButton.addView(btn);
        productTable.addView(rowButton);
    }

    private TextView createAndFillTextView(String text) {
        TextView tv = new TextView(MealActivity.this);
        tv.setText(text);
        tv.setTextSize(2, 20);
        tv.setTextColor(Color.parseColor("#FF000000"));
        tv.setTypeface(null, Typeface.BOLD);
        return tv;
    }

    private void deleteProduct(int productId) {
        ProductApi productApi = retrofitService.getRetrofit().create(ProductApi.class);
        productApi.delete(productId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {

            }

            @Override
            public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {

            }
        });
    }

    private void goBackToMenu() {
        Intent i = new Intent(MealActivity.this, MenuActivity.class);
        i.putExtra("json_user", (new Gson()).toJson(currentUser));
        startActivity(i);
        finish();
    }
}

package com.example.health_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.health_app.models.Stats;
import com.example.health_app.models.User;
import com.example.health_app.models.requests.StatsRequest;
import com.example.health_app.retrofit.RetrofitService;
import com.example.health_app.retrofit.StatsApi;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.Period;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalculatorActivity extends AppCompatActivity {

    User currentUser;
    Stats currentStats;

    EditText userWeight;
    EditText userHeight;
    EditText userAge;
    EditText userDailyCalorieIntake;
    ToggleButton btnSex;
    Button btnCalculate;
    Button btnSaveKcal;

    RetrofitService retrofitService;
    StatsApi statsApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String jsonUser = extras.getString("json_user");
            currentUser = new Gson().fromJson(jsonUser, User.class);
            String jsonStats = extras.getString("json_stats");
            currentStats = new Gson().fromJson(jsonStats, Stats.class);
        }

        initialize();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(CalculatorActivity.this, MenuActivity.class);
        i.putExtra("json_user", (new Gson()).toJson(currentUser));
        startActivity(i);
        finish();
    }

    private void initialize() {
        userWeight = findViewById(R.id.userWeight);
        userHeight = findViewById(R.id.userHeight);
        userAge = findViewById(R.id.userAge);
        userDailyCalorieIntake = findViewById(R.id.userDailyCalorieIntake);
        btnSex = findViewById(R.id.btnSex);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnSaveKcal = findViewById(R.id.btnSaveKcal);

        retrofitService = new RetrofitService();
        statsApi = retrofitService.getRetrofit().create(StatsApi.class);

        userWeight.setText(String.valueOf(currentStats.getWeight()));
        userHeight.setText(String.valueOf(currentUser.getHeight()));
        // ToDo: get age from Date
        userAge.setText(String.valueOf(calculateAge(currentUser.getBirthday())));
        userDailyCalorieIntake.setText(String.valueOf(currentStats.getDailyCalorieIntake()));

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnSex.getText().toString().equals("Vyras")) {
                    userDailyCalorieIntake.setText(String.valueOf(
                            10 * Float.parseFloat(userWeight.getText().toString())
                                    + 6.25 * Float.parseFloat(userHeight.getText().toString())
                                    - 5 * Integer.parseInt(userAge.getText().toString()) + 5));
                } else {
                    userDailyCalorieIntake.setText(String.valueOf(
                            10 * Float.parseFloat(userWeight.getText().toString())
                                    + 6.25 * Float.parseFloat(userHeight.getText().toString())
                                    - 5 * Integer.parseInt(userAge.getText().toString()) - 161));
                }
            }
        });

        btnSaveKcal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatsRequest updateStats = new StatsRequest();

                updateStats.setStatsId(currentStats.getId());
                updateStats.setDailyCalorieIntake(Float.parseFloat(userDailyCalorieIntake.getText().toString()));

                statsApi.updateStats(updateStats).enqueue(new Callback<Stats>() {
                    @Override
                    public void onResponse(Call<Stats> call, Response<Stats> response) {
                        if (response.code() == 200 && response.body() != null) {
                            Toast.makeText(CalculatorActivity.this,
                                    "Jūsų pasirinkimas buvo išsaugotas",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Stats> call, Throwable t) {
                    }
                });
            }
        });
    }

    public int calculateAge(String birthDate) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate dob = LocalDate.parse(birthDate);
            LocalDate curDate = LocalDate.now();
            if ((dob != null) && (curDate != null)) {
                return Period.between(dob, curDate).getYears();
            }
        }
        return 0;
    }
}

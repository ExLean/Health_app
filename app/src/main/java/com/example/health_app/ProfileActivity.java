package com.example.health_app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.health_app.models.Stats;
import com.example.health_app.models.User;
import com.example.health_app.models.requests.StatsRequest;
import com.example.health_app.retrofit.RetrofitService;
import com.example.health_app.retrofit.StatsApi;
import com.example.health_app.retrofit.UserApi;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// ToDo: Pakeicia user bet neparodo atsinaujinant, dar neziurejau del stats, bet ciuju veikia

public class ProfileActivity extends AppCompatActivity {

    private User currentUser;
    private Stats currentStats;

    private TextView nameView;
    private TextView emailView;
    private TextView birthdayView;
    private TextView heightView;
    private TextView weightView;
    private Button editUser;

//    private EditText nameEdit;
//    private EditText emailEdit;
    private EditText birthdayEdit;
    private EditText heightEdit;
    private EditText weightEdit;
    private Button saveUser;

    private Calendar myCalendar;
    private RetrofitService retrofitService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String jsonUser = extras.getString("json_user");
            currentUser = new Gson().fromJson(jsonUser, User.class);
            String jsonStats = extras.getString("json_stats");
            currentStats = new Gson().fromJson(jsonStats, Stats.class);
        }

        if (currentUser.getUsername() != null) {
            initialize();
        } else {
            retrofitService = new RetrofitService();

            UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);
            userApi.getUserById(currentUser.getId()).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.code() == 200 && response.body() != null) {
                        currentUser = response.body();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });

            StatsApi statsApi = retrofitService.getRetrofit().create(StatsApi.class);
            statsApi.getStatsById(currentStats.getId()).enqueue(new Callback<Stats>() {
                @Override
                public void onResponse(Call<Stats> call, Response<Stats> response) {
                    if (response.code() == 200 && response.body() != null) {
                        currentStats = response.body();

                        initialize();
                    }
                }

                @Override
                public void onFailure(Call<Stats> call, Throwable t) {

                }
            });
        }
    }

    private void initialize() {
        retrofitService = new RetrofitService();
        myCalendar = Calendar.getInstance();

        nameView = findViewById(R.id.username);
        emailView = findViewById(R.id.email);
        birthdayView = findViewById(R.id.birthday);
        heightView = findViewById(R.id.height);
        weightView = findViewById(R.id.weight);
        editUser = findViewById(R.id.update_user);

//        nameEdit = findViewById(R.id.edit_username);
//        emailEdit = findViewById(R.id.edit_email);
        birthdayEdit = findViewById(R.id.edit_birthday);
        heightEdit = findViewById(R.id.edit_height);
        weightEdit = findViewById(R.id.edit_weight);
        saveUser = findViewById(R.id.save_user);

        String text;
        text = "Slapyvardis: " + currentUser.getUsername();
        nameView.setText(text);
        text = "El. paštas: " + currentUser.getEmail();
        emailView.setText(text);
        text = "Gimimo data: " + currentUser.getBirthday();
        birthdayView.setText(text);
        text = "Ūgis: " + String.valueOf(currentUser.getHeight());
        heightView.setText(text);
        text = "Svoris: " + String.valueOf(currentStats.getWeight());
        weightView.setText(text);

//        nameEdit.setVisibility(View.INVISIBLE);
//        emailEdit.setVisibility(View.INVISIBLE);
        birthdayEdit.setVisibility(View.INVISIBLE);
        heightEdit.setVisibility(View.INVISIBLE);
        weightEdit.setVisibility(View.INVISIBLE);
        saveUser.setVisibility(View.INVISIBLE);

        editUser.setOnClickListener(v -> {
            nameView.setVisibility(View.INVISIBLE);
            emailView.setVisibility(View.INVISIBLE);
            birthdayView.setVisibility(View.INVISIBLE);
            heightView.setVisibility(View.INVISIBLE);
            weightView.setVisibility(View.INVISIBLE);
            editUser.setVisibility(View.INVISIBLE);

//            nameEdit.setVisibility(View.VISIBLE);
//            emailEdit.setVisibility(View.VISIBLE);
            birthdayEdit.setVisibility(View.VISIBLE);
            heightEdit.setVisibility(View.VISIBLE);
            weightEdit.setVisibility(View.VISIBLE);
            saveUser.setVisibility(View.VISIBLE);

//            nameEdit.setText(currentUser.getUsername());
//            emailEdit.setText(currentUser.getEmail());
            birthdayEdit.setText(currentUser.getBirthday());
            heightEdit.setText(String.valueOf(currentUser.getHeight()));
            weightEdit.setText(String.valueOf(currentStats.getWeight()));
        });

        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateLabel();
        };
        birthdayEdit.setOnClickListener(view ->
                new DatePickerDialog(ProfileActivity.this, date,
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());


        saveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User updateUser = new User();
                StatsRequest updateStats = new StatsRequest();

                updateUser.setId(currentUser.getId());
//                updateUser.setUsername(nameEdit.getText().toString());
//                updateUser.setEmail(emailEdit.getText().toString());
                updateUser.setBirthday(String.valueOf(birthdayEdit.getText()));
                updateUser.setHeight(Float.parseFloat(heightEdit.getText().toString()));

                updateStats.setStatsId(currentStats.getId());
                updateStats.setWeight(Float.parseFloat(weightEdit.getText().toString()));

                UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);
                userApi.updateUser(updateUser).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });

                StatsApi statsApi = retrofitService.getRetrofit().create(StatsApi.class);
                statsApi.updateStats(updateStats).enqueue(new Callback<Stats>() {
                    @Override
                    public void onResponse(Call<Stats> call, Response<Stats> response) {

                    }

                    @Override
                    public void onFailure(Call<Stats> call, Throwable t) {

                    }
                });

                User userId = new User();
                Stats statsId = new Stats();
                userId.setId(currentUser.getId());
                statsId.setId(currentStats.getId());

                Intent i = new Intent(ProfileActivity.this,
                        ProfileActivity.class);
                i.putExtra("json_user", (new Gson()).toJson(userId));
                i.putExtra("json_stats", (new Gson()).toJson(statsId));
                startActivity(i);
                finish();
            }
        });
    }

    private void updateLabel(){
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.getDefault());
        birthdayEdit.setText(dateFormat.format(myCalendar.getTime()));
    }
}

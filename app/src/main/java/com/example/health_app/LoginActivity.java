package com.example.health_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.health_app.models.User;
import com.example.health_app.retrofit.RetrofitService;
import com.example.health_app.retrofit.UserApi;
import com.google.gson.Gson;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText username_field = findViewById(R.id.username_login_textField);
        EditText password_field = findViewById(R.id.password_login_textField);
        Button login = findViewById(R.id.login_btn);
        Button goToRegister = findViewById(R.id.goToRegister_btn);

        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);

        login.setOnClickListener(view -> {
            String username = username_field.getText().toString();
            String password = password_field.getText().toString();

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);

            userApi.getUserByLoginData(user)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                            Toast.makeText(LoginActivity.this, "Prisijungta",
                                    Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(LoginActivity.this,
                                    MenuActivity.class);
                            i.putExtra("json_user", (new Gson()).toJson(response.body()));
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                            Toast.makeText(LoginActivity.this,
                                    "Prisijungti nepavyko", Toast.LENGTH_SHORT).show();
                            Logger.getLogger(LoginActivity.class.getName()).log(Level.SEVERE,
                                    "Error occurred trying to login", t);
                        }
                    });
        });

        goToRegister.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Išeiti iš programėlės?")
                .setMessage("Ar jūs tikrai norite išeiti iš programėlės?")
                .setNegativeButton("atšaukti", null)
                .setPositiveButton("Taip", (arg0, arg1) -> LoginActivity.super.onBackPressed()).create().show();
    }
}
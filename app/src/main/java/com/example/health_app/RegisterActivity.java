package com.example.health_app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.health_app.models.User;
import com.example.health_app.retrofit.RetrofitService;
import com.example.health_app.retrofit.UserApi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    final Calendar myCalendar = Calendar.getInstance();
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeComponents();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    private void initializeComponents() {
        TextInputEditText usr_text = findViewById(R.id.username_register_textField);
        TextInputEditText pss_text = findViewById(R.id.password_register_textField);
        EditText eml_text = findViewById(R.id.email_register_textField);

        editText = (EditText) findViewById(R.id.Birthday);
        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateLabel();
        };
        editText.setOnClickListener(view -> new DatePickerDialog(RegisterActivity.this, date,
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        TextInputEditText hgh_text = findViewById(R.id.height_register_textField);
        MaterialButton register = findViewById(R.id.register_btn);

        RetrofitService retrofitService = new RetrofitService();
        UserApi userApi = retrofitService.getRetrofit().create(UserApi.class);

        register.setOnClickListener(view -> {
            User user = new User();
            user.setUsername(String.valueOf(usr_text.getText()));
            user.setPassword(String.valueOf(pss_text.getText()));
            user.setEmail(String.valueOf(eml_text.getText()));
            user.setBirthday(String.valueOf(editText.getText()));
            user.setHeight(Float.parseFloat(String.valueOf(hgh_text.getText())));

            userApi.createUser(user)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                            if (response.code() == 200 && response.body() != null) {
                                Toast.makeText(RegisterActivity.this, "Naujas naudotojas užregistruotas", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                            Toast.makeText(RegisterActivity.this, "Registracija nepavyko", Toast.LENGTH_SHORT).show();
                            Logger.getLogger(RegisterActivity.class.getName()).log(Level.SEVERE, "Error occurred", t);
                        }
                    });
        });
    }

    private void updateLabel(){
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.getDefault());
        editText.setText(dateFormat.format(myCalendar.getTime()));
    }

}

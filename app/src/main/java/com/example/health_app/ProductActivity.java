package com.example.health_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.health_app.models.Meal;
import com.example.health_app.models.Product;
import com.example.health_app.models.User;
import com.google.gson.Gson;

public class ProductActivity extends AppCompatActivity {

    User currentUser;
    Product currentProduct;

    SearchView foodSearch;
    ListView foodList;
    EditText productAmount;
    Button btnG;
    Button btnMl;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String jsonUser = extras.getString("json_user");
            currentUser = new Gson().fromJson(jsonUser, User.class);
            String jsonProduct = extras.getString("json_product");
            currentProduct = new Gson().fromJson(jsonProduct, Product.class);
        }

        // ToDo: add exist product text
        foodSearch = findViewById(R.id.foodSearch);
//        foodSearch.setQuery();

        foodList = findViewById(R.id.foodList);

        productAmount = findViewById(R.id.inputFProductAmount);
        productAmount.setText(String.valueOf(currentProduct.getAmount());

        btnG = findViewById(R.id.btnG);
        btnMl = findViewById(R.id.btnMl);
//        if (currentProduct.getMetric().toString().equals("G")) {
//            btnG.setClipToOutline(true);
//            btnMl.setClipToOutline(false);
//        } else {
//            btnG.setClipToOutline(false);
//            btnMl.setClipToOutline(true);
//        }

        btnSave = findViewById(R.id.btnSaveProduct);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ToDo: save product
            }
        });

        // https://www.javatpoint.com/android-searchview
    }
}

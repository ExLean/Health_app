package com.example.health_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.health_app.models.Stats;
import com.example.health_app.models.User;
import com.example.health_app.retrofit.ProductApi;
import com.example.health_app.retrofit.RetrofitService;
import com.example.health_app.retrofit.StatsApi;
import com.google.gson.Gson;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatsActivity extends AppCompatActivity {

    User currentUser;
    Stats currentStats;

    GraphView graphView;
    Spinner spinner;
    String[] values;

    List<java.sql.Date> allDates;
    List<Double> allValues;

    RetrofitService retrofitService;
    StatsApi statsApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String jsonUser = extras.getString("json_user");
            currentUser = new Gson().fromJson(jsonUser, User.class);
            String jsonStats = extras.getString("json_stats");
            currentStats = new Gson().fromJson(jsonStats, Stats.class);
        }

        initialize();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                createGraph(values[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(StatsActivity.this, MenuActivity.class);
        i.putExtra("json_user", (new Gson()).toJson(currentUser));
        startActivity(i);
        finish();
    }

    private void initialize() {
        graphView = findViewById(R.id.graphView);
        spinner = findViewById(R.id.spinner);

        retrofitService = new RetrofitService();
        statsApi = retrofitService.getRetrofit().create(StatsApi.class);

        values = new String[] { "Svoris", "Likusios kalorijos", "Maistinės medžiagos" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void createGraph(String graphName) {
        statsApi.getAllCurrentUserStatsHistory(currentUser.getId()).enqueue(new Callback<List<Stats>>() {
            @Override
            public void onResponse(Call<List<Stats>> call, Response<List<Stats>> response) {
                if (response.code() == 200 && response.body() != null) {

                    if (graphName.equals("Svoris")) {
                        LineGraphSeries<DataPoint> lineGraphSeries = new LineGraphSeries<>();
                        for (Stats stat : response.body()) {
                            lineGraphSeries.appendData(new DataPoint(stat.getDate(), stat.getWeight()), true, response.body().size(), true);
                        }

                        lineGraphSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                            @Override
                            public void onTap(Series series, DataPointInterface dataPoint) {
                                Toast.makeText(StatsActivity.this,
                                        "Svoris: " + dataPoint.getY() + "kg",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                        lineGraphSeries.setDrawDataPoints(true);
                        lineGraphSeries.setDataPointsRadius(15f);
                        lineGraphSeries.setAnimated(true);

                        graphView.removeAllSeries();
                        graphView.addSeries(lineGraphSeries);

                        graphView.getGridLabelRenderer().setVerticalAxisTitle("Svoris");
                    } else if (graphName.equals("Likusios kalorijos")) {
                        LineGraphSeries<DataPoint> lineGraphSeries = new LineGraphSeries<>();
                        for (Stats stat : response.body()) {
                            lineGraphSeries.appendData(new DataPoint(stat.getDate(), stat.getLeftCalories()), true, response.body().size(), true);
                        }
                        lineGraphSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                            @Override
                            public void onTap(Series series, DataPointInterface dataPoint) {
                                Toast.makeText(StatsActivity.this,
                                        "Likusios kcal: " + dataPoint.getY(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        lineGraphSeries.setDrawDataPoints(true);
                        lineGraphSeries.setDataPointsRadius(15f);
                        lineGraphSeries.setAnimated(true);

                        graphView.removeAllSeries();
                        graphView.addSeries(lineGraphSeries);
                        graphView.getGridLabelRenderer().setVerticalAxisTitle("Kalorijos");
                    } else if (graphName.equals("Maistinės medžiagos")){
                        BarGraphSeries<DataPoint> barGraphSeriesCarbs = new BarGraphSeries<>();
//                        BarGraphSeries<DataPoint> barGraphSeriesFat = new BarGraphSeries<>();
//                        BarGraphSeries<DataPoint> barGraphSeriesProtein = new BarGraphSeries<>();
//                        for (Stats stat : response.body()) {
                            barGraphSeriesCarbs.appendData(new DataPoint(response.body().get(1).getDate(), 10), true, response.body().size(), true);
//                            barGraphSeriesFat.appendData(new DataPoint(stat.getDate(), stat.getFatAmount()), true, response.body().size(), true);
//                            barGraphSeriesProtein.appendData(new DataPoint(stat.getDate(), stat.getProteinAmount()), true, response.body().size(), true);
//                        }
//
                        barGraphSeriesCarbs.setSpacing(50);
                        barGraphSeriesCarbs.setDrawValuesOnTop(true);
                        barGraphSeriesCarbs.setValuesOnTopColor(Color.BLACK);

                        barGraphSeriesCarbs.setAnimated(true);
//                        barGraphSeriesFat.setAnimated(true);
//                        barGraphSeriesProtein.setAnimated(true);
                        barGraphSeriesCarbs.setColor(Color.GREEN);
//                        barGraphSeriesFat.setColor(Color.YELLOW);
//                        barGraphSeriesProtein.setColor(Color.WHITE);

                        graphView.removeAllSeries();
                        graphView.addSeries(barGraphSeriesCarbs);
//                        graphView.addSeries(barGraphSeriesFat);
//                        graphView.addSeries(barGraphSeriesProtein);
                        graphView.getGridLabelRenderer().setVerticalAxisTitle("Gramai");
                    }

                    graphView.getGridLabelRenderer().setHorizontalAxisTitle("Data");
                    graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(StatsActivity.this, new SimpleDateFormat("dd/MM")));
                    graphView.getGridLabelRenderer().setHorizontalLabelsAngle(45);
                    graphView.getGridLabelRenderer().setNumHorizontalLabels(response.body().size());
                    graphView.getGridLabelRenderer().setLabelsSpace(25);

                    graphView.getViewport().setScalable(true);
                    graphView.getViewport().setScrollable(true);
                    graphView.getViewport().setXAxisBoundsManual(false);
                }
            }
            @Override
            public void onFailure(Call<List<Stats>> call, Throwable t) {
            }
        });
    }
}

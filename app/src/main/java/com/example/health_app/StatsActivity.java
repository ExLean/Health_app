package com.example.health_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.health_app.models.Stats;
import com.example.health_app.models.User;
import com.example.health_app.retrofit.RetrofitService;
import com.example.health_app.retrofit.StatsApi;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatsActivity extends AppCompatActivity {

    User currentUser;
    Stats currentStats;

    GraphView graphView;
    BarChart barChart;
    PieChart pieChart;
    Spinner graphSpinner;
    DatePicker datePicker;

    String[] values;

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
        barChart = findViewById(R.id.bar_chart);
        graphSpinner = findViewById(R.id.graph_spinner);
        datePicker = findViewById(R.id.date_picker);
        pieChart = findViewById(R.id.pie_chart);

        retrofitService = new RetrofitService();
        statsApi = retrofitService.getRetrofit().create(StatsApi.class);

        values = new String[] { "Svoris", "Likusios kalorijos", "Maistinės medžiagos" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        graphSpinner.setAdapter(adapter);

        graphSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                createGraph(values[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        datePicker.setMaxDate(System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    createGraph("Maistinės medžiagos");
                }
            });
        }
    }

    private void createGraph(String graphName) {
        statsApi.getAllCurrentUserStatsHistory(currentUser.getId()).enqueue(new Callback<List<Stats>>() {
            @Override
            public void onResponse(Call<List<Stats>> call, Response<List<Stats>> response) {
                if (response.code() == 200 && response.body() != null) {

                    if (graphName.equals("Svoris")) {
                        graphView.setVisibility(View.VISIBLE);
                        pieChart.setVisibility(View.GONE);
                        datePicker.setVisibility(View.INVISIBLE);

                        Calendar cal = Calendar.getInstance();

                        LineGraphSeries<DataPoint> lineGraphSeries = new LineGraphSeries<>();
                        for (Stats stat : response.body()) {
                            cal.setTime(stat.getDate());
                            cal.set(Calendar.HOUR_OF_DAY, 0);
                            cal.set(Calendar.MINUTE, 0);
                            cal.set(Calendar.SECOND, 0);
                            cal.set(Calendar.MILLISECOND, 0);

                            lineGraphSeries.appendData(new DataPoint(cal.getTime(), stat.getWeight()), true, response.body().size(), true);
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
                        graphView.setVisibility(View.VISIBLE);
                        pieChart.setVisibility(View.GONE);
                        datePicker.setVisibility(View.INVISIBLE);

                        LineGraphSeries<DataPoint> lineGraphSeries = new LineGraphSeries<>();
                        LineGraphSeries<DataPoint> dailyCalorieIntake = new LineGraphSeries<>();
                        for (Stats stat : response.body()) {
                            lineGraphSeries.appendData(new DataPoint(stat.getDate(), stat.getLeftCalories()), true, response.body().size(), true);
                            dailyCalorieIntake.appendData(new DataPoint(stat.getDate(), stat.getDailyCalorieIntake()), true, response.body().size(), true);
                        }
                        lineGraphSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                            @Override
                            public void onTap(Series series, DataPointInterface dataPoint) {
                                String p = String.valueOf(dataPoint.getY());
                                BigDecimal rounded = new BigDecimal(p).setScale(1, RoundingMode.HALF_UP);

                                Toast.makeText(StatsActivity.this,
                                        "Likusios kcal: " + rounded,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        lineGraphSeries.setDrawDataPoints(true);
                        lineGraphSeries.setDataPointsRadius(15f);
                        lineGraphSeries.setAnimated(true);

                        dailyCalorieIntake.setColor(Color.RED);

                        graphView.removeAllSeries();
                        graphView.addSeries(lineGraphSeries);
                        graphView.addSeries(dailyCalorieIntake);
                        graphView.getGridLabelRenderer().setVerticalAxisTitle("Kalorijos");
                    } else if (graphName.equals("Maistinės medžiagos")) {
                        graphView.setVisibility(View.GONE);
                        pieChart.setVisibility(View.VISIBLE);
                        datePicker.setVisibility(View.VISIBLE);

                        List<PieEntry> entries = new ArrayList<>();
                        Calendar cal1 = Calendar.getInstance();
                        Calendar cal2 = Calendar.getInstance();
                        cal1.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

                        for (Stats stat : response.body()) {
                            cal2.setTime(stat.getDate());
                            if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                                    && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {

                                entries.add(new PieEntry(stat.getCarbAmount(), "Angl."));
                                entries.add(new PieEntry(stat.getFatAmount(), "Rieb."));
                                entries.add(new PieEntry(stat.getProteinAmount(), "Balt."));

                                goAndFillPieChart(entries);
                            }
                        }
                    }

                    if (graphView.getVisibility() == View.VISIBLE) {
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
            }

            @Override
            public void onFailure(Call<List<Stats>> call, Throwable t) {
            }
        });
    }

    private void goAndFillPieChart(List<PieEntry> entries) {
        PieDataSet dataSet = new PieDataSet(entries, "Maistinės medžiagos");

        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(30f);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.animateY(1000);
    }
}

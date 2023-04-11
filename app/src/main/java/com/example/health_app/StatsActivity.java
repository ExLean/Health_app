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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
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
    BarChart barChart;
    PieChart pieChart;
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
        barChart = findViewById(R.id.bar_chart);
        spinner = findViewById(R.id.spinner);
        pieChart = findViewById(R.id.pie_chart);

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
                        graphView.setVisibility(View.VISIBLE);
                        barChart.setVisibility(View.GONE);
                        pieChart.setVisibility(View.GONE);
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
                        graphView.setVisibility(View.VISIBLE);
                        barChart.setVisibility(View.GONE);
                        pieChart.setVisibility(View.GONE);
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
                    } else if (graphName.equals("Maistinės medžiagos")) {
                        graphView.setVisibility(View.GONE);
                        barChart.setVisibility(View.GONE);
                        pieChart.setVisibility(View.VISIBLE);
//                        graphView.setVisibility(View.GONE);
//                        barChart.setVisibility(View.VISIBLE);
//                        List<BarEntry> carbs = new ArrayList<>();
//                        List<BarEntry> fat = new ArrayList<>();
//                        List<BarEntry> protein = new ArrayList<>();
//
//                        for (Stats stat : response.body()) {
//                            carbs.add(new BarEntry((Long.valueOf(stat.getDate().getTime())).floatValue(), stat.getCarbAmount()));
//                            fat.add(new BarEntry((Long.valueOf(stat.getDate().getTime())).floatValue(), stat.getFatAmount()));
//                            protein.add(new BarEntry((Long.valueOf(stat.getDate().getTime())).floatValue(), stat.getProteinAmount()));
//                        }
//
////                        carbs.add(new BarEntry(0, 10));
////                        fat.add(new BarEntry(0, 15));
////                        protein.add(new BarEntry(0, 7));
////
////                        carbs.add(new BarEntry(1, 17));
////                        fat.add(new BarEntry(1, 5));
////                        protein.add(new BarEntry(1, 17));
////
////                        carbs.add(new BarEntry(2, 20));
////                        fat.add(new BarEntry(2, 12));
////                        protein.add(new BarEntry(2, 13));
//
//                        BarDataSet carbsSet = new BarDataSet(carbs, "Angl.");
//                        carbsSet.setColor(Color.BLUE);
//                        BarDataSet fatSet = new BarDataSet(fat, "Rieb.");
//                        fatSet.setColor(Color.YELLOW);
//                        BarDataSet proteinSet = new BarDataSet(protein, "Balt.");
//                        proteinSet.setColor(Color.WHITE);
//
//                        BarData barData = new BarData(carbsSet, fatSet, proteinSet);
//                        barData.setBarWidth(0.8f);
//
//                        barChart.setDrawValueAboveBar(true);
//                        barChart.setDrawGridBackground(false);
//                        barChart.getDescription().setEnabled(false);
//                        barChart.getLegend().setEnabled(true);
//                        barChart.animateY(1000);
//
//
//                        XAxis xAxis = barChart.getXAxis();
//                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//                        xAxis.setValueFormatter(new DateValueFormatter());
//                        xAxis.setGranularity(24 * 60 * 60 * 1000); // one day in milliseconds
//                        xAxis.setLabelCount(carbs.size());
//                        xAxis.setDrawGridLines(false);
//                        xAxis.setDrawAxisLine(true);
//                        xAxis.setAxisMinimum(carbs.get(0).getX());
//                        xAxis.setAxisMaximum(carbs.get(carbs.size() - 1).getX() + xAxis.getGranularity());
//
//                        barChart.setData(barData);
//
////                        barChart.getXAxis().setAxisMaximum(carbs.get(0).getX() + barChart.getBarData().getGroupWidth(1f, 0.03f) * 3);
////                        barChart.groupBars(carbs.get(0).getX(), 1f, 0.03f);
//                        barChart.canScrollHorizontally(1);
//                        barChart.invalidate();

                        List<PieEntry> entries = new ArrayList<>();

//                        for (Stats stat : response.body()) {
//                            if (stat.getDate().equals(new java.sql.Date(System.currentTimeMillis()))) {
                                entries.add(new PieEntry(response.body().get(2).getCarbAmount(), "Angl."));
                                entries.add(new PieEntry(response.body().get(2).getFatAmount(), "Rieb."));
                                entries.add(new PieEntry(response.body().get(2).getProteinAmount(), "Balt."));
//                            }
//                        }

                        PieDataSet dataSet = new PieDataSet(entries, "Pie Chart");

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

    private static class DateValueFormatter extends ValueFormatter {

        private final SimpleDateFormat mFormat = new SimpleDateFormat("dd/MM");

        @Override
        public String getFormattedValue(float value) {
            return mFormat.format(new Date((long) value));
        }
    }
}

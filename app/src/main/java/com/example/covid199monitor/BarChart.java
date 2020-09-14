package com.example.covid199monitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;

import java.util.ArrayList;
import java.util.List;

public class BarChart extends AppCompatActivity {
    AnyChartView barChart;

    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);

        barChart = findViewById(R.id.barChart);

        Intent i = getIntent();
        List<Estado> estados = (List<Estado>) i.getSerializableExtra("estados");
        int type = Integer.parseInt(i.getStringExtra("type"));

        text = findViewById(R.id.title);

        BarChartShow(type, estados);
    }

    private void BarChartShow(int type, List<Estado> estados) {
        if(type==1) {
            Cartesian bar = AnyChart.bar();

            List<DataEntry> data = new ArrayList<>();

            for (int i = 0; i < estados.size(); i++) {
                data.add(new ValueDataEntry(estados.get(i).getNombre(), estados.get(i).getCasos_positivos()));
            }

            text.setText("Casos Positivos");

            bar.data(data);

            barChart.setChart(bar);
        }
        else if(type==2) {
            Cartesian bar = AnyChart.bar();

            List<DataEntry> data = new ArrayList<>();

            for(int i=0; i<estados.size(); i++) {
                data.add(new ValueDataEntry(estados.get(i).getNombre(), estados.get(i).getFallecimientos()));
            }

            text.setText("Fallecimientos");

            bar.data(data);

            barChart.setChart(bar);
        }
    }
}
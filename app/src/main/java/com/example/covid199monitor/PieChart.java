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
import com.anychart.charts.Cartesian3d;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Bar3d;
import com.anychart.core.lineargauge.pointers.Bar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PieChart extends AppCompatActivity {

    AnyChartView pieChart;

    TextView text;
    Button alt;

    List<Estado> estados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        pieChart = findViewById(R.id.pieChart);

        Intent i = getIntent();
        estados = (List<Estado>) i.getSerializableExtra("estados");
        final int type = Integer.parseInt(i.getStringExtra("type"));

        text = findViewById(R.id.title);
        alt = findViewById(R.id.alt);

        alt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewBarChartC(Integer.toString(type));
            }
        });

        PieChartShow(type, estados);
    }

    private void ViewBarChartC(String q) {
        Intent i = new Intent(this, BarChart.class);
        i.putExtra("type", q);
        i.putExtra("estados", (Serializable)estados);

        startActivity(i);
    }

    private void PieChartShow(int type, List<Estado> estados) {
        if(type==1) {
            Pie pie = AnyChart.pie();

            List<DataEntry> data = new ArrayList<>();

            for (int i = 0; i < estados.size(); i++) {
                data.add(new ValueDataEntry(estados.get(i).getNombre(), estados.get(i).getCasos_positivos()));
            }

            text.setText("Casos Positivos");

            pie.data(data);

            pieChart.setChart(pie);
        }
        else if(type==2) {
            Pie pie = AnyChart.pie();

            List<DataEntry> data = new ArrayList<>();

            for(int i=0; i<estados.size(); i++) {
                data.add(new ValueDataEntry(estados.get(i).getNombre(), estados.get(i).getFallecimientos()));
            }

            text.setText("Fallecimientos");

            pie.data(data);

            pieChart.setChart(pie);
        }
    }
}
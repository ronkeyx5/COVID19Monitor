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

import java.util.ArrayList;
import java.util.List;

public class PieChart extends AppCompatActivity {

    String[] efe = {"Aguascalientes", "Baja California", "Colima", "Estado de Mexico"};
    int ene[] = {5,9,3,4};
    AnyChartView pieChart;
    AnyChartView barChart;

    TextView text;
    Button alt;

    int type;
    List<Estado> estados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);

        //ConfigPieChart();

        Intent i = getIntent();
        estados = (List<Estado>) i.getSerializableExtra("estados");
        type = Integer.parseInt(i.getStringExtra("type"));

        text = findViewById(R.id.title);
        alt = findViewById(R.id.alt);

        alt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeChart();
            }
        });

        PieChartShow(1);
    }

    private void ChangeChart() {
        if(pieChart.getVisibility()==View.VISIBLE)  {
            pieChart.setVisibility(View.GONE);
            barChart.setVisibility(View.VISIBLE);
            alt.setText("Pastel");
            PieChartShow(2);
        }
        else {
            pieChart.setVisibility(View.VISIBLE);
            barChart.setVisibility(View.GONE);
            alt.setText("Barras");
            PieChartShow(1);
        }
    }

    private void PieChartShow(int type2) {
        if(type==1 && type2==1) {
            Pie pie = AnyChart.pie();

            List<DataEntry> data = new ArrayList<>();

            for(int i=0; i<estados.size(); i++) {
                data.add(new ValueDataEntry(estados.get(i).getNombre(), estados.get(i).getCasos_positivos()));
            }

            text.setText("Casos Positivos");

            pie.data(data);

            pieChart.setChart(pie);
        }
        else if(type==1 && type2==2)    {
            Cartesian bar = AnyChart.bar();

            List<DataEntry> data = new ArrayList<>();

            for(int i=0; i<estados.size(); i++) {
                data.add(new ValueDataEntry(estados.get(i).getNombre(), estados.get(i).getCasos_positivos()));
            }

            text.setText("Casos Positivos");

            bar.data(data);

            barChart.setChart(bar);
        }
        else if(type==2 && type2==1) {
            Pie pie = AnyChart.pie();

            List<DataEntry> data = new ArrayList<>();

            for(int i=0; i<estados.size(); i++) {
                data.add(new ValueDataEntry(estados.get(i).getNombre(), estados.get(i).getFallecimientos()));
            }

            text.setText("Fallecimientos");

            pie.data(data);

            pieChart.setChart(pie);
        }
        else if(type==2 && type2==2)    {
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

    public void ConfigPieChart() {
        Pie pie = AnyChart.pie();
        List<DataEntry> data = new ArrayList<>();

        for(int i=0; i<efe.length; i++) {
            data.add(new ValueDataEntry(efe[i], ene[i]));
        }

        pie.data(data);
        pieChart.setChart(pie);
    }
}
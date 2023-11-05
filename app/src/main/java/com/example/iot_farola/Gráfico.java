package com.example.iot_farola;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iot_farola.modelo.Farola;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class Gráfico extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grafica);
        LineChart lineChart = findViewById(R.id.linechart);

        // Obtén la farola de los extras del intent
        Farola farolaSeleccionada = getIntent().getParcelableExtra("farola");

        if (farolaSeleccionada != null) {
            setupLineChart(lineChart, farolaSeleccionada);
        }
    }

    private void setupLineChart(LineChart lineChart, Farola farola) {
        // Configuración del gráfico
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.getDescription().setEnabled(false);

        // Agregar datos al gráfico de líneas usando los datos de la farola
        addDataToLineChart(lineChart, farola);
    }

    private void addDataToLineChart(LineChart lineChart, Farola farola) {
        ArrayList<Entry> entries = new ArrayList<>();

        // Agregar datos de ejemplo (puedes reemplazarlos con tus propios datos)
        entries.add(new Entry(1f, 10f));
        entries.add(new Entry(2f, 25f));
        entries.add(new Entry(3f, 15f));
        entries.add(new Entry(4f, 32f));
        entries.add(new Entry(5f, 18f));

        LineDataSet dataSet = new LineDataSet(entries, "Datos de Ejemplo");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);
    }
}

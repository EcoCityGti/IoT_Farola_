package com.example.iot_farola;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class Tab1 extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab1, container, false);
        ViewPager2 viewpager = v.findViewById(R.id.viewPaGer);
        viewpager.setAdapter(new com.example.iot_farola.MiPageAdapter1(requireActivity()));
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        TextView nombre = v.findViewById(R.id.nombre);
        nombre.setText(usuario.getDisplayName());

        if(usuario.isAnonymous()){
            nombre.setText("Invitado/a");
        }

        String[] nombres = new String[]{"Listas","Mapa de la zona"};
        TabLayout tabs = v.findViewById(R.id.tabs1);
        new TabLayoutMediator(tabs, viewpager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position){
                        Drawable iconDrawable = null;
                        int iconSizeInDp = 150; // Tamaño del icono en dp (ajusta este valor según tus necesidades)

                        switch (position) {
                            case 0:
                                tab.setText("Lista");
                                tab.setIcon(R.drawable.baseline_format_list_bulleted_24);
                                break;
                            case 1:
                                tab.setText("Mapa");
                                tab.setIcon(R.drawable.baseline_map_24);
                                break;
                        }
                    }
                }
        ).attach();
        LineChart lineChart = v.findViewById(R.id.LineChart);
        setupLineChart(lineChart);

        RequestQueue colaPeticiones = Volley.newRequestQueue(requireActivity());
        ImageLoader lectorImagenes = new ImageLoader(colaPeticiones,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache =
                            new LruCache<String, Bitmap>(10);
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }
                });
// Foto de usuario
        Uri urlImagen = usuario.getPhotoUrl();
        if (urlImagen != null) {
            NetworkImageView foto = (NetworkImageView) v.findViewById(R.id.imagen1);
            foto.setImageUrl(urlImagen.toString(), lectorImagenes);
        }
        return v;
    }
    private void setupLineChart(LineChart lineChart) {
        // Configuración del gráfico
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.getDescription().setEnabled(false);

        // Agregar datos al gráfico de líneas (puedes llamar a tu método addDataToLineChart aquí)
        addDataToLineChart(lineChart);
    }
    private void addDataToLineChart(LineChart lineChart) {
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

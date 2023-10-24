package com.example.iot_farola;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class listaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs_barra_mapalista);
        ViewPager2 viewPager = findViewById(R.id.menu_visto);
        viewPager.setAdapter(new com.example.iot_farola.MiPageAdapter(this));
        TabLayout tabs = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabs, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position){
                        tab.setText(nombres[position]);
                    }
                }
        ).attach();
    }
    private String[] nombres = new String[]{"Lista","Mapa"};
}

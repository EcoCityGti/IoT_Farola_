package com.example.iot_farola;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AppActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs_barra);
        ViewPager2 viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new com.example.iot_farola.MiPageAdapter(this));
        TabLayout tabs = findViewById(R.id.tabs);
        new TabLayoutMediator(tabs, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position){
                        Drawable iconDrawable = null;
                        int iconSizeInDp = 150; // Tamaño del icono en dp (ajusta este valor según tus necesidades)

                        switch (position) {
                            case 0:
                                iconDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.twotone_home_24);
                                break;
                            case 1:
                                iconDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_light_24);
                                break;
                            case 2:
                                iconDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.baseline_electric_bolt_24);
                                break;
                            case 3:
                                iconDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.outline_account_circle_24);
                                break;
                        }
                        if (iconDrawable != null) {
                            iconDrawable.setBounds(0, 0, (int) (iconSizeInDp * getResources().getDisplayMetrics().density), (int) (iconSizeInDp * getResources().getDisplayMetrics().density));
                            iconDrawable.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.my_dark_tertiary), PorterDuff.Mode.SRC_ATOP);
                            tab.setIcon(iconDrawable);
                        }
                    }
                }
        ).attach();
    }
    //private String[] nombres = new String[]{"Inicio","Farola","EcoCoin","Perfil"};
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("AppActivity", "onCreateOptionsMenu called");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            // Handle the settings action here.
            return true;
        }
        if (id == R.id.acercaDe) {
            // Handle the "Acerca de" action by launching the AcercaDeActivity.
            lanzarAcercaDe();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void lanzarAcercaDe() {
        Intent i = new Intent(this, AcercaDeActivity.class);
        //mp.pause();
        startActivity(i);
    }
}
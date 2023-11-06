package com.example.iot_farola.presentacion;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.iot_farola.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
//-------------------------App--------------------------------------------
public class AppActivity extends AppCompatActivity {
//---------------------On Create()--------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        setContentView(R.layout.tabs_barra);
        ViewPager2 viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new com.example.iot_farola.MiPageAdapter(this));
        TabLayout tabs = findViewById(R.id.tabs);
        //---------------------Configuracion TabsBarra()--------------------------------------------

        new TabLayoutMediator(tabs, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position){
                        Drawable iconDrawable = null;
                        int iconSizeInDp = 150; // Tamaño del icono en dp (ajusta este valor según tus necesidades)
//----------------------------------Insertar iconos en cada tab--------------------------------------------------------------
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
//--------------------------------------------------Resize Icon(ignorar)--------------------------------------------------------------------
                        if (iconDrawable != null) {
                            iconDrawable.setBounds(0, 0, (int) (iconSizeInDp * getResources().getDisplayMetrics().density), (int) (iconSizeInDp * getResources().getDisplayMetrics().density));
                            //iconDrawable.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.my_dark_tertiary), PorterDuff.Mode.SRC_ATOP);
                            tab.setIcon(iconDrawable);
                        }
                    }
                }
        ).attach();//Configuracion TabBarra()
    }//On create()
   /* @Override
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
    }*/
}
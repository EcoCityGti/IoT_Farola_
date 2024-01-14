package com.example.iot_farola.presentacion;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.iot_farola.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Tab3 extends Fragment {
    private CheckBox checkBox3;
    private CheckBox checkBox4;
    private CheckBox checkBox5;
    private CheckBox checkBox6;
    private CheckBox checkBox7;
    private CheckBox checkBox8;
    private CheckBox checkBox9;
    private CheckBox checkBox10;
    private CheckBox checkBox11;
    private CheckBox checkBox12;
    private CheckBox checkBox13;
    private CheckBox checkBox14;
    private ImageButton imageButton;
    private TabLayout tutorial;
    private TabLayout diarios;
    private TabLayout semanal;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab3, container, false);
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        imageButton = v.findViewById(R.id.imageButton2);
        checkBox3 = v.findViewById(R.id.checkBox3);
        checkBox4 = v.findViewById(R.id.checkBox4);
        checkBox5 = v.findViewById(R.id.checkBox5);
        checkBox6 = v.findViewById(R.id.checkBox6);
        checkBox7 = v.findViewById(R.id.checkBox7);
        checkBox8 = v.findViewById(R.id.checkBox8);
        checkBox9 = v.findViewById(R.id.checkBox9);
        checkBox10 = v.findViewById(R.id.checkBox10);
        checkBox11 = v.findViewById(R.id.checkBox11);
        checkBox12 = v.findViewById(R.id.checkBox12);
        checkBox13 = v.findViewById(R.id.checkBox13);
        checkBox14 = v.findViewById(R.id.checkBox14);
        tutorial = v.findViewById(R.id.Tutorial);
        diarios = v.findViewById(R.id.desafios_diarios);
        semanal = v.findViewById(R.id.semanal);
        tutorial.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectedTabPosition = tab.getPosition();

                // Dependiendo de la pestaña seleccionada, muestra u oculta las CheckBox.
                switch (selectedTabPosition) {
                    case 0:
                        checkBox5.setVisibility(View.VISIBLE);
                        checkBox6.setVisibility(View.VISIBLE);
                        checkBox3.setVisibility(View.VISIBLE);
                        checkBox4.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.baseline_keyboard_arrow_down_24);
                        checkBox5.setVisibility(View.GONE);
                        checkBox6.setVisibility(View.GONE);
                        checkBox3.setVisibility(View.GONE);
                        checkBox4.setVisibility(View.GONE);
                        break;
                    // Agrega más casos para otras pestañas si es necesario.
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No es necesario implementar nada aquí, pero puedes hacerlo si es necesario.
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No es necesario implementar nada aquí, pero puedes hacerlo si es necesario.
            }
        });
        semanal.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectedTabPosition = tab.getPosition();

                // Dependiendo de la pestaña seleccionada, muestra u oculta las CheckBox.
                switch (selectedTabPosition) {
                    case 0:
                        checkBox11.setVisibility(View.VISIBLE);
                        checkBox12.setVisibility(View.VISIBLE);
                        checkBox13.setVisibility(View.VISIBLE);
                        checkBox14.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.baseline_keyboard_arrow_down_24);
                        checkBox11.setVisibility(View.GONE);
                        checkBox12.setVisibility(View.GONE);
                        checkBox13.setVisibility(View.GONE);
                        checkBox14.setVisibility(View.GONE);
                        break;
                    // Agrega más casos para otras pestañas si es necesario.
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No es necesario implementar nada aquí, pero puedes hacerlo si es necesario.
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No es necesario implementar nada aquí, pero puedes hacerlo si es necesario.
            }
        });
        diarios.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectedTabPosition = tab.getPosition();

                // Dependiendo de la pestaña seleccionada, muestra u oculta las CheckBox.
                switch (selectedTabPosition) {
                    case 0:
                        checkBox7.setVisibility(View.VISIBLE);
                        checkBox8.setVisibility(View.VISIBLE);
                        checkBox9.setVisibility(View.VISIBLE);
                        checkBox10.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.baseline_keyboard_arrow_down_24);
                        checkBox7.setVisibility(View.GONE);
                        checkBox8.setVisibility(View.GONE);
                        checkBox9.setVisibility(View.GONE);
                        checkBox10.setVisibility(View.GONE);
                        break;
                    // Agrega más casos para otras pestañas si es necesario.
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No es necesario implementar nada aquí, pero puedes hacerlo si es necesario.
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No es necesario implementar nada aquí, pero puedes hacerlo si es necesario.
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar u ocultar las casillas de verificación al hacer clic en el ImageButton
                int visibility = checkBox3.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                checkBox3.setVisibility(visibility);
                checkBox4.setVisibility(visibility);
                checkBox5.setVisibility(visibility);
                checkBox6.setVisibility(visibility);
            }
        });
        return v;
    }

}

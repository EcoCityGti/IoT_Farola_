package com.example.iot_farola;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_farola.datos.AdaptadorFarolas;
import com.example.iot_farola.datos.RepositorioFarolas;
import com.example.iot_farola.modelo.Farola;

public class Lista extends Fragment {
    private RecyclerView recyclerView;
    private AdaptadorFarolas adaptador;
    private RepositorioFarolas farolas;


    public Lista() {
        // Constructor vacío requerido
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lista, container, false);

        // Inicializar RecyclerView y adaptador
        // Configura el adaptador con los datos que deseas mostrar
        // adaptador.setDatos(datos);
        adaptador = ((Aplicacion) getApplicationContext()).adaptador;
        farolas = ((Aplicacion) getApplicationContext()).farolas;
        recyclerView = view.findViewById(R.id.RecyclerView);
        recyclerView.setAdapter(adaptador);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adaptador);
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos= recyclerView.getChildAdapterPosition(v);
                mostrarFarola(pos);
            }
        });
        return view;
    }
    void mostrarFarola(int pos) {
        // Obtén la farola seleccionada
        Farola farolaSeleccionada = ((Aplicacion) requireContext().getApplicationContext()).farolas.elemento(pos);

        // Crea un intent para abrir la actividad Gráfico
        Intent intent = new Intent(requireContext(), Gráfico.class);
        intent.putExtra("farola", farolaSeleccionada); // Puedes pasar la farola como extra para que Gráfico la muestre en el gráfico
        startActivity(intent);
    }

}

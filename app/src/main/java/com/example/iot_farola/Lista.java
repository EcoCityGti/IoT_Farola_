package com.example.iot_farola;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_farola.datos.AdaptadorFarolasFirestoreUI;
import com.example.iot_farola.datos.RepositorioFarolas;
import com.example.iot_farola.modelo.Farola;
import com.example.iot_farola.presentacion.VistaFarolaActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class Lista extends Fragment {
    private RecyclerView recyclerView;
    private AdaptadorFarolasFirestoreUI adaptador;
    private RepositorioFarolas farolas;
    private String id;

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
        adaptador = ((Aplicacion) requireActivity().getApplication()).adaptador;
        id = ((Aplicacion) requireActivity().getApplication()).farolaId;
        farolas = ((Aplicacion) requireActivity().getApplication()).farolas;
        recyclerView = view.findViewById(R.id.RecyclerView);
        recyclerView.setAdapter(adaptador);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        adaptador.startListening();
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recyclerView.getChildAdapterPosition(v);
                mostrarFarola(pos);
            }
        });
        return view;
    }
    void mostrarFarola(int pos) {
        // Obtén el ID del documento de Firestore correspondiente a la posición
        String documentId = adaptador.getSnapshots().getSnapshot(pos).getId();

        // Accede a Firestore para obtener el campo "nombre" de la farola seleccionada
        FirebaseFirestore.getInstance().collection("farolas").document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Obtiene el valor del campo "nombre" del documento
                        String nombreFarola = documentSnapshot.getString("nombre");

                        // Crea un intent para abrir la actividad VistaFarolaActivity
                        Intent intent = new Intent(requireContext(), VistaFarolaActivity.class);
                        // Pasa el nombre como un extra al intent
                        intent.putExtra("nombreFarola", nombreFarola);
                        startActivity(intent);
                    } else {
                        // El documento no existe
                        Toast.makeText(requireContext(), "Documento no encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Maneja errores de lectura de Firestore
                    Toast.makeText(requireContext(), "Error al obtener datos de Firestore", Toast.LENGTH_SHORT).show();
                });
    }


}

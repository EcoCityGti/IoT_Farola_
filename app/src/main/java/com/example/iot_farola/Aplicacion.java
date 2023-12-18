package com.example.iot_farola;

import android.app.Application;

import com.example.iot_farola.datos.AdaptadorFarolas;
import com.example.iot_farola.datos.AdaptadorFarolasFirestoreUI;
import com.example.iot_farola.datos.FarolasLista;
import com.example.iot_farola.datos.RepositorioFarolas;
import com.example.iot_farola.modelo.Farola;
import com.example.iot_farola.modelo.GeoPunto;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class Aplicacion extends Application {

    public GeoPunto posicionActual =  new GeoPunto(0.0, 0.0);
    public RepositorioFarolas farolas;

    public AdaptadorFarolasFirestoreUI adaptador;
    public String farolaId="";



    @Override public void onCreate() {
        super.onCreate();
        farolas = (RepositorioFarolas) new FarolasLista();
        Query query = FirebaseFirestore.getInstance()
                .collection("farolas")
                .limit(50);
        FirestoreRecyclerOptions<Farola> opciones = new FirestoreRecyclerOptions
                .Builder<Farola>().setQuery(query, Farola.class).build();
        adaptador = new AdaptadorFarolasFirestoreUI(opciones, this);
    }
}

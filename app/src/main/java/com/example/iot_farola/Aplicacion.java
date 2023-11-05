package com.example.iot_farola;

import android.app.Application;

import com.example.iot_farola.datos.AdaptadorFarolas;
import com.example.iot_farola.datos.FarolasLista;
import com.example.iot_farola.datos.RepositorioFarolas;
import com.example.iot_farola.modelo.GeoPunto;


public class Aplicacion extends Application {

    public GeoPunto posicionActual =  new GeoPunto(0.0, 0.0);
    public RepositorioFarolas farolas = new FarolasLista();

    public AdaptadorFarolas adaptador = new AdaptadorFarolas(farolas);


    @Override public void onCreate() {
        super.onCreate();
    }
}

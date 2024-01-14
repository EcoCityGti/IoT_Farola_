package com.example.iot_farola.datos;



import com.example.iot_farola.modelo.Farola;

import java.util.ArrayList;
import java.util.List;

public class FarolasLista implements RepositorioFarolas {
    protected List<Farola> listaFarolas ;//= añadeEjemplos();//ejemploLugares();

    public FarolasLista() {
        listaFarolas = new ArrayList<Farola>();
        añadeEjemplos();
    }

    public Farola elemento(int id) {
        return listaFarolas.get(id);
    }



    public void añade(Farola farola) {
        listaFarolas.add(farola);
    }

    public int nuevo() {
        Farola farola = new Farola();
        listaFarolas.add(farola);
        return listaFarolas.size()-1;
    }

    public void borrar(int id) {
        listaFarolas.remove(id);
    }

    public int tamaño() {
        return listaFarolas.size();
    }

    public void actualiza(int id, Farola farola) {
        listaFarolas.set(id, farola);
    }

    public void añadeEjemplos() {
        añade(new Farola("Escuela Politécnica Superior de Gandía",
                "C/ Paranimf, 1 46730 Gandia (SPAIN)", -0.166093, 38.995656, "",0.0,0.0,0.0, 0, 0.0));
        añade(new Farola("El tardeo",
                "Carrer de la Ràbida, 36, 46730 Platja de Gandia, Valencia",
                -0.16490963584760662, 38.997195127454006, "",0.0,0.0,0.0,0,0.0));
        añade(new Farola("Ruta 66","Carrer Alcoi, 37, 46730 Grau i Platja, Valencia",  -0.16266961222227524, 38.995198851998346,
                "",0.0,0.0,0.0,0,0.0));
    }
}
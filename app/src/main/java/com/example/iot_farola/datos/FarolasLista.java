package com.example.iot_farola.datos;



import com.example.iot_farola.modelo.Farola;

import java.util.ArrayList;
import java.util.List;

public class FarolasLista implements RepositorioFarolas {
    protected List<Farola> listaFarolas ;//= añadeEjemplos();//ejemploLugares();

    public FarolasLista() {
        //listaLugares = ejemploLugares();
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
                "C/ Paranimf, 1 46730 Gandia (SPAIN)", -0.166093, 38.995656, 962849300, "http://www.epsg.upv.es",
                "Uno de los mejores lugares para formarse.", 3));
        /*añade(new Lugar("Al de siempre",
                "P.Industrial Junto Molí Nou - 46722, Benifla (Valencia)",
                -0.190642, 38.925857, TipoLugar.BAR, 636472405, "",
                "No te pierdas el arroz en calabaza.", 3));
        añade(new Lugar("androidcurso.com","ciberespacio", 0.0, 0.0, TipoLugar.EDUCACION,
                962849300,"http://androidcurso.com",
                "Amplia tus conocimientos sobre Android.", 5));
        añade(new Lugar("Barranco del Infierno",
                "Vía Verde del río Serpis. Villalonga (Valencia)",
                -0.295058, 38.867180, TipoLugar.NATURALEZA, 0,
                "http://sosegaos.blogspot.com.es/2009/02/lorcha-villalonga-via-"+
                        "verde-del-rio.html","Espectacular ruta para bici o andar", 4));
        añade(new Lugar("La Vital",
                "Avda. de La Vital, 0 46701 Gandía (Valencia)", -0.1720092,
                38.9705949, TipoLugar.COMPRAS, 962881070,
                "http://www.lavital.es/", "El típico centro comercial", 2));*/

    }

        /*public static List<Lugar> añadeEjemplos2() {
        ArrayList<Lugar> lugares = new ArrayList<Lugar>();
        lugares.add(new Lugar("Escuela Politécnica Superior de Gandía",
                "C/ Paranimf, 1 46730 Gandia (SPAIN)",-0.166093,38.995656,
                TipoLugar.EDUCACION,962849300, "http://www.epsg.upv.es",
                "Uno de los mejores lugares para formarse.", 3));
        lugares.add(new Lugar("Al de siempre",
                "P.Industrial Junto Molí Nou - 46722, Benifla (Valencia)",
                -0.190642, 38.925857, TipoLugar.BAR, 636472405, "",
                "No te pierdas el arroz en calabaza.", 3));
        lugares.add(new Lugar("androidcurso.com",
                "ciberespacio", 0.0, 0.0, TipoLugar.EDUCACION,
                962849300, "http://androidcurso.com",
                "Amplia tus conocimientos sobre Android.", 5));
        lugares.add(new Lugar("Barranco del Infierno",
                "Vía Verde del río Serpis. Villalonga (Valencia)",
                -0.295058, 38.867180, TipoLugar.NATURALEZA, 0,
                "http://sosegaos.blogspot.com.es/2009/02/lorcha-villalonga-via-"+
                        "verde-del-rio.html","Espectacular ruta para bici o andar", 4));
        lugares.add(new Lugar("La Vital",
                "Avda. de La Vital, 0 46701 Gandía (Valencia)", -0.1720092,
                38.9705949, TipoLugar.COMPRAS, 962881070,
                "http://www.lavital.es/", "El típico centro comercial", 2));
        return lugares;
    }*/
}

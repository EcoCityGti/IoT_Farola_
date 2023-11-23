package com.example.iot_farola.modelo;

import com.example.iot_farola.modelo.GeoPunto;

import java.io.Serializable;
//-----Clase Farola-------------------------------------------------------------
//------------------------------------------------------------------------------
public class Farola implements Serializable {
    private String nombre;
    private String direccion;
    private GeoPunto posicion;
    private String foto;
    private int telefono;

//---------------------Constructor--------------------------------------------
    public Farola(String nombre, String direccion, double longitud,
                  double latitud, int telefono, String foto) {
        //fecha = System.currentTimeMillis();
        posicion = new GeoPunto(longitud, latitud);
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.foto = foto;
    }

    public Farola() {
        posicion = new GeoPunto(0.0,0.0);
    }
//-----------------------Getters and Setters-------------------------------
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public GeoPunto getPosicion() {
        return posicion;
    }

    public void setPosicion(GeoPunto posicion) {
        this.posicion = posicion;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }
    @Override
    public String toString() {
        return "Lugar{" +
                "nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", posicion=" + posicion +
                ", foto='" + foto + '\'' +
                ", telefono=" + telefono +
                '}';
    }

}

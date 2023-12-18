package com.example.iot_farola.modelo;

import com.example.iot_farola.modelo.GeoPunto;

import java.io.Serializable;

public class Farola implements Serializable {
    private String nombre;
    private String direccion;
    private GeoPunto posicion;
    private String foto;
    private Double humedad;
    private Double temperatura;
    private Double ruido;
    private int luminosidad;
    private double humo;

    // Constructor modificado
    public Farola(String nombre, String direccion, double longitud,
                  double latitud, String foto, Double humedad, Double temperatura, Double ruido, int luminosidad, double humo) {
        this.posicion = new GeoPunto(longitud, latitud);
        this.nombre = nombre;
        this.direccion = direccion;
        this.humedad = humedad;
        this.temperatura = temperatura;
        this.ruido = ruido;
        this.luminosidad = luminosidad;
        this.humo = humo;
        this.foto = foto;
    }
    public Farola() {
        posicion = new GeoPunto(0.0,0.0);
    }
    // Getters y Setters

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

    public Double getHumedad() {
        return humedad;
    }

    public void setHumedad(Double humedad) {
        this.humedad = humedad;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public Double getRuido() {
        return ruido;
    }

    public void setRuido(Double ruido) {
        this.ruido = ruido;
    }

    public int getLuminosidad() {
        return luminosidad;
    }

    public void setLuminosidad(int luminosidad) {
        this.luminosidad = luminosidad;
    }

    public double getHumo() {
        return humo;
    }

    public void setHumo(double humo) {
        this.humo = humo;
    }

    @Override
    public String toString() {
        return "Farola{" +
                "nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", posicion=" + posicion +
                ", foto='" + foto + '\'' +
                ", humedad=" + humedad +
                ", temperatura=" + temperatura +
                ", ruido=" + ruido +
                ", luminosidad=" + luminosidad +
                '}';
    }
}
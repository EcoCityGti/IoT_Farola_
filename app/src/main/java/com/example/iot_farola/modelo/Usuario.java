package com.example.iot_farola.modelo;

import com.google.firebase.firestore.FirebaseFirestore;

public class Usuario {
    private String nombre;
    private String correo;
    private String telefono;
    private String direccion;
    private String nombreUsuario;
    private long inicioSesion;

    // Empty constructor
    public Usuario() {
    }

    // Constructor with all parameters
    public Usuario(String nombre, String correo, long inicioSesion, String telefono, String direccion, String nombreUsuario) {
        this.nombre = nombre;
        this.correo = correo;
        this.inicioSesion = inicioSesion;
        this.telefono = telefono;
        this.direccion = direccion;
        this.nombreUsuario = nombreUsuario;
    }

    // Constructor with two parameters, using System.currentTimeMillis() for inicioSesion
    public Usuario(String nombre, String correo, String telefono, String direccion, String nombreUsuario) {
        this(nombre, correo, System.currentTimeMillis(),telefono, direccion, nombreUsuario);
    }

    // Getter and setter methods for each attribute
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public long getInicioSesion() {
        return inicioSesion;
    }

    public void setInicioSesion(long inicioSesion) {
        this.inicioSesion = inicioSesion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }


    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}

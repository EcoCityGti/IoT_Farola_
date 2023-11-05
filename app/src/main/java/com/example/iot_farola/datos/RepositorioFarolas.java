package com.example.iot_farola.datos;


import com.example.iot_farola.modelo.Farola;

public interface RepositorioFarolas {
    Farola elemento(int id); //Devuelve el elemento dado su id
    void añade(Farola farola); //Añade el elemento indicado
    int nuevo(); //Añade un elemento en blanco y devuelve su id
    void borrar(int id); //Elimina el elemento con el id indicado
    int tamaño(); //Devuelve el número de elementos
    void actualiza(int id, Farola farola); //Reemplaza un elementoz
}

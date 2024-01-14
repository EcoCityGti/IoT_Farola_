package com.example.iot_farola.datos;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_farola.R;

import java.util.ArrayList;
import java.util.List;

public class ResultadosAdapter extends RecyclerView.Adapter<ResultadosAdapter.ResultadoViewHolder> {
    private List<String> datosFiltrados; // Lista filtrada según la consulta

    // Constructor y otros métodos necesarios

    public ResultadosAdapter() {
        this.datosFiltrados = new ArrayList<>();
    }

    public void filtrarResultados(List<String> nuevosDatos) {
        datosFiltrados.clear();
        datosFiltrados.addAll(nuevosDatos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResultadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el diseño de cada elemento de la lista y crea un ViewHolder para él
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_lista, parent, false);
        return new ResultadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultadoViewHolder holder, int position) {
        // Vincula los datos del elemento en la posición dada con las vistas en el ViewHolder
        String dato = datosFiltrados.get(position);
        holder.textViewNombre.setText(dato);
    }

    @Override
    public int getItemCount() {
        // Devuelve el número total de elementos en la lista
        return datosFiltrados.size();
    }

    // Implementación del adaptador (ViewHolder, onCreateViewHolder, onBindViewHolder, etc.)

    public static class ResultadoViewHolder extends RecyclerView.ViewHolder {
        // Define las vistas que se mostrarán en cada elemento de la lista (puede ajustarse según tus necesidades)
        public TextView textViewNombre;

        public ResultadoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializa las vistas aquí (por ejemplo, textViewNombre = itemView.findViewById(R.id.textViewNombre);)
        }
    }
}

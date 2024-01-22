package com.example.iot_farola.datos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_farola.Aplicacion;
import com.example.iot_farola.R;
import com.example.iot_farola.databinding.ElementoListaBinding;
import com.example.iot_farola.modelo.Farola;
import com.example.iot_farola.modelo.GeoPunto;

public class AdaptadorFarolas extends RecyclerView.Adapter<AdaptadorFarolas.ViewHolder> {

    protected RepositorioFarolas farolas;
    private int numItems;

    public static TextView distancia;
    // Lista de lugares a mostrar
    protected View.OnClickListener onClickListener;

    public AdaptadorFarolas(RepositorioFarolas farolas) {
        this.farolas = farolas;
        this.numItems = farolas.tamaño();
    }

    // Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nombre, direccion;
        public ImageView foto;

        // View Binding
        private final ElementoListaBinding binding;

        public ViewHolder(ElementoListaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            nombre = binding.nombre;
            direccion = binding.direccion;
            foto = binding.foto;
            distancia = binding.distancia;
        }

        // Personalizamos un ViewHolder a partir de un lugar
        public void personaliza(Farola farola) {
            binding.nombre.setText(farola.getNombre());
            binding.direccion.setText(farola.getDireccion());
            GeoPunto pos = ((Aplicacion) itemView.getContext().getApplicationContext()).posicionActual;
            if (pos.equals(GeoPunto.SIN_POSICION) || farola.getPosicion().equals(GeoPunto.SIN_POSICION)) {
                distancia.setText("... Km");
            } else {
                int d = (int) pos.distancia(farola.getPosicion());
                if (d < 2000)
                    distancia.setText(d + " " + itemView.getContext().getString(R.string.distancia_m));
                else
                    distancia.setText(d / 1000 + " " + itemView.getContext().getString(R.string.distancia_km));
            }
        }
    }

    // Creamos el ViewHolder con la vista de un elemento sin personalizar
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        ElementoListaBinding binding = ElementoListaBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                false);
        binding.getRoot().setOnClickListener(onClickListener);
        return new ViewHolder(binding);
    }

    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Farola farola = farolas.elemento(position);
        holder.personaliza(farola);
    }

    // Indicamos el número de elementos de la lista
    @Override
    public int getItemCount() {
        return numItems;
    }

    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setNumItems(int numItems) {
        this.numItems = numItems;
    }

    public void updateNumItems(int newNumItems) {
        numItems = newNumItems;
        notifyDataSetChanged();
    }
}

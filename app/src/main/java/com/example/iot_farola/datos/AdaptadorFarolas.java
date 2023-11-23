package com.example.iot_farola.datos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_farola.Aplicacion;
import com.example.iot_farola.modelo.Farola;
import com.example.iot_farola.modelo.GeoPunto;
import com.example.iot_farola.R;


public class AdaptadorFarolas extends RecyclerView.Adapter<AdaptadorFarolas.ViewHolder> {

    protected RepositorioFarolas farolas;
    private int numItems;

    public static TextView distancia;
    // Lista de lugares a mostrar
    protected View.OnClickListener onClickListener;

    public AdaptadorFarolas(RepositorioFarolas farolas) {
        this.farolas = farolas;
        this.numItems=farolas.tamaño();
    }

    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class ViewHolder extends RecyclerView.ViewHolder {

            public TextView nombre,direccion;
            public ImageView foto;
            public RatingBar valoracion;
            public ViewHolder(View itemView){
                super(itemView);
                nombre = (TextView) itemView.findViewById(R.id.nombre);
                direccion = (TextView) itemView.findViewById(R.id.direccion);;
                foto = (ImageView) itemView.findViewById(R.id.foto);
                valoracion = (RatingBar) itemView.findViewById(R.id.valoracion);
                distancia = (TextView) itemView.findViewById(R.id.distancia);
            }



        // Personalizamos un ViewHolder a partir de un lugar
        public void personaliza(Farola lugar) {
            nombre.setText(lugar.getNombre());
            direccion.setText(lugar.getDireccion());
            foto.setScaleType(ImageView.ScaleType.FIT_END);
            GeoPunto pos=((Aplicacion) itemView.getContext().getApplicationContext())
                    .posicionActual;
            if (pos.equals(GeoPunto.SIN_POSICION) ||
                    lugar.getPosicion().equals(GeoPunto.SIN_POSICION)) {
                distancia.setText("... Km");
            } else {
                int d=(int) pos.distancia(lugar.getPosicion());
                if (d < 2000) distancia.setText(d + " m");
                else          distancia.setText(d / 1000 + " Km");
            }
        }
    }

    // Creamos el ViewHolder con la vista de un elemento sin personalizar
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_lista, parent, false);
        view.setOnClickListener(onClickListener);
        return new AdaptadorFarolas.ViewHolder(view);
    }

    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(ViewHolder holder, int posicion) {
        Farola farola = farolas.elemento(posicion);
        holder.personaliza(farola);
    }

    // Indicamos el número de elementos de la lista
    @Override public int getItemCount() {
        return numItems;
    }

    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public void setNumItems(int numItems){
        this.numItems = numItems;
    }
    public void updateNumItems(int newNumItems) {
        numItems = newNumItems;
        notifyDataSetChanged();
    }

}

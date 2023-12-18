package com.example.iot_farola.datos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.iot_farola.R;
import com.example.iot_farola.datos.AdaptadorFarolas;
import com.example.iot_farola.modelo.Farola;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import kotlin.jvm.functions.Function4;


public class AdaptadorFarolasFirestoreUI extends
        FirestoreRecyclerAdapter<Farola, AdaptadorFarolas.ViewHolder> {
    protected View.OnClickListener onClickListener;
    protected Context context;

    public AdaptadorFarolasFirestoreUI(
            @NonNull FirestoreRecyclerOptions<Farola> options, Context context){
        super(options);
        this.context = context;
    }

    @Override
    public AdaptadorFarolas.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elemento_lista, parent, false);
        view.setOnClickListener(onClickListener);
        return new AdaptadorFarolas.ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdaptadorFarolas
            .ViewHolder holder, int position, @NonNull Farola farola) {
        holder.personaliza(farola);
    }

    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
    }

    public String getKey(int pos) {
        return super.getSnapshots().getSnapshot(pos).getId();
    }

    public int getPos(String id) {
        int pos = 0;
        while (pos < getItemCount()) {
            if (getKey(pos).equals(id)) return pos;
            pos++;
        }
        return -1;
    }
}

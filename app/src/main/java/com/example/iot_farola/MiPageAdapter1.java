package com.example.iot_farola;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.iot_farola.presentacion.MapaActivity;


public class MiPageAdapter1 extends FragmentStateAdapter {
    public MiPageAdapter1(FragmentActivity activity){
        super(activity);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override @NonNull
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new Lista();
            case 1: return new MapaActivity();
        }
        return null;
    }
}

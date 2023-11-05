package com.example.iot_farola;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.iot_farola.presentacion.Tab1;
import com.example.iot_farola.presentacion.Tab2;
import com.example.iot_farola.presentacion.Tab3;
import com.example.iot_farola.presentacion.Tab4;

public class MiPageAdapter extends FragmentStateAdapter {
    public MiPageAdapter(FragmentActivity activity){
        super(activity);
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    @Override @NonNull
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new Tab1();
            case 1: return new Tab2();
            case 2: return new Tab3();
            case 3: return new Tab4();
        }
        return null;
    }
}

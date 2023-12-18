package com.example.iot_farola;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.util.List;

public class SensorDataAdapter extends ArrayAdapter<String> {
    private Context context;

    public SensorDataAdapter(Context context, List<String> sensorDataList) {
        super(context, R.layout.grid_item_layout, android.R.id.text1, sensorDataList);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        ImageView iconImageView = view.findViewById(R.id.imageView7);

        // Change the icon based on the data type
        String sensorData = getItem(position);
        if (iconImageView != null && sensorData != null) {
            if (sensorData.contains("25°C")) {
                iconImageView.setImageResource(R.drawable.baseline_thermostat_24);
            } else if (sensorData.contains("60%")) {
                iconImageView.setImageResource(R.drawable.baseline_water_drop_24);
            } else if (sensorData.contains("50dB")) {
                iconImageView.setImageResource(R.drawable.baseline_volume_up_24);
            } else if (sensorData.contains("800l")) {
                iconImageView.setImageResource(R.drawable.baseline_light_24);
            } else {
                // Default icon
                iconImageView.setImageResource(R.drawable.farolasin);
            }
        }
        return view;
    }

}


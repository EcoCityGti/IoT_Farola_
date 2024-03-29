package com.example.iot_farola.presentacion;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


import com.example.iot_farola.Aplicacion;
import com.example.iot_farola.R;
import com.example.iot_farola.datos.RepositorioFarolas;
import com.example.iot_farola.modelo.Farola;
import com.example.iot_farola.modelo.GeoPunto;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MapaActivity extends Fragment
        implements OnMapReadyCallback {
    private GoogleMap mapa;
    private RepositorioFarolas farolas;
    private int LOCATION_PERMISSION_REQUEST_CODE=54656464;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mapa, container, false);
        farolas = ((Aplicacion) requireActivity().getApplication()).farolas;
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);
        if (!checkLocationPermissions()) {
            // Mostrar un diálogo explicativo antes de solicitar permiso
            showLocationPermissionExplanationDialog();
        }
        return view;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Verificar y solicitar permisos de ubicación
        if (ActivityCompat.checkSelfPermission(requireActivity(),
                ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapa.setMyLocationEnabled(true);
            mapa.getUiSettings().setZoomControlsEnabled(true);
            mapa.getUiSettings().setCompassEnabled(true);
        } else {
            // Solicitar permisos si no están disponibles
            requestLocationPermissions();
        }
        // Centrar el mapa en la Comunidad Valenciana
        LatLng comunidadValenciana = new LatLng(39.4699, -0.3763); // Coordenadas aproximadas de la Comunidad Valenciana
        float zoomLevel = 7.0f; // Puedes ajustar el nivel de zoom según tus preferencias

        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(comunidadValenciana, zoomLevel));

        // Obtener datos de farolas desde Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("farolas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Farola farola = document.toObject(Farola.class);
                            GeoPunto p = farola.getPosicion();
                            if (p != null && p.getLatitud() != 0) {
                                // Crear marcador y agregar al mapa
                                Bitmap iGrande = BitmapFactory.decodeResource(getResources(), R.drawable.marcador);
                                Bitmap icono = Bitmap.createScaledBitmap(iGrande,
                                        iGrande.getWidth() / 20, iGrande.getHeight() / 20, false);

                                mapa.addMarker(new MarkerOptions()
                                        .position(new LatLng(p.getLatitud(), p.getLongitud()))
                                        .title(farola.getNombre()).snippet(farola.getDireccion())
                                        .icon(BitmapDescriptorFactory.fromBitmap(icono)));
                            }
                        }
                    } else {
                        // Manejar errores al obtener datos de Firestore
                    }
                });
    }

    private boolean checkLocationPermissions() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }
    private void showLocationPermissionExplanationDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        dialogBuilder.setMessage(R.string.localiz);
        dialogBuilder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestLocationPermissions(); // Solicitar permiso después de la aceptación.
            }
        });
        dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Manejar la negación del permiso aquí.
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

}

package com.example.iot_farola.presentacion;// Importa las clases necesarias
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.iot_farola.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class TecnicoActivity extends AppCompatActivity {

    // Definir un código de solicitud para el permiso de la cámara
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tecnico);

        // Solicitar permiso de cámara si no está concedido
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Si el permiso de la cámara ya está concedido, inicializar el lector de códigos QR
            initQRCodeScanner();
        }
    }

    // Método para inicializar el lector de códigos QR
    private void initQRCodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }

    // Método llamado después de que el usuario otorga o deniega el permiso
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, inicializar el lector de códigos QR
                initQRCodeScanner();
            } else {
                // Permiso denegado, mostrar un mensaje y cerrar la aplicación
                Toast.makeText(this, "Permiso de cámara denegado. La aplicación se cerrará.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    // Método llamado después de escanear un código QR
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // Escaneo cancelado
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show();
            } else {
                // Se ha leído un código QR, muestra el resultado
                String scannedData = result.getContents();
                Log.d("QRCodeScanner", "Código QR leído: " + scannedData);
                Toast.makeText(this, "Código QR leído: " + scannedData, Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
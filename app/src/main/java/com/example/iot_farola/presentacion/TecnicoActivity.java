package com.example.iot_farola.presentacion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iot_farola.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class TecnicoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Iniciar el escáner QR
        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Obtener el resultado del escaneo
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        // Verificar si el escaneo fue exitoso
        if (result != null && result.getContents() != null) {
            // Obtener el contenido del código QR
            String qrContent = result.getContents();

            // En este ejemplo, asumimos que el contenido es una URI de archivo
            Uri fileUri = Uri.parse(qrContent);

            // Abrir el archivo
            openFile(fileUri);
        } else {
            // El escaneo fue cancelado
            Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show();
            finish(); // Puedes cerrar la actividad si el escaneo fue cancelado
        }
    }

    private void openFile(Uri fileUri) {
        // Aquí puedes implementar la lógica para abrir el archivo
        // Puedes usar Intent para abrir el archivo con la aplicación correspondiente

        Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
        openFileIntent.setData(fileUri);
        startActivity(openFileIntent);

        finish(); // Puedes cerrar la actividad después de abrir el archivo
    }
}
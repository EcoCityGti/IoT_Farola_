package com.example.iot_farola.presentacion;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iot_farola.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Reestablecer extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText etCorreo;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recontrasenya);
        auth = FirebaseAuth.getInstance(); // Initialize Firebase Authentication
        etCorreo = findViewById(R.id.editTextTextEmailAddress2);
        Button resta = findViewById(R.id.Reestablecer);

        resta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarCorreoRestablecerContraseña(v);
            }
        });
    }
    private void mensaje(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Aviso")
                .setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void enviarCorreoRestablecerContraseña(View view) {
        // Obtén la dirección de correo del usuario
        String correo = etCorreo.getText().toString();

        if (correo.isEmpty()) {
            etCorreo.setError(getString(R.string.loginerr1));
            return;
        }

        // Mostrar un cuadro de diálogo de carga
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.recontr2));
        progressDialog.show();

        // Utiliza FirebaseAuth para enviar el correo de restablecimiento
        auth.sendPasswordResetEmail(correo)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss(); // Ocultar el cuadro de diálogo de carga

                        if (task.isSuccessful()) {
                            // Envío de correo exitoso
                            mensaje(getString(R.string.recontr3));
                        } else {
                            // Fallo en el envío del correo
                            mensaje(getString(R.string.recontr4));
                        }
                    }
                });
    }

}

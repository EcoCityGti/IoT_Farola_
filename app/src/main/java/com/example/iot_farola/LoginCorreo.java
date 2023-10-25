package com.example.iot_farola;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginCorreo extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText etCorreo;
    private EditText etContraseña;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_correo);

        auth = FirebaseAuth.getInstance(); // Initialize Firebase Authentication
        etCorreo = findViewById(R.id.corr);
        etContraseña = findViewById(R.id.password);
        Button inicio = findViewById(R.id.iniciar_sesion);
        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inicioSesiónCorreo(v);
            }
        });

        TextView Registro = findViewById(R.id.Registrarte);
        Registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Registro.class);
                startActivity(i);
            }
        });
    }

    private void verificaSiUsuarioValidado() {
        if (auth.getCurrentUser() != null) {
            Intent i = new Intent(this, AppActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }
    private boolean verificaCampos() {
        String correo = etCorreo.getText().toString();
        String contraseña = etContraseña.getText().toString();

        if (correo.isEmpty()) {
            etCorreo.setError("Introduce un correo");
        } else if (!correo.matches(".+@.+[.].+")) {
            etCorreo.setError("Correo no válido");
        } else if (contraseña.isEmpty()) {
            etContraseña.setError("Introduce una contraseña");
        } else if (contraseña.length() < 6) {
            etContraseña.setError("Ha de contener al menos 6 caracteres");
        } else if (!contraseña.matches(".*[0-9].*")) {
            etContraseña.setError("Ha de contener un número");
        } else if (!contraseña.matches(".*[A-Z].*")) {
            etContraseña.setError("Ha de contener una letra mayúscula");
        } else {
            return true;
        }
        return false;
    }
    private void mensaje(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Error")
                .setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void inicioSesiónCorreo(View view) {
        if (verificaCampos()) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Iniciando sesión...");
            progressDialog.show();

            String correo = etCorreo.getText().toString();
            String contraseña = etContraseña.getText().toString();

            auth.signInWithEmailAndPassword(correo, contraseña)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Successfully logged in, perform further actions
                                verificaSiUsuarioValidado();
                            } else {
                                // Failed to log in, show an error message
                                String errorMessage = task.getException().getLocalizedMessage();
                                mensaje(errorMessage);
                            }
                        }
                    });
        }
    }
}
